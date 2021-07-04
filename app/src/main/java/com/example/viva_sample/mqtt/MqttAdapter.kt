package com.example.viva_sample.mqtt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viva_sample.databinding.ItemChattingBinding
import com.orhanobut.logger.Logger

class MqttAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mqttList: MutableList<ChatItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemChattingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MqttHolder(binding)
    }

    fun add(chatItem: ChatItem) {
        Logger.d("## add ==> ${chatItem.toString()}")
        mqttList.add(chatItem)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MqttHolder -> {
                holder.bind(mqttList)
            }
            else -> {
                throw Exception("You should not attach here.")
            }
        }

    }

    override fun getItemCount(): Int {
        Logger.d("## getItemCount ==>${mqttList.size}")
        return mqttList.size
    }

    inner class MqttHolder(private val binding: ItemChattingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chattList: List<ChatItem>) {
            Logger.d("## bind ==> ${chattList}")
            val item: ChatItem = chattList[adapterPosition]


            binding.item = item

        }
    }
}