## Kotlin by 关键字详解

1）Kotlin 的类委托

>目标类必须实现一个接口，委托类必须是目标类所实现接口的子类型。这是需要注意的。

现在有一个需求，统计向一个 HashSet 添加元素的次数，该怎么实现？

1，使用继承方式实现？ 

```
class InheritSolution<T>: HashSet<T>() {

    var objectAdded = 0

    override fun add(element: T): Boolean {
        objectAdded++
        return super.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        // 因为 super.addAll 内部调用了 add 方法，所以这里不必统计添加个数了。
        return super.addAll(elements)
    }
}
```

这有什么问题吗？

>当基类的实现被修改或者有新的方法被添加进来时，可能需要改变之前进行继承时的类行为，从而导致子类的行为不符合预期。

那我们该怎么办呢？除了采用继承的方式之外，我们还可以采用组合的方式。

>实际上，在建立新类时，应该优先考虑组合，因为它更加简单灵活。如果采用这种方式，设计会变得更加清晰。

```
class AssembleSolution<T> : MutableSet<T> {
	var objectAdded = 0
    private val innerSet = HashSet<T>()
    
    override fun add(element: T): Boolean {
        objectAdded++
        return innerSet.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        objectAdded += elements.size
        return innerSet.addAll(elements)
    }

    override fun clear() {
        innerSet.clear()
    }

    override fun iterator(): MutableIterator<T> {
        return innerSet.iterator()
    }

    override fun remove(element: T): Boolean {
        return innerSet.remove(element)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return innerSet.removeAll(elements)
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return innerSet.retainAll(elements)
    }

    override val size: Int
        get() = innerSet.size

    override fun contains(element: T): Boolean {
        return innerSet.contains(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return innerSet.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return innerSet.isEmpty()
    }
}
```

这种方式从代码设计上看确实好，相比于继承的实现方式不再耦合了，但是却需要非常多的模板代码，这点很烦人啊。

现在就该 Kotlin 的类委托出场了，它可以解决需要写非常多的模板代码的问题。

Kotlin 是如何帮我们减少了模板代码了呢？请反编译下面的代码看看，理解消化一下。

```
class DelegateSolution<T>(
    val innerSet: MutableSet<T> = HashSet<T>()
) : MutableSet<T> by innerSet {
    var objectAdded = 0
    override fun add(element: T): Boolean {
        objectAdded++
        return innerSet.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        objectAdded += elements.size
        return innerSet.addAll(elements)
    }
}
```

需要注意的是：

- DelegateSolution 必须实现一个接口，而不能继承于一个类；

- innerSet 的实际类型必须是 DelegateSolution 所实现接口的子类型；

2）委托属性

先看一个需求，需要对 name 和 lastname 赋值时，做一些格式化工作：
首字母大写其余字母小写，并统计格式化操作的次数，再获取 name 和 lastname 值的时候，把它们的 值的长度 和 值 用 `-` 拼接后返回。

please check Person.kt

委托属性的基本语法是这样的：

```
class Foo {
    var p: Type by Delegate()
}
```

等价于：

```
class Foo {
    private val delegate = Delegate()
    var p: Type
    	set(value: Type) = delegate.setValue(this, ..., value)
    	get() = delegate.getValue(this, ...)
}
```

##  Kotlin 内置的属性委托

`by lazy()` 函数用于实现属性的惰性初始化，即只有在第一次访问属性时，才对它进行初始化。

为什么 `lazy()` 函数可以放在 by 后面用于获取委托对象呢？

>这是因为 Lazy.kt 中定义了符合约定的扩展函数：

```
public inline operator fun <T> Lazy<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value
```

## Delegates.notNull()

`Delegates.notNull()` 用于实现属性的延迟初始化，和 `lateinit` 类似。

**它们的区别是：**

- notNull 会给每个属性额外创建一个对象，而 `lateinit` 不会；

- notNull 可以用于基本数据类型的延迟初始化，而 `lateinit` 不可以。

Ref:

https://blog.csdn.net/willway_wang/article/details/120795321




























