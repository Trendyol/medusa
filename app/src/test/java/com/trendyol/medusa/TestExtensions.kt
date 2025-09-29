package com.trendyol.medusa

import androidx.fragment.app.Fragment
import com.trendyol.medusalib.navigator.MultipleStackNavigator

/**
 * Executes navigator operations and ensures fragment transactions are committed.
 * This is essential for testing as fragment transactions are asynchronous by default.
 */
fun MainActivity.doAndExecuteFragmentTransactions(run: MultipleStackNavigator.() -> Unit) =
    run.invoke(this.multipleStackNavigator).also { supportFragmentManager.executePendingTransactions() }

/**
 * Extracts the readable fragment name from a SampleFragment instance.
 * This is used for test assertions to verify the correct fragments are being navigated to.
 */
fun Fragment.getFragmentName(): String =
    SampleFragment.from(this as SampleFragment) ?: "unknown fragment"
