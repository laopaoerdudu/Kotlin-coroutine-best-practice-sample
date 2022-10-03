
为了能够在 Java 中正常调用，因此添加了注解 `@JvmName` 更改函数名称，来解决这个问题。

>将生成的函数名称打乱，是为了防止方法重载冲突，或者 Java 意外调用。

Inline classes 是在 Kotlin 1.3 引入的 ，在 Kotlin 1.5 时进入了稳定版本，废弃了 inline 修饰符，引入了 `Value classes`。

现阶段 Value classes  和 Inline classes 一样，只能在构造函数中传入一个参数，参数需要用 val 声明，将来可以在构造函数中添加多个参数，
但是每个参数都需要用 val 声明。

升级到 Kotlin 1.5 之后，Inline classes 将被弃用，编译器将会给出警告。

根据提示目前唯一需要改变的是语法 inline 替换为 `value`, 然后再添加 `@JvmInline` 注解即可。
编译后的效果和 `Inline classes` 是一样的，因此后面的案例将会使用 Value classes。

```
@JvmInline
value class User(val name: String)
```

因为 value class 编译后将会添加 final 修饰符，因此不能被继承，同样也不能继承其他的类，但是可以实现接口。
当 value class 实现接口时，失去了内联效果，依然会在堆中创建 User 对象，失去了内联的效果。

```
interface LoginInterface

@JvmInline
value class User(val name: String) : LoginInterface

fun testInline() {
    println(User("DHL"))
}

// 编译后的代码
public static final void testInline() {
  User var0 = User.box-impl(User.constructor-impl("DHL"));
  System.out.println(var0);
}

public static final User box_impl/* $FF was: box-impl*/(String v) {
  return new User(v);
}
```

当构造函数的参数为基本数据类型，且传递的参数 value class 的对象为空时，将失去内联效果，会失去内联效果。

```
@JvmInline
value class User(val age: Int)

fun login(user: User?): Int = user?.age ?: 0

fun testInline() {
    println(login(User(10)))
}

// 编译后的代码
public static final int login_js0Jwf8/* $FF was: login-js0Jwf8*/(@Nullable User user) {
  return user != null ? user.unbox-impl() : 0;
}

public static final void testInline() {
  int var0 = login-js0Jwf8(User.box-impl(10));
  System.out.println(var0);
}

public static final User box_impl/* $FF was: box-impl*/(int v) {
  return new User(v);
}
```

当构造函数的参数为 String，且传递的参数 value class 的对象为空时，依然可以起到内联的效果。
编译后的 Java 代码并没有创建对象，传递给方法 login 的参数 User 被替换为传进去的值 String。

```
@JvmInline
value class User(val name: String)

fun login(user: User?): String = user?.name ?: ""

fun testInline() {
    println(login(User("DHL")))
}

// 编译后的代码
public static final String login_js0Jwf8/* $FF was: login-js0Jwf8*/(@Nullable String user) {
    // ......
  return var10000;
}

public static final void testInline() {
    String var0 = login-js0Jwf8("DHL");
    System.out.println(var0);
}
```

## 什么是 data class？

data class 表示数据类，编译器会根据主构造函数声明的参数，自动生成 equals() 、 hashCode() 、 toString() 、 componentN() 、 copy() 、 setXXX() 、 getXXX() 等等模板方法，
将我们从重复的劳动力解放出来了，专注于核心业务的实现。

## value class 和 data class 的区别

- value class 占用更少的内存，执行效率更高

- value class 执行效率比 data class 快

- value class 没有 copy() 方法

>value class 只能通过构造函数去创建对象，需要显示指定所有的参数

- value class 构造函数只能传入一个参数

>而 data class 支持在构造函数中添加多个参数，参数可以用 val 或者 var 声明。

- value class 为什么不能重写 equals() 、 hashcode() 方法

>value class 构造函数只能传入一个参数，而且必须用 val 进行修饰，所以不存在需要比较两个相同的参数场景，因此 Kotlin 不让重写 equals()  和 hashcode() 方法。

- value class 和 data class 都不能被继承

>类的加载过程：加载、验证、准备、解析、初始化等等阶段，之后会执行 <clinit>() 方法，初始化静态变量，执行静态代码块等等。
如果类已经初始化了，直接执行对象的创建过程;
>对象的创建过程：在堆内存中开辟一块空间，给开辟空间分配一个地址，之后执行初始化，会执行 <init>() 方法，初始化普通变量，调用普通代码块

```
@JvmInline
value class User(val name: String)
fun login(user: User?): String = user?.name ?: ""
println(login(User("DHL")))

// 编译后的代码
// 我们在实例化 User 的时候，并没有在堆中分配对象
String var0 = login-js0Jwf8("DHL");
System.out.println(var0);
```

### Value class 执行时间比 data class 快多少？

```
data class User1(val name: String)
fun printDataClass(user: User1) { }

@JvmInline
value class User2(val name: String)
fun printValueClass(user: User2) {}

@OptIn(ExperimentalTime::class)
fun main() {
   // data class
   val measureDataClass = measureTime {
       repeat(100) {
           User1("DHL")
      }
  }
   println("measure data class time ${measureDataClass.toDouble(TimeUnit.MILLISECONDS)} ms")

   // value class
   val measureRunValueClass = measureTime {
       repeat(100) {
           User2("DHL")
      }
  }
   println("measure value class time ${measureRunValueClass.toDouble(TimeUnit.MILLISECONDS)} ms")
}

// 测试结果
measure data class time 6.790241 ms
measure value class time 0.832866 ms
```










































