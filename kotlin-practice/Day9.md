
`lateinit var` 的作用也比较简单，就是让编译期在检查时不要因为属性变量未被初始化而报错。

而 `by lazy` 真正做到了声明的同时也指定了延迟初始化时的行为，在属性被第一次被使用的时候能自动初始化。

>`by lazy` 具体是怎么实现的：生成一个该属性的附加属性 `:nameXXdelegate`
在构造器中，将使用 `lazy(()->T)` 创建的 `Lazy` 实例对象赋值给 `nameXXdelegate`；
当该属性被调用，即其 `getter` 方法被调用时返回 `nameXXdelegate.getVaule()`，
>而 `nameXXdelegate.getVaule()` 方法的返回结果是对象 `nameXXdelegate` 内部的 `_value` 属性值，
>在 `getVaule()` 第一次被调用时会将 `_value` 进行初始化，往后都是直接将 `_value` 的值返回，从而实现属性值的唯一一次初始化。

```
// 用于属性延迟初始化
val name: Int by lazy { 1 }

// 用于局部变量延迟初始化
public fun foo() {
    val bar by lazy { "hello" }
    println(bar)
}
```

