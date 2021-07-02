package com.example.viva_sample.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viva_sample.databinding.ItemHospitalBinding
import com.example.viva_sample.room.entitiy.HospitalEntity

class RoomAdapter(private val HospitalList: List<HospitalEntity>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemHospitalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BookmarkHolder -> {
                holder.bind(HospitalList)
            }
            else -> {
                throw Exception("You should not attach here.")
            }
        }

    }

    override fun getItemCount(): Int {
        return HospitalList.size
    }

    inner class BookmarkHolder(private val binding: ItemHospitalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookmarkList: List<HospitalEntity>) {
            val hospital: HospitalEntity = bookmarkList[adapterPosition]

            binding.item = hospital

        }
    }
}