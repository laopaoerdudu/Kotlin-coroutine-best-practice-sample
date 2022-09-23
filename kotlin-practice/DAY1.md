
为了能够在 Java 中正常调用，因此添加了注解 `@JvmName` 更改函数名称，来解决这个问题。

>将生成的函数名称打乱，是为了防止方法重载冲突，或者 Java 意外调用。

Inline classes 是在 Kotlin 1.3 引入的 ，在 Kotlin 1.5 时进入了稳定版本，废弃了 inline 修饰符，引入了 `Value classes`。

现阶段 Value classes  和 Inline classes 一样，只能在构造函数中传入一个参数，参数需要用 val 声明，将来可以在构造函数中添加多个参数，
但是每个参数都需要用  val 声明。

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





































