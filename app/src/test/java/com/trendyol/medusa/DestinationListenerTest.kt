package com.trendyol.medusa

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.trendyol.medusalib.navigator.MultipleStackNavigator
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DestinationListenerTest {


    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        FragmentGenerator.fragmentNumber = 0
    }

    @Test
    fun `given activity is resumed, when observeDestinationChanges is called, last destination must be emitted`() {
        val testObserver = TestObserver<Fragment>()
        launchActivity<MainActivity>().use { scenario ->
            // given
            scenario.moveToState(Lifecycle.State.RESUMED)

            // when
            scenario.onActivity { activity ->
                activity.doAndExecuteFragmentTransactions {
                    observeDestinationChanges(activity, testObserver)
                }

                // then
                Assert.assertEquals(
                    "fragment 1",
                    testObserver.values.single().getFragmentName()
                )
            }
        }
    }

    @Test
    fun `given navigator is at second tab, when switchTab is called with index 0, then last two observed values of destination listener must be 2 and 1`() {
        val testObserver = TestObserver<Fragment>()
        launchActivity<MainActivity>().use { scenario ->
            // Given
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { activity ->
                activity.doAndExecuteFragmentTransactions {
                    switchTab(1)
                    observeDestinationChanges(activity, testObserver)
                }

                // when
                activity.doAndExecuteFragmentTransactions {
                    switchTab(0)
                }

                // then
                Assert.assertEquals(
                    "fragment 1",
                    testObserver.getLastFragmentName(indexFromLast = 1)
                )
                Assert.assertEquals(
                    "fragment 2",
                    testObserver.getLastFragmentName(0)
                )
            }
        }
    }

    @Test
    fun `given a fragment is started after root fragment, when onBackPressed called, then fragment history must be 1-2-1`() {
        val testObserver = TestObserver<Fragment>()
        launchActivity<MainActivity>().use { scenario ->
            // Given
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { activity ->
                activity.doAndExecuteFragmentTransactions {
                    observeDestinationChanges(activity, testObserver)
                    start(FragmentGenerator.generateNewFragment())
                }

                activity.onBackPressed()
                activity.supportFragmentManager.executePendingTransactions()

                Assert.assertEquals("fragment 1", testObserver.getFragmentName(0))
                Assert.assertEquals("fragment 2", testObserver.getFragmentName(1))
                Assert.assertEquals("fragment 1", testObserver.getFragmentName(2))
            }
        }
    }

    @Test
    fun `given a fragment is started after root fragment, when switchTabIs called, then fragment history must be 1-2-3`() {
        val testObserver = TestObserver<Fragment>()
        launchActivity<MainActivity>().use { scenario ->
            // Given
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { activity ->
                activity.doAndExecuteFragmentTransactions {
                    observeDestinationChanges(activity, testObserver)
                    start(FragmentGenerator.generateNewFragment())
                }

                activity.doAndExecuteFragmentTransactions { switchTab(0) }

                Assert.assertEquals(
                    "fragment 1",
                    testObserver.getLastFragmentName(indexFromLast = 2)
                )
                Assert.assertEquals(
                    "fragment 2",
                    testObserver.getLastFragmentName(indexFromLast = 1)
                )
                Assert.assertEquals("fragment 3", testObserver.getLastFragmentName())
            }
        }
    }

    @Test
    fun `given a fragment is started after root fragment and switched another tab, when onBackPressed is called, then fragment history must be 1-2-3-2`() {
        val testObserver = TestObserver<Fragment>()
        launchActivity<MainActivity>().use { scenario ->
            // Given
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { activity ->
                activity.doAndExecuteFragmentTransactions {
                    observeDestinationChanges(activity, testObserver)
                    start(FragmentGenerator.generateNewFragment())
                    switchTab(0)
                }

                activity.onBackPressed()
                activity.supportFragmentManager.executePendingTransactions()

                Assert.assertEquals(
                    "fragment 1",
                    testObserver.getLastFragmentName(indexFromLast = 3)
                )
                Assert.assertEquals(
                    "fragment 2",
                    testObserver.getLastFragmentName(indexFromLast = 2)
                )
                Assert.assertEquals(
                    "fragment 3",
                    testObserver.getLastFragmentName(indexFromLast = 1)
                )
                Assert.assertEquals("fragment 2", testObserver.getLastFragmentName())
            }
        }
    }

    @Test
    fun `given all three tabs are visited, when onBackPressed is called twice, then fragment history must be 1-2-3-2-1`() {
        val testObserver = TestObserver<Fragment>()
        launchActivity<MainActivity>().use { scenario ->
            // Given
            scenario.moveToState(Lifecycle.State.RESUMED)

            scenario.onActivity { activity ->
                activity.multipleStackNavigator.observeDestinationChanges(activity, testObserver)
                activity.doAndExecuteFragmentTransactions {
                    switchTab(0)
                }
                activity.doAndExecuteFragmentTransactions {
                    switchTab(2)
                }

                activity.onBackPressed()
                activity.supportFragmentManager.executePendingTransactions()
                activity.onBackPressed()
                activity.supportFragmentManager.executePendingTransactions()

                Assert.assertEquals(
                    "fragment 1",
                    testObserver.getLastFragmentName(indexFromLast = 4)
                )
                Assert.assertEquals(
                    "fragment 2",
                    testObserver.getLastFragmentName(indexFromLast = 3)
                )
                Assert.assertEquals(
                    "fragment 3",
                    testObserver.getLastFragmentName(indexFromLast = 2)
                )
                Assert.assertEquals(
                    "fragment 2",
                    testObserver.getLastFragmentName(indexFromLast = 1)
                )
                Assert.assertEquals("fragment 1", testObserver.getLastFragmentName())
            }
        }
    }
    private fun MainActivity.doAndExecuteFragmentTransactions(run: MultipleStackNavigator.() -> Unit) =
        run.invoke(this.multipleStackNavigator).also { supportFragmentManager.executePendingTransactions() }

    private fun TestObserver<Fragment>.getLastFragmentName(indexFromLast: Int = 0) =
        values[values.lastIndex - indexFromLast].getFragmentName()

    private fun TestObserver<Fragment>.getFragmentName(index: Int) =
        values[index].getFragmentName()

    private fun Fragment.getFragmentName() =
        SampleFragment.from(this as SampleFragment)
}

