## 什么是 `typealias`？

在 Kotlin 源码中遇到长签名的表达式都会使用 `typealias`，它的作用就是给类取一个别名。
当我们使用高阶函数、Lambda 表达式、具有长签名的表达式的时候，使用 `typealias` 会更好，举个例子代码如下所示。

```
inline fun requestData(type: Int, call: (code: Int, type: Int) -> Unit) {
   call(200, type)
}

typealias Callback = (code: Int, type: Int) -> Unit

inline fun  requestData(type: Int, call: Callback) {
   call(200, type)
}
```

