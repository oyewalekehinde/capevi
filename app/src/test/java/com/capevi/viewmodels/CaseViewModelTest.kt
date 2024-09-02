// package com.capevi.viewmodels
//
// import androidx.arch.core.executor.testing.InstantTaskExecutorRule
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.test.TestCoroutineDispatcher
// import kotlinx.coroutines.test.runBlockingTest
// import kotlinx.coroutines.test.setMain
// import org.junit.Before
// import org.junit.Rule
// import org.junit.Test
// import org.junit.rules.TestRule
//
// class CaseViewModelTest {
//    @get:Rule
//    var rule: TestRule = InstantTaskExecutorRule()
//
//    private val testDispatcher = TestCoroutineDispatcher()
//    private lateinit var viewModel: CaseViewModel
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(testDispatcher)
//        viewModel = CaseViewModel()
//    }
//    @Test
//    fun `increment increases counter by 1`(): Unit = runBlockingTest {
//        // Arrange
//        val observer = Observer<Int> {}
//        viewModel.counter.observeForever(observer)
//
//        // Act
//        viewModel.increment()
//
//        // Assert
//        assertEquals(1, viewModel.counter.value)
//
//        // Clean up
//        viewModel.counter.removeObserver(observer)
//    }
//
// }
