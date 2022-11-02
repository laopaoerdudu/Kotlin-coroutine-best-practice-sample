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

留意使用协程的单元测试代码，因为其执行可能是异步的，并且可能发生在多个线程中。

### TestDispatchers

>使协程的执行可预测

`TestDispatcher` 有两种可用的实现：`StandardTestDispatcher` 和 `UnconfinedTestDispatcher`，可分别对新启动的协程执行不同的调度。
两者都使用 `TestCoroutineScheduler` 来控制虚拟时间并管理测试中正在运行的协程。

一个测试中只能使用一个调度器实例，应共用该调度器。如果未指定，TestScope 将默认创建 `StandardTestDispatcher`，并将其用于运行顶级测试协程。

### StandardTestDispatcher

**可通过多种方式让出测试协程，以让排队的协程运行。所有以下调用都可在返回之前让其他协程在测试线程上运行：**

- `advanceUntilIdle`：在调度器上运行所有其他协程，直到队列中没有任何内容。这是一个不错的默认选择，可让所有待处理的协程运行，适用于大多数测试场景。

- `advanceTimeBy`：将虚拟时间提前指定时长，并运行已调度为在该虚拟时间点之前运行的所有协程。

- `runCurrent`：运行已调度为在当前虚拟时间运行的协程。

For example:

```
// 可使用 advanceUntilIdle 先让两个待处理的协程执行其工作，然后再继续执行断言：
@Test
fun standardTest() = runTest {
    // GIVEN
    val userRepo = UserRepository()

    // WHEN
    launch { userRepo.register("Alice") }
    launch { userRepo.register("Bob") }
    advanceUntilIdle() // Yields to perform the registrations

    // THEN
    assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ✅ Passes
}
```

### 注入测试调度程序

被测试代码可能会使用调度程序来切换线程（使用 `withContext`）或启动新协程。
在多个线程上并行执行代码时，测试可能会变得不稳定。如果在您无法控制的后台线程上运行代码，可能就难以在正确的时间执行断言或等待任务完成。

在测试中，请将这些调度程序替换为 TestDispatchers 的实例。这样做有几个好处：

- 代码将在单个测试线程上运行，让测试更具确定性

- 您可以控制新协程的调度和执行方式

- TestDispatchers 使用虚拟时间调度器，它可以自动跳过延迟，并允许您手动将时间提前

>注意：您可以在一个测试中创建和使用任意数量的 TestDispatchers，但它们必须共用同一个调度器。请注意不要创建多个调度器。

为了确保测试中只有一个调度器，请先创建 `MainDispatcherRule` 属性。
然后，根据需要在其他类级属性的初始化器中重复使用其调度程序（如果您需要不同类型的 TestDispatcher，则重复使用其调度器）。







