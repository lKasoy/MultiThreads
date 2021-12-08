package com.example.multithreads.services

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.multithreads.databinding.FragmentItemBinding


class NumbersAdapter :
    ListAdapter<Int, NumbersAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result: Int = currentList[position]
        holder.bind(result)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Int>() {

        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(private val binding: FragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: Int) {
            binding.apply {
                number.text = result.toString()
            }
        }
    }
}