package com.trendyol.medusa

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.trendyol.medusalib.navigator.Navigator
import com.trendyol.medusalib.navigator.NavigatorListener

class MainActivity : AppCompatActivity() {

    private var mTextMessage: TextView? = null

    lateinit var navigator: Navigator

    private val firstTabFragment = FragmentGenerator.generateNewFragment()
    private val secondTabFragment = FragmentGenerator.generateNewFragment()
    private val thirdTabFragment = FragmentGenerator.generateNewFragment()

    private val rootFragmentList: MutableList<Fragment> = ArrayList()


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                navigator.switchTab(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                navigator.switchTab(1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                navigator.switchTab(2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootFragmentList.add(firstTabFragment)
        rootFragmentList.add(secondTabFragment)
        rootFragmentList.add(thirdTabFragment)

        navigator = Navigator(
                supportFragmentManager,
                R.id.container,
                rootFragmentList,
                navigatorListener = object : NavigatorListener {
                    override fun onTabChanged(tabIndex: Int) {
                        Log.v("TEST", "Tab Changed : $tabIndex")
                    }
                })

        mTextMessage = findViewById<View>(R.id.message) as TextView?
        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
