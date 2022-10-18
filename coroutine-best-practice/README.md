## 协程简史

协程是 suspend-resume 的结构。

>即在其中一段程序尚未执行完成时就向另一段程序移交控制权，而且前者会保存自己的上下文，以便稍后恢复现场继续执行。

两个关键字 `yield/resume`

一个进程包含至少一个线程（主线程），一个线程里面有0个或多个协程，一个协程是以线程为宿主进行的计算活动。
协程一旦确定宿主线程，一般不会再更改。

进程是资源分配的基本单位，线程（内核态）是 CPU 调度的基本单位，协程对于 OS 来说是透明的。
协程被认为是用户态的线程，协程的调度由用户完成。 进程向自己所属线程开放内存空间，线程有自己的堆栈、程序计数器、寄存器数据。

一个线程消耗的内存一般在 MB 级别，而协程占用内存一般在几十到几百字节，线程上下文切换的成本在几十纳秒到几微秒间，当线程繁忙且数量众多时，这些切换会消耗绝大部分的CPU运算能力。

**多线程的方式存在诸多缺点：**

- 创建开销较大，线程调度时需要上下文切换；

- 数量受限，能启动的最大线程数受操作系统限制，这个限制对于后端应用的影响尤为明显；

- 平台受限，某些平台比如 JavaScript 不支持自定义线程，注意 WebWorker 是浏览器提供的 API 而非 JavaScript 的能力；

- 数据同步带来额外性能开销；

- 对象生命周期不一致造成的内存泄漏；

- 使用难度大，调试多线程、线程安全让人头大；

**在协程编程模式下，可以轻松有十几万协程，这是线程无法比拟的；**

```
fun main() {
    GlobalScope.launch(Dispatchers.Main) { // 在主线程中触发协程
        postItem(Item())
    }
    Thread.sleep(100)
}

suspend fun postItem(item: Item) {
    val token = preparePost() // 暂停点1：获取签名
    val post = submitPost(token, item) // 暂停点2：发送网络请求
    processPost(post) // 处理结果
}

suspend fun preparePost() : Token = withContext(Dispatchers.IO) { ... }
suspend fun submitPost(t: Token, i: Item) : Post = withContext(Dispatchers.IO) { ... }
fun processPost(post: Post) { ... }
```

Kotlin 的 async/await 

```
fun postItem(item: Item) {
    GlobalScope.launch {
        // 获取签名
        val deferredToken: Deferred<Token> = async { preparePost() }
        val token: Token = deferredToken.await()

        // 发送网络请求
        val deferredPost: Deferred<Post> = async { submitPost(token, item) }
        val post = deferredPost.await()

        // 处理结果
        processPost(post)
    }
}

suspend fun preparePost() : Token = withContext(Dispatchers.IO) { ... }
suspend fun submitPost(t: Token, i: Item) : Post = withContext(Dispatchers.IO) { ... }
fun processPost(post: Post) { ... }
```

**Kotlin 没有借助 JVM 额外的魔法，是怎么实现协程的暂停和恢复的呢？**

>Kotlin 编译器会将上述 suspend 相关代码转换成状态机相关的代码。
> Kotlin 并未借助栈来实现协程的暂停和恢复，而是通过状态机和闭包来实现的，状态机和闭包作为局部变量是存储在堆上的，占用的内存空间更小。

Kotlin 协程的暂停和恢复同样离不开线程的切换，从某种程度上说，Kotlin 协程本质上是一个线程调度的框架。

**一旦启动一个协程，如何取消呢？不取消会产生资源泄漏的问题。**

>而 Kotlin 与 Swift 类似，是通过结构化并发的方式实现的。
>Kotlin 中的 Job，取消父协程会同步取消其子孙协程。








