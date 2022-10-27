## 测试协程

`InstantTaskExecutorRule` 是一种 JUnit 规则，用于配置 `LiveData` 以同步执行每项任务。

`MainCoroutineScopeRule` 是此代码库中的自定义规则，用于将 `Dispatchers.Main` 配置为使用 `kotlinx-coroutines-test` 中的 `TestCoroutineDispatcher`。
这样一来，测试可以将用于测试的虚拟时钟拨快，并让代码可以使用单元测试中的 Dispatchers.Main。

```
@get:Rule
val coroutineScope =  MainCoroutineScopeRule()

@get:Rule
val instantTaskExecutorRule = InstantTaskExecutorRule()
```