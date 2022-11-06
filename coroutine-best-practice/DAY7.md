## Coroutines & Patterns for work that shouldn't be cancelled

If you’re creating your own `CoroutineScope`, make sure you tie it to a Job and call cancel when needed.

### Coroutines best practices

Inject `Dispatchers` into classes

>Don’t hardcode them when creating new coroutines or calling `withContext`.


### Operations that shouldn't be cancelled in Coroutines

please check the following code?

```
class MyViewModel(private val repo: Repository) : ViewModel() {
  fun callRepo() {
    viewModelScope.launch {
      repo.doWork()
    }
  }
}
class Repository(private val ioDispatcher: CoroutineDispatcher) {
  suspend fun doWork() {
    withContext(ioDispatcher) {
      doSomeOtherWork()
      veryImportantOperation() // This shouldn’t be cancelled
    }
  }
}
```

We don’t want `veryImportantOperation()` to be controlled by `viewModelScope` as it could be cancelled at any point.
We want that operation to outlive `viewModelScope`. How can we achieve that?

```
class MyApplication : Application() {
  // No need to cancel this scope as it'll be torn down with the process
  val applicationScope = CoroutineScope(SupervisorJob() + otherConfig)
}
```

We don’t need to cancel this scope since we want it to remain active as long as the application process is alive, 
so we don’t hold a reference to the `SupervisorJob`. 
We can use this scope to run coroutines that need a longer lifetime than the calling scope might offer in our app.

Whenever you create a new Repository instance, pass in the `applicationScope` we created above.

solution 1:

```
class Repository(
  private val externalScope: CoroutineScope,
  private val ioDispatcher: CoroutineDispatcher
) {
  suspend fun doWork() {
    withContext(ioDispatcher) {
      doSomeOtherWork()
      externalScope.launch {
        // if this can throw an exception, wrap inside `try/catch`
        // or rely on a `CoroutineExceptionHandler` installed in the externalScope's CoroutineScope
        veryImportantOperation()
      }.join()
    }
  }
}
```

solution 2:

```
class Repository(
  private val externalScope: CoroutineScope,
  private val ioDispatcher: CoroutineDispatcher
) {
  suspend fun doWork(): Any { // Use a specific type in Result
    withContext(ioDispatcher) {
      doSomeOtherWork()
      return externalScope.async {
        // Exceptions are exposed when calling await, they will be
        // propagated in the coroutine that called doWork. Watch
        // out! They will be ignored if the calling context cancels.
        veryImportantOperation()
      }.await()
    }
  }
}
```

In any case, the ViewModel code does’t change and with the above, even if the `viewModelScope` gets destroyed, 
the work using externalScope will keep running. 
Furthermore, doWork() won’t return until veryImportantOperation() completes.

❌ GlobalScope
There are multiple reasons why you shouldn't use `GlobalScope`:

- Promotes hard-coding values. It might be tempting to hardcode Dispatchers if you use GlobalScope straight-away. 
That’s a bad practice!

- It makes testing very hard. As your code is going to be executed in an uncontrolled scope, 
you won’t be able to manage execution of work started by it.

**Recommendation: Don’t use it directly.**

Notice that the default CoroutineContext of this scope uses `Dispatchers.Main.immediate` which might not be desirable for background work. 
As with `GlobalScope`, you’d have to pass a common `CoroutineContext` to all coroutines started by GlobalScope.

## Notes:

**Whenever you need some work to run beyond its current scope, 
we recommend creating a custom scope in your Application class and running coroutines within it. 
Avoid using `GlobalScope`, `ProcessLifecycleOwner` scope and `NonCancellable` for this type of work.**