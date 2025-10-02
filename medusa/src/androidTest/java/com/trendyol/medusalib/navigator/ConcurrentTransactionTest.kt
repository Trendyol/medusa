package com.trendyol.medusalib.navigator

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.trendyol.medusalib.TestChildFragment
import com.trendyol.medusalib.TestParentFragment
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction
import com.trendyol.medusalib.navigator.transaction.TransactionType
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch


@RunWith(AndroidJUnit4::class)
class ConcurrentTransactionTest {

    @Test
    fun givenNavigatorWithShowAndHideWhenFragmentResetsTheCurrentTabAndStartsAnotherFragmentThenItMustNotThrowAnyExceptions() {
        // Given
        val transaction = CountDownLatch(1)
        var caughtException: Throwable? = null
        var navigator: Navigator? = null
        val scenario = launchFragmentInContainer<TestParentFragment>(
            initialState = Lifecycle.State.INITIALIZED
        )
        scenario.moveToState(Lifecycle.State.RESUMED)
        val rootFragment = TestChildFragment.newInstance("Root")
        val expectedFragment = TestChildFragment.newInstance("ExpectedFragment")

        scenario.withFragment { navigator = createNavigator(rootFragment = rootFragment) }
        scenario.moveToState(Lifecycle.State.RESUMED)

        // When
        rootFragment.onFragmentVisibleAgain = {
            resetTabAndStartFragment(navigator!!, expectedFragment)
                .onSuccess { transaction.countDown() }
                .onFailure {
                    caughtException = it
                    transaction.countDown()
                }
        }
        scenario.startAndDismissAFragment(navigator!!)

        transaction.await()
        caughtException?.let { throw it }
        scenario.moveToState(Lifecycle.State.RESUMED)
        onView(withText("ExpectedFragment")).check(matches(isDisplayed()))
    }

    private fun FragmentScenario<TestParentFragment>.startAndDismissAFragment(navigator: Navigator) {
        onFragment { navigator.start(TestChildFragment.newInstance("SecondFragment")) }
        moveToState(Lifecycle.State.RESUMED)
        onFragment { navigator.goBack() }
    }

    private fun resetTabAndStartFragment(
        navigator: Navigator,
        expectedFragment: TestChildFragment
    ): Result<Unit> {
        return runCatching {
            navigator.resetCurrentTab(true)
            navigator.start(expectedFragment)
        }
    }

    private fun TestParentFragment.createNavigator(rootFragment: TestChildFragment): MultipleStackNavigator {
        return MultipleStackNavigator(
            fragmentManager = this.childFragmentManager,
            containerId = TestParentFragment.CONTAINER_ID,
            rootFragmentProvider = listOf({ rootFragment }),
            navigatorConfiguration = NavigatorConfiguration(
                defaultNavigatorTransaction = NavigatorTransaction(TransactionType.SHOW_HIDE)
            )
        ).apply { this.initialize(null) }
    }
}