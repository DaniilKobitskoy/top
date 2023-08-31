package com.fivegbet.fiveg.bets.esportedasrote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FootballRules : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_football_rules)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val titles = resources.getStringArray(R.array.football_rules)
        val descriptions = resources.getStringArray(R.array.rules_info)
        val images = resources.obtainTypedArray(R.array.football_image)

        val items = ArrayList<Item>()

        for (i in titles.indices) {
            val item = Item(images.getResourceId(i, -1), titles[i], descriptions[i])
            items.add(item)
        }

        images.recycle()

        val adapter = ItemAdapter(items)
        recyclerView.adapter = adapter
    }
}