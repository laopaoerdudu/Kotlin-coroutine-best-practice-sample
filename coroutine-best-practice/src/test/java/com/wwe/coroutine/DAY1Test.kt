package com.wwe.coroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean

/** runTest 会跟踪在 TestScope 的调度程序所使用的调度器上排队的协程，只要该调度器上还有待处理的工作，它就不会返回。 */
class DAY1Test {

    @Test
    fun testFetchData() = runTest {
        /**
         * 要点：runTest 在 TestScope 中运行测试协程，通过 TestDispatcher.
         * TestDispatchers 使用 TestCoroutineScheduler 来控制虚拟时间并在测试中调度新协程。
         * 测试中的所有 TestDispatchers 都必须使用同一调度器实例。
         *
         * runTest 默认使用 StandardTestDispatcher
         */
        val data = fetchData()
        assertEquals("Hello world", data)
    }

    suspend fun fetchData(): String {
        delay(1000L)
        return "Hello world"
    }

    @Test
    fun repoInitWorksAndDataIsHelloWorld() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = Repository(dispatcher)

        repository.initialize()
        advanceUntilIdle() // Runs the new coroutine
        assertEquals(true, repository.initialized.get())

        val data = repository.fetchData() // No thread switch, delay is skipped
        assertEquals("Hello world", data)
    }

    @Test
    fun repoInitWorks() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = BetterRepository(dispatcher)

        repository.initialize().join() // Suspends until the new coroutine is done
        assertEquals(true, repository.initialized.get())

        val data = repository.fetchData()
        assertEquals("Hello world", data)
    }

    @Test
    fun settingMainDispatcher() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            val viewModel = HomeViewModel()
            viewModel.loadMessage() // Uses testDispatcher, runs its coroutine eagerly
            assertEquals("Greetings!", viewModel.message.value)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

class Repository(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    private val scope = CoroutineScope(ioDispatcher)
    val initialized = AtomicBoolean(false)

    // A function that starts a new coroutine on the IO dispatcher
    fun initialize() {
        scope.launch {
            initialized.set(true)
        }
    }

    // A suspending function that switches to the IO dispatcher
    suspend fun fetchData(): String = withContext(ioDispatcher) {
        require(initialized.get()) { "Repository should be initialized first" }
        delay(500L)
        "Hello world"
    }
}

class BetterRepository(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    private val scope = CoroutineScope(ioDispatcher)
    val initialized = AtomicBoolean(false)

    fun initialize() = scope.launch {
        initialized.set(true)
    }

    suspend fun fetchData(): String = withContext(ioDispatcher) {
        require(initialized.get()) { "Repository should be initialized first" }
        delay(500L)
        "Hello world"
    }
}

class HomeViewModel : ViewModel() {
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> get() = _message

    fun loadMessage() {
        viewModelScope.launch {
            _message.value = "Greetings!"
        }
    }
}

class HomeViewModelTestUsingRule {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun settingMainDispatcher() = runTest { // Uses Main’s scheduler
        val viewModel = HomeViewModel()
        viewModel.loadMessage()
        advanceUntilIdle()
        assertEquals("Greetings!", viewModel.message.value)
    }
}

class DispatcherTypesTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun injectingTestDispatchers() = runTest { // Uses Main’s scheduler
        // Use the UnconfinedTestDispatcher from the Main dispatcher
        val unconfinedRepo = Repository(mainDispatcherRule.testDispatcher)

        // Create a new StandardTestDispatcher (uses Main’s scheduler)
       // val standardRepo = Repository(StandardTestDispatcher())

        unconfinedRepo.initialize()
        advanceUntilIdle()
        assertEquals(true, unconfinedRepo.initialized.get())

        val data = unconfinedRepo.fetchData()
        assertEquals("Hello world", data)
    }
}

/** 随意创建您自己的 TestDispatcher */
class RepositoryTest {
    // Creates the single test scheduler
    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository = Repository(testDispatcher)

    @Test
    fun someRepositoryTest() = runTest(testDispatcher.scheduler) {
        // Take the scheduler from the TestScope
        val newTestDispatcher = UnconfinedTestDispatcher(this.testScheduler)
        // Or take the scheduler from the first dispatcher, they’re the same
        val anotherTestDispatcher = UnconfinedTestDispatcher(testDispatcher.scheduler)

        // Test the repository...
    }
}

/** 创建您自己的 TestScope */
class ExampleTest {
    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)

    @Test
    fun someTest() = testScope.runTest {
        // ...
    }
}

