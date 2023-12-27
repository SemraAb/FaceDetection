package com.samra.facedetection.ui.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.samra.facedetection.databinding.ItemResultBinding


class ResultAdapter : RecyclerView.Adapter<ResultAdapter.ResultHolder>() {
    class ResultHolder(val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultHolder {
        return ResultHolder(
            ItemResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                true
            )
        )
    }

    override fun getItemCount(): Int {
        return 13
    }

    override fun onBindViewHolder(holder: ResultHolder, position: Int) {
        holder.binding.testDate.text = " salam "
    }
}