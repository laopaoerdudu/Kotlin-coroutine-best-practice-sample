## Android 上的 Kotlin 协程

协程是一种并发设计模式，您可以在 Android 平台上使用它来简化异步执行的代码。

在 Android 上，协程有助于管理长时间运行的任务。

### 特点

- 轻量：您可以在单个线程上运行多个协程，因为 **协程支持挂起，不会使正在运行协程的 `线程` 阻塞**。挂起比阻塞节省内存，且支持多个并行操作。

- 内存泄漏更少：使用结构化并发机制在一个作用域内执行多项操作。

- 内置取消支持：取消操作会自动在运行中的整个协程层次结构内传播。

- Jetpack 集成：许多 Jetpack 库都包含提供全面协程支持的扩展。某些库还提供自己的协程作用域，可供您用于结构化并发。

### Best practice

```
class LoginRepository(...) {
    ...
    suspend fun makeLoginRequest(
        jsonBody: String
    ): Result<LoginResponse> {

        // Move the execution of the coroutine to the I/O dispatcher
        return withContext(Dispatchers.IO) {
            // Blocking network request code
        }
    }
}

class LoginViewModel(
    private val loginRepository: LoginRepository
): ViewModel() {

    fun login(username: String, token: String) {

        // Create a new coroutine on the UI thread
        viewModelScope.launch {
            val jsonBody = "{ username: \"$username\", token: \"$token\"}"

            // Make the network call and suspend execution until it finishes
            val result = try {
                loginRepository.makeLoginRequest(jsonBody)
            } catch(e: Exception) {
                Result.Error(Exception("Network request failed"))
            }
            
            // Display result of the network request to the user
            when (result) {
                is Result.Success<LoginResponse> -> // Happy path
                else -> // Show error in UI
            }
        }
    }
}
```

- `launch` 不接受 `Dispatchers.IO` 参数。如果您未将 `Dispatcher` 传递至 `launch`，则从 `viewModelScope` 启动的所有协程都会在主线程中运行。

>`launch` 在主线程上创建新协程，然后协程开始执行。

- 系统现在会处理网络请求的结果，以显示成功或失败界面。

>在协程内，调用 `loginRepository.makeLoginRequest()` 现在会挂起协程的进一步执行操作，直至 makeLoginRequest() 中的 withContext 块结束运行。
>`withContext` 块结束运行后，login() 中的协程在主线程上恢复执行操作，并返回网络请求的结果。






