package com.appify.secretwindow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.appify.secretwindow.dataModels.SecretItem

class SecretsAdapter : RecyclerView.Adapter<SecretsAdapter.SecretsViewHolder>() {
    inner class SecretsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSecretSource: TextView = itemView.findViewById(R.id.tv_itemSource)
        val tvSecretText: TextView = itemView.findViewById(R.id.tv_itemText)
        val tvSecretTimestamp: TextView = itemView.findViewById(R.id.tv_itemTimestamp)
    }

    private val diffCallback = object : DiffUtil.ItemCallback<SecretItem>() {
        override fun areItemsTheSame(oldItem: SecretItem, newItem: SecretItem): Boolean {
            return oldItem.sourceName == newItem.sourceName
        }

        override fun areContentsTheSame(oldItem: SecretItem, newItem: SecretItem): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }
    }

    private val differList = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecretsViewHolder {
        return SecretsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_items_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SecretsViewHolder, position: Int) {
        val posData = differList.currentList[position]

        holder.tvSecretSource.text = posData.sourceName
        holder.tvSecretText.text = posData.text
        holder.tvSecretTimestamp.text =
            holder.itemView.context.getString(R.string.last_updated_text, posData.timestamp)
    }

    fun submitListToDiffer(givenList: List<SecretItem>) {
        differList.submitList(givenList)
    }

    override fun getItemCount(): Int {
        return differList.currentList.size
    }
}