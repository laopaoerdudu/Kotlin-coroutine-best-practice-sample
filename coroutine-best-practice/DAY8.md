## Dispatchers.Main.immediate

`immediate` is used to execute the coroutine immediately without needing to re-dispatch the work to the appropriate thread.

`Dispatchers.Main` uses the Android `Looper.getMainLooper()` method to run code in the UI thread. 
That method is available in Instrumented Android tests but not in Unit tests.

### Unit Testing `viewModelScope`

Note that `Dispatchers.setMain` is only needed if you use `viewModelScope` or you hardcode `Dispatchers.Main` in your codebase.

`TestCoroutineDispatcher` is a dispatcher that gives us control of how coroutines are executed, 
being able to `pause/resume` execution and control its virtual clock. 
It was added as an experimental API in Kotlin Coroutines v1.2.1.
You can read more about it in the [documentation](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test).

Don’t use `Dispatchers.Unconfined` as a replacement of `Dispatchers.Main`, 
it will break all assumptions and timings for code that does use Dispatchers.Main. 
Since a unit test should run well in isolation and without any side effects, 
you should call `Dispatchers.resetMain()` and clean up the executor when the test finishes running.

