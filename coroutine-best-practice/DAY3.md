## 协程高级概念

借助 Kotlin 中的结构化并发机制，您可以定义用于启动一个或多个协程的 `coroutineScope`。

>CoroutineScope 会跟踪它使用 launch 或 async 创建的所有协程。
>您可以随时调用 scope.cancel() 以取消正在进行的工作（即正在运行的协程）。
>在 Android 中，某些 KTX 库为某些生命周期类提供自己的 CoroutineScope。
>例如，ViewModel 有 viewModelScope，Lifecycle 有 lifecycleScope。不过，与调度程序不同，CoroutineScope 不运行协程。

### Structured concurrency with async

```
suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    one.await() + two.await()
}
```

**Note:** 

>This way, if something goes wrong inside the code of the concurrentSum function, and it throws an exception, 
> all the coroutines that were launched in its scope will be cancelled.

但是，如果您需要创建自己的 CoroutineScope 以控制协程在应用的特定层中的生命周期，则可以创建一个如下所示的 CoroutineScope：

```
class ExampleClass {

    // Job and Dispatcher are combined into a CoroutineContext which
    // will be discussed shortly
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun exampleMethod() {
        // Starts a new coroutine within the scope
        scope.launch {
            // New coroutine that can call suspend functions
            fetchDocs()
        }
    }

    fun cleanUp() {
        // Cancel the scope to cancel ongoing coroutines work
        scope.cancel()
    }
}
```

使用 `viewModelScope` 时，会在 ViewModel 的 onCleared() 方法中自动为您取消作用域。

### Job 作业

Job 是协程的句柄。使用 `launch` 或 `async` 创建的每个协程都会返回一个 Job 实例，该实例是相应协程的 `唯一标识` 并管理其生命周期。
您还可以将 Job 传递给 CoroutineScope 以进一步管理其生命周期。

```
class ExampleClass {
    ...
    fun exampleMethod() {
        // Handle to the coroutine, you can control its lifecycle
        val job = scope.launch {
            // New coroutine
        }

        if (...) {
            // Cancel the coroutine started above, this doesn't affect the scope
            // this coroutine was launched in
            job.cancel()
        }
    }
}
```

## CoroutineContext

`CoroutineContext` 使用以下元素集定义协程的行为：

- Job：控制协程的生命周期。

- CoroutineDispatcher：将工作分派到适当的线程。

- CoroutineName：协程的名称，可用于调试。

- CoroutineExceptionHandler：处理未捕获的异常

```
class ExampleClass {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun exampleMethod() {
        // Starts a new coroutine on Dispatchers.Main as it's the scope's default
        val job1 = scope.launch {
            // New coroutine with CoroutineName = "coroutine" (default)
        }

        // Starts a new coroutine on Dispatchers.Default
        val job2 = scope.launch(Dispatchers.Default + "BackgroundCoroutine") {
            // New coroutine with CoroutineName = "BackgroundCoroutine" (overridden)
        }
    }
}
```
