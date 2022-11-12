## 如何写出可以取消的代码

- 定期调用挂起函数，检查是否取消

- 显式检查取消状态

## 在 finally 中释放资源

我们使用 try-catch 捕获 CancellationException 发现会产生父协程等待所有子协程完成后才能完成，
所以建议不用 try-catch 而是 try{…} finally{…} ，让父协程在被取消时正常执行终结操作:

## CancellationException

`CancellationException` 的真实实现是 j.u.c. 中的 CancellationException ：

```
public actual typealias CancellationException = java.util.concurrent.CancellationException
```

1, 未捕获的 Exception 只能由使用协程构建器的根协程产生。

2, 所有子协程都将异常的处理委托给他们的父协程，父协程也委托给它自身的父协程，直到委托给根协程处理。

3, 所以在子协程中的 CoroutineExceptionHandler 永远不会被使用。

如果协程没有配置用于处理异常的 Handler ，未捕获的异常将按以下方式处理:

- 如果 exception 是 CancellationException ，那么它将被忽略(因为这是取消正在运行的协程的假定机制)。

## 超时取消

使用了 `withTimeout` ，运行过程中会导致 Crash 。

有两种解决办法：

- 使用 `try{…} catch (ex: TimeoutCancellationException){…}` 代码块

- 在超时的情况下不是抛出异常而是返回 null 的 `withTimeoutOrNull` 函数


































