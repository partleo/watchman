package com.example.harmatek.cardrecyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.harmatek.R


class RecyclerViewAdapter(private val cardList: ArrayList<Card>, private val mListener: CardClickListener) : RecyclerView.Adapter<CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_layout_button, parent, false)
        return CardViewHolder(v)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindItems(cardList[position])
        holder.listLayout.setOnClickListener {
            mListener.onCardClicked(holder, position)
        }
        holder.animationScaleIn(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
}
