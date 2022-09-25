inline 修饰符适用于以下情况：

- inline 修饰符适用于把函数作为另一个函数的参数，例如高阶函数 filter、map、joinToString 或者一些独立的函数 repeat

- inline 操作符适合和 reified 操作符结合在一起使用

- 如果函数体很短，使用 inline 操作符可以提高效率

## by lazy

by lazy 作用是懒加载，保证首次访问的时候才初始化 lambda 表达式中的代码， by lazy 有三种模式。

- `LazyThreadSafetyMode.NONE` 仅仅在单线程

- `LazyThreadSafetyMode.SYNCHRONIZED` 在多线程中使用

- `LazyThreadSafetyMode.PUBLICATION` 不常用

LazyThreadSafetyMode.SYNCHRONIZED 是默认的模式，多线程中使用，可以保证线程安全，但是会有 double check + lock 性能开销，
可以查看源码去看看。

如果是在主线程中使用，和初始化相关的逻辑，建议使用 `LazyThreadSafetyMode.NONE` 模式，减少不必要的开销。

