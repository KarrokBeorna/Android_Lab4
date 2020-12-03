package com.example.android_lab4

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import name.ank.lab4.BibDatabase
import java.io.InputStream
import java.io.InputStreamReader
import com.example.android_lab4.databinding.EntriesBinding
import name.ank.lab4.Keys

class Adapter(base: InputStream) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    private val database = BibDatabase(InputStreamReader(base))

    class ViewHolder(binding: EntriesBinding) : RecyclerView.ViewHolder(binding.root) {
        val author = binding.author
        val title = binding.title
        val year = binding.year
        val pages = binding.pages
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = EntriesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = database.getEntry(position % database.size())
        holder.author.text = "Author(s): " + entry.getField(Keys.AUTHOR)
        holder.title.text = "Title: " + entry.getField(Keys.TITLE)
        holder.year.text = "Year: " + entry.getField(Keys.YEAR)
        holder.pages.text = "Pages: " + entry.getField(Keys.PAGES)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
}