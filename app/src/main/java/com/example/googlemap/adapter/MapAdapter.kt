package com.example.googlemap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googlemap.database.MyLocation
import com.example.googlemap.databinding.ItemRvBinding

class MapAdapter(var list:ArrayList<MyLocation>):RecyclerView.Adapter<MapAdapter.MyViewholder>() {
    inner class MyViewholder(var itemRvBinding: ItemRvBinding):RecyclerView.ViewHolder(itemRvBinding.root){
        fun onBind(myLocation: MyLocation){
            itemRvBinding.latitudeText.text = myLocation.latitude.toString()
            itemRvBinding.longtitudeText.text = myLocation.longtitude.toString()
            itemRvBinding.timeText.text = myLocation.time.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        return MyViewholder(
            ItemRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}