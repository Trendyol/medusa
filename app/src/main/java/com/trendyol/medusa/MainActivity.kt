package com.trendyol.medusa

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.trendyol.medusalib.navigator.MultipleStackNavigator
import com.trendyol.medusalib.navigator.Navigator
import com.trendyol.medusalib.navigator.NavigatorConfiguration
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction

class MainActivity : AppCompatActivity(), Navigator.NavigatorListener {

    lateinit var navigation: BottomNavigationView

    private val rootFragmentProvider: List<() -> Fragment> = listOf(
        { FragmentGenerator.generateNewFragment() },
        { FragmentGenerator.generateNewFragment() },
        { FragmentGenerator.generateNewFragment() }
    )

    private val newListOfFragments: List<() -> Fragment> = listOf(
        { FragmentGenerator.generateBrandNewFragments() },
        { FragmentGenerator.generateBrandNewFragments() },
        { FragmentGenerator.generateBrandNewFragments() }
    )

    val multipleStackNavigator: MultipleStackNavigator =
        MultipleStackNavigator(
            supportFragmentManager,
            R.id.fragmentContainer,
            rootFragmentProvider,
            navigatorListener = this,
            navigatorConfiguration = NavigatorConfiguration(1, true, NavigatorTransaction.SHOW_HIDE))

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                multipleStackNavigator.switchTab(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                multipleStackNavigator.switchTab(1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                multipleStackNavigator.switchTab(2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation = findViewById<View>(R.id.navigation) as BottomNavigationView

        multipleStackNavigator.initialize(savedInstanceState)
        val restartRootFragmentCheckBox = findViewById<View>(R.id.restartSwitch) as SwitchCompat
        findViewById<Button>(R.id.resetCurrentTab).setOnClickListener { multipleStackNavigator.resetCurrentTab(restartRootFragmentCheckBox.isChecked) }
        findViewById<Button>(R.id.resetXTab).setOnClickListener { multipleStackNavigator.reset(1, restartRootFragmentCheckBox.isChecked) }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        findViewById<Button>(R.id.reset).setOnClickListener { multipleStackNavigator.reset() }
        findViewById<Button>(R.id.resetWithNewSet).setOnClickListener {
            multipleStackNavigator.resetWithFragmentProvider(newListOfFragments)
        }

    }

    override fun onBackPressed() {
        if (multipleStackNavigator.canGoBack()) {
            multipleStackNavigator.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onTabChanged(tabIndex: Int) {
        when (tabIndex) {
            0 -> navigation.selectedItemId = R.id.navigation_home
            1 -> navigation.selectedItemId = R.id.navigation_dashboard
            2 -> navigation.selectedItemId = R.id.navigation_notifications
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_java_sample -> {
                startActivity(Intent(this, MainActivity2::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        multipleStackNavigator.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
