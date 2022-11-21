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

**But, what’s the compiler actually doing under the hood when we mark the function as `suspend`?**

### Continuation interface

The way suspend functions communicate with each other is with `Continuation` objects.
A Continuation is just a generic callback interface with some extra information. 
As we will see later, it will represent the generated state machine of a suspend function.

```
interface Continuation<in T> {
  public val context: CoroutineContext
  public fun resumeWith(value: Result<T>)
}
```

```
// UserRemoteDataSource.kt
suspend fun logUserIn(userId: String, password: String): User

// UserLocalDataSource.kt
suspend fun logUserIn(userId: String): UserDb

suspend fun loginUser(userId: String, password: String): User {
  val user = userRemoteDataSource.logUserIn(userId, password)
  val userDb = userLocalDataSource.logUserIn(user)
  return userDb
}
```

As you see, the Kotlin compiler is doing a lot for us!
From this suspend function, The compiler generated all of this for us:

```
fun loginUser(userId: String, password: String, completion: Continuation<Any?>) {
  val user = userRemoteDataSource.logUserIn(userId, password)
  val userDb = userLocalDataSource.logUserIn(user)
  completion.resume(userDb)
}

fun loginUser(userId: String?, password: String?, completion: Continuation<Any?>) {

    class LoginUserStateMachine(
        // completion parameter is the callback to the function that called loginUser
        completion: Continuation<Any?>
    ): CoroutineImpl(completion) {
        // objects to store across the suspend function
        var user: User? = null
        var userDb: UserDb? = null

        // Common objects for all CoroutineImpl
        var result: Any? = null
        var label: Int = 0

        // this function calls the loginUser again to trigger the 
        // state machine (label will be already in the next state) and 
        // result will be the result of the previous state's computation
        override fun invokeSuspend(result: Any?) {
            this.result = result
            loginUser(null, null, this)
        }
    }

    val continuation = completion as? LoginUserStateMachine ?: LoginUserStateMachine(completion)

    when(continuation.label) {
        0 -> {
            // Checks for failures
            throwOnFailure(continuation.result)
            // Next time this continuation is called, it should go to state 1
            continuation.label = 1
            // The continuation object is passed to logUserIn to resume 
            // this state machine's execution when it finishes
            userRemoteDataSource.logUserIn(userId!!, password!!, continuation)
        }
        1 -> {
            // Checks for failures
            throwOnFailure(continuation.result)
            // Gets the result of the previous state
            continuation.user = continuation.result as User
            // Next time this continuation is called, it should go to state 2
            continuation.label = 2
            // The continuation object is passed to logUserIn to resume 
            // this state machine's execution when it finishes
            userLocalDataSource.logUserIn(continuation.user, continuation)
        }
        2 -> {
            // Checks for failures
            throwOnFailure(continuation.result)
            // Gets the result of the previous state
            continuation.userDb = continuation.result as UserDb
            // Resumes the execution of the function that called this one
            continuation.cont.resume(continuation.userDb)
        }
        else -> throw IllegalStateException(...)
    }
}
```

`CoroutineExceptionHandler` 用于记录异常、显示某种类型的错误消息、终止和/或重新启动应用程序。

**注意协程抛出 CancellationException 并不会导致 App Crash 。**

**如果根协程或者scope中没有设置 `CoroutineExceptionHandler`，异常会被直接抛出。**

Ref:

https://medium.com/androiddevelopers/the-suspend-modifier-under-the-hood-b7ce46af624f













