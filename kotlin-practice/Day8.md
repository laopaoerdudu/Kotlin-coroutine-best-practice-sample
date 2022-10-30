## Kotlin 中的 `inline`, `noinline` 和 `crossinline`

- `inline` 在编译时，会将此修饰符修饰的函数复制到调用处（称为内联），避免创建 `Function` 对象，以减少创建对象的内存开销；

- `noinline` 需要配合 `inline` 使用，使用在函数形参上，告诉编译器当前这个函数不能内联；

- `crossinline` 需要配合 `inline` 使用，告诉编译器不能使用 `return`，得使用 `return@label` 来返回到指定位置。
  （本身在 lambda 内部就是不能使用 `return` 的，而只能使用 `return@label` ）

## Notes

1，如果你写的是高阶函数，会有函数类型的参数，加上 inline 就对了；

2，`noinline` 不是作用于函数的，而是作用于函数的参数；

>当需要将函数类型的参数作为对象返回值的时候，如果不关闭内联，就无法使用 return 返回。

3，对于一个标记了 `inline` 的内联函数，你可以对它的任何一个或多个函数类型的参数添加 `noinline` 关键字；
