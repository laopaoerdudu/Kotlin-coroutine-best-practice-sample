## CoroutineScope

A CoroutineScope keeps track of any coroutine you create using `launch` or `async` (these are extension functions on CoroutineScope). 
The ongoing work (running coroutines) can be canceled by calling `scope.cancel()` at any point in time.

>In some platforms like Android, 
> there are KTX libraries that already provide a CoroutineScope in certain lifecycle classes such as `viewModelScope` and `lifecycleScope`.

## Job

A Job is a handle to a coroutine. For every coroutine that you create (by `launch` or `async`), 
it returns a `Job` instance that uniquely identifies the coroutine and manages its lifecycle.
As we saw above, you can also pass a `Job` to a `CoroutineScope` to keep a handle on its lifecycle.

**Job lifecycle**

>A Job can go through a set of states: New, Active, Completing, Completed, Cancelling and Cancelled.
While we don’t have access to the states themselves,
we can access properties of a Job: `isActive`, `isCancelled` and `isCompleted`.

**If the coroutine is in an active state, calling job.cancel() will move the job in the Cancelling state (isActive = false, isCancelled = true).
Once all children have completed their work the coroutine will go in the Cancelled state and isCompleted = true.**

## CoroutineContext

The `CoroutineContext` is a set of elements that define the behavior of a coroutine. It’s made of:

- Job — controls the lifecycle of the coroutine.

- CoroutineDispatcher — dispatches work to the appropriate thread.

- CoroutineName — name of the coroutine, useful for debugging.

- CoroutineExceptionHandler — handles uncaught exceptions, will be covered in Part 3 of the series.

**Note:** 

>`CoroutineContexts` can be combined using the `+` operator. 
As the `CoroutineContext` is a set of elements, 
a new `CoroutineContext` will be created with the elements on the right side of the plus overriding those on the left.
E.g. (Dispatchers.Main, “name”) + (Dispatchers.IO) = (Dispatchers.IO, “name”)

Some elements have default values: `Dispatchers.Default` is the default of CoroutineDispatcher 
and “coroutine” the default of CoroutineName.

### Making your coroutine work cancellable

All suspend functions from `kotlinx.coroutines` are cancellable: `withContext`, `delay` etc. 
So if you’re using any of them you don’t need to check for cancellation and stop execution or throw a `CancellationException`. 
But, if you’re not using them, to make your coroutine code cooperative we have two options:

- Checking `job.isActive` or `ensureActive()`

- Let other work happen using `yield()`

### Let other work happen using `yield()`

If the work you’re doing is facing below 3 cases, please use yield().

>`yield` will be checking for completion and exit the coroutine by throwing `CancellationException` if the job is already completed.

1) CPU heavy

2) may exhaust the thread pool

3) you want to allow the thread to do other work without having to add more threads to the pool

### `Job.join` together with `job.cancel`

`Job.join` suspends a coroutine until the work is completed. Together with `job.cancel` it behaves as you’d expect:

- If you’re calling `job.cancel` then `job.join`, the coroutine will `suspend` until the job is completed.

- Calling `job.cancel` after `job.join` has no effect, as the job is already completed.

### Deferred.await

Here’s why we get the exception: the role of `await` is to suspend the coroutine until the result is computed; 
since the coroutine is cancelled, the result cannot be computed. Therefore, calling await after cancel leads to JobCancellationException: Job was cancelled.

```
val deferred = async { … }
deferred.cancel()
val result = deferred.await() // throws JobCancellationException!
```

On the other hand, if you’re calling `deferred.cancel` after `deferred.await` nothing happens, as the coroutine is already completed.

### Try catch finally

```
val job = launch {
   try {
      work()
   } catch (e: CancellationException){
      println(“Work cancelled!”)
    } finally {
      println(“Clean up!”)
    }
}
delay(1000L)
println(“Cancel!”)
job.cancel()
println(“Done!”)
```

But, if the **cleanup work** we need to execute is suspending, the code above won’t work anymore, 
as once the coroutine is in Cancelling state, it can’t suspend anymore.

**Bad practice for suspend Clean up work!**

```
val job = launch {
   try {
      work()
   } catch (e: CancellationException){
      println(“Work cancelled!”)
    } finally {
      launch {
        println(“Clean up!”)
      }
    }
}
delay(1000L)
println(“Cancel!”)
job.cancel()
println(“Done!”)
```

**Good practice for suspend Clean up work!**

>To be able to call suspend functions when a coroutine is cancelled, 
>we will need to switch the cleanup work we need to do in a `NonCancellable` CoroutineContext. 
> This will allow the code to suspend and will keep the coroutine in the Cancelling state until the work is done.

```
val job = launch {
   try {
      work()
   } catch (e: CancellationException){
      println(“Work cancelled!”)
    } finally {
      // Recommendation: use it ONLY for suspending cleanup code.
      withContext(NonCancellable){
         delay(1000L) // or some other suspend fun 
         println(“Cleanup done!”)
      }
    }
}
delay(1000L)
println(“Cancel!”)
job.cancel()
println(“Done!”)
```

**Note: A coroutine in the cancelling state is not able to suspend!**

### `suspendCancellableCoroutine` and `invokeOnCancellation`

If you converted callbacks to coroutines by using the `suspendCoroutine` method, 
then prefer using `suspendCancellableCoroutine` instead. 
The work to be done on cancellation can be implemented using `continuation.invokeOnCancellation`:

```
suspend fun work() {
   return suspendCancellableCoroutine { continuation ->
       continuation.invokeOnCancellation { 
          // do cleanup
       }
   // rest of the implementation
}
```

#### Notes

Use the CoroutineScopes defined in Jetpack: viewModelScope or lifecycleScope that cancels their work when their scope completes. 
If you’re creating your own CoroutineScope, make sure you’re tying it to a job and calling cancel when needed.

