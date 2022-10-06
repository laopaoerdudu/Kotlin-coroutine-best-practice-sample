## Kotlin 和 Java 的协变

比如 `? extends Number`，只要声明时传入的类型是 Number 或者 Number 的子类型都可以。

### 协变应用

下面的代码是等价的，是可以正常编译的。

```
// kotlin
val numbers: MutableList<out Number> = ArrayList<Int>()

// Java
List<? extends Number> numbers = new ArrayList<Integer>();
```

无法添加元素，只能获取元素。

协变只能读取数据，不能添加数据。

在 Kotlin 中一个协变类，参数前面加上 out 修饰后，这个参数在当前类中 只能作为函数的返回值，或者修饰只读属性。

```
interface ProduceExtends<out T> {
   val num: T          // 用于只读属性
   fun getItem(): T    // 用于函数的返回值
}
```

### 逆变应用

逆变只能添加数据，不能按照泛型读取数据。

在 Kotlin 中一个逆变类，参数前面加上 in 修饰后，这个参数在当前类中 只能作为函数的参数，或者修饰可变属性。

```
interface ConsumerSupper<in T> {
   fun addItem(t: T)
}
```

































