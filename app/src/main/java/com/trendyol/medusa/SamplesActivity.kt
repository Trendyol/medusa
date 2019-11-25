package com.trendyol.medusa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by erkut.aras on 2019-11-25.
 */
class SamplesActivity : AppCompatActivity() {

    private val samples = listOf(
        Pair<String, Class<*>>("MultipleStackNavigator - Kotlin", MainActivity::class.java),
        Pair<String, Class<*>>("MultipleStackNavigator - Java", MainActivity2::class.java)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_samples)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@SamplesActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(this@SamplesActivity, LinearLayoutManager.VERTICAL))
            adapter = SamplesAdapter(samples) {
                startActivity(Intent(this@SamplesActivity, it))
            }
            (adapter as SamplesAdapter).notifyDataSetChanged()
        }
    }
}