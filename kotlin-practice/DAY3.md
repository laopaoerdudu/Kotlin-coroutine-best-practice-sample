## 什么是侧泛型？

- 泛型类，或泛型接口的声明

```
public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}
```

**思考：Retrofit 是如何传递泛型信息的？**

- 带有泛型参数的方法

```
public List<String> parse(String jsonStr) {
    List<String> topNews =  new Gson().fromJson(jsonStr, new TypeToken<List<String>>() {}.getType());
    return topNews;
}
```

**思考：Gson 是怎么获取泛型类型的？**

## 什么是 PECS 原则？

1，如果你只是从集合中读数据，你应该用 `extend`

2，如果你只是往集合中写数据，你应该用 `super`

3，如果你往集合中既读又写，那么你不应该用 `extend` 或者 `super`

使用 PECS 主要是为了实现 `集合的多态`。

```
public static void getOutFruits(List<? extends Fruit> basket) {
    for (Fruit fruit : basket) {
        System.out.println(fruit);
        ...
    }
}
```

在 `List<? extends Fruit>` 的泛型集合中，对于元素的类型，编译器只能知道元素是继承自 `Fruit`，具体是 `Fruit` 的哪个子类是无法知道的。
所以向一个无法知道具体类型的泛型集合中插入元素是不能通过编译的。
但是由于知道元素是继承自 `Fruit`，所以从这个泛型集合中取 `Fruit` 类型的元素是可以的。

在 `List<? super Apple>` 的泛型集合中，元素的类型是 Apple 的父类，但无法知道是哪个具体的父类， 因此读取元素时无法确定以哪个父类进行读取。
插入元素时可以插入 Apple 与 Apple 的子类，因为这个集合中的元素都是 Apple 的父类，子类型是可以赋值给父类型的。

总得来说，`List<Fruit>` 和 `List<Apple>` 之间没有任何继承关系。
API的参数想要同时兼容2者，则只能使用 `PECS` 原则。这样做提升了API的灵活性，实现了泛型集合的多态。

遵循这个原则的好处是，可以在编译阶段保证代码安全，减少未知错误的发生。

Please ref: 

https://mp.weixin.qq.com/s/hRSLXfakXPhGdKaCMQFHfw










































