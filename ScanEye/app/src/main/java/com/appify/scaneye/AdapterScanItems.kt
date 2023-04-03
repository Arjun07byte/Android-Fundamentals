package com.appify.scaneye

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.appify.scaneye.dataModels.DateTimeHelper
import com.appify.scaneye.dataModels.ScanHistoryItem

class AdapterScanItems : RecyclerView.Adapter<AdapterScanItems.AdapterScanViewHolder>() {
    class AdapterScanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQrTitle: TextView = itemView.findViewById(R.id.tv_qrTitle)
        val tvQrDesc: TextView = itemView.findViewById(R.id.tv_qrDesc)
        val tvQrTime: TextView = itemView.findViewById(R.id.tv_qrTime)
    }

    private val differCallBack = object : DiffUtil.ItemCallback<ScanHistoryItem>() {
        override fun areItemsTheSame(oldItem: ScanHistoryItem, newItem: ScanHistoryItem): Boolean {
            return oldItem.historyItemTime == newItem.historyItemTime
        }

        override fun areContentsTheSame(
            oldItem: ScanHistoryItem,
            newItem: ScanHistoryItem
        ): Boolean {
            return oldItem.historyItemDesc == newItem.historyItemDesc
        }
    }

    private val differList = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterScanViewHolder {
        return AdapterScanViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_items_history,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AdapterScanViewHolder, position: Int) {
        val posData = differList.currentList[position]
        holder.tvQrTitle.text = posData.historyItemTitle.ifEmpty {
            posData.historyItemType
        }
        holder.tvQrDesc.text = posData.historyItemDesc
        when (posData.scanDateKey) {
            0, -1 -> {
                holder.tvQrTime.text = holder.itemView.context.getString(
                    R.string.visited_at_text,
                    DateTimeHelper.getOnlyTime(posData.historyItemTime) ?: "%23s"
                )
            }
            else -> {
                holder.tvQrTime.text = holder.itemView.context.getString(
                    R.string.visited_at_text_with_line_br,
                    posData.historyItemTime
                )
            }
        }
    }

    fun submitListToDiffer(givenList: List<ScanHistoryItem>) {
        differList.submitList(givenList)
    }

    override fun getItemCount(): Int {
        return differList.currentList.size
    }
}