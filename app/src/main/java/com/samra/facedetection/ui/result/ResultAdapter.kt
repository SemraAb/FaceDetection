package com.samra.facedetection.ui.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samra.facedetection.common.utils.GenericDiffUtil
import com.samra.facedetection.data.local.Result
import com.samra.facedetection.databinding.ItemResultBinding


class ResultAdapter() : ListAdapter<Result, RecyclerView.ViewHolder>(GenericDiffUtil<Result>(
    myItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    myContentsTheSame = { oldItem, newItem -> oldItem == newItem }
)) {
    var correctResult: Float = 0F
    inner class ViewHolder(private val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Result) {
            with(binding) {
                incorrectTest.text = item.testText
                if(item.testResult){
                    correctResult++
                }
                ratingBar.rating = correctResult
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position))
    }
}