package com.example.android_lab4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_lab4.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        val manager = LinearLayoutManager(this)

        binding.recyclerView.apply {
            addItemDecoration(DividerItemDecoration(context, manager.orientation))
            layoutManager = manager
            adapter = Adapter(resources.openRawResource(R.raw.articles))
        }

        setContentView(binding.root)
    }
}