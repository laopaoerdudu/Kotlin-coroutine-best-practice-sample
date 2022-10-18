## 什么是泛型的可空性？

```
// 3 种声明等价
class Forest<T>
 
class Forest<T : Any?>
 
class Forest<T : Tree?>
```

**Note: null 在 Kotlin 中的类型为 Nothing?（Kotlin 中一切皆为对象）**