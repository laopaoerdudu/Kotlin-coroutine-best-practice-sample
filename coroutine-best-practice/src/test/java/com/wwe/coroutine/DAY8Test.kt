package com.wwe.coroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class DAY8Test {

    @get:Rule
    var coroutinesTestRule = MainDispatcherRule()

    @Test
    fun test() = runTest(coroutinesTestRule.testDispatcher) {
        // ...
        //  Calling `runTest` will make that coroutine to execute synchronously in the test.
    }
}

class MainViewModel(private val dependency: Any) : ViewModel() {

    fun sampleMethod() {
        viewModelScope.launch {
            val hashCode = dependency.hashCode()
            // TODO: do something with hashCode
        }
    }
}
