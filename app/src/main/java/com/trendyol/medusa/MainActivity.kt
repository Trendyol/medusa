package com.trendyol.medusa

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.trendyol.medusalib.navigator.MultipleStackNavigator
import com.trendyol.medusalib.navigator.Navigator
import com.trendyol.medusalib.navigator.NavigatorConfiguration
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction

class MainActivity : AppCompatActivity(), Navigator.NavigatorListener {

    private var mTextMessage: TextView? = null

    lateinit var navigation: BottomNavigationView

    var multipleStackNavigator: MultipleStackNavigator? = null

    private val firstTabFragment = FragmentGenerator.generateNewFragment()
    private val secondTabFragment = FragmentGenerator.generateNewFragment()
    private val thirdTabFragment = FragmentGenerator.generateNewFragment()

    private val rootFragmentList: MutableList<Fragment> = ArrayList()


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                multipleStackNavigator!!.switchTab(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                multipleStackNavigator!!.switchTab(1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                multipleStackNavigator!!.switchTab(2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation = findViewById<View>(R.id.navigation) as BottomNavigationView

        rootFragmentList.add(firstTabFragment)
        rootFragmentList.add(secondTabFragment)
        rootFragmentList.add(thirdTabFragment)


        multipleStackNavigator = MultipleStackNavigator(
            supportFragmentManager,
            R.id.fragmentContainer,
            rootFragmentList,
            navigatorListener = this,
            navigatorConfiguration = NavigatorConfiguration(1, true, NavigatorTransaction.SHOW_HIDE))


        mTextMessage = findViewById<View>(R.id.message) as TextView?
        val restartRootFragmentCheckBox = findViewById<View>(R.id.restartSwitch) as SwitchCompat
        findViewById<Button>(R.id.resetCurrentTab).setOnClickListener { multipleStackNavigator!!.resetCurrentTab(restartRootFragmentCheckBox.isChecked) }
        findViewById<Button>(R.id.resetXTab).setOnClickListener { multipleStackNavigator!!.reset(1, restartRootFragmentCheckBox.isChecked) }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        findViewById<Button>(R.id.reset).setOnClickListener { multipleStackNavigator!!.reset() }
    }

    override fun onBackPressed() {
        if (multipleStackNavigator!!.canGoBack()) {
            multipleStackNavigator!!.goBack()
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
}
