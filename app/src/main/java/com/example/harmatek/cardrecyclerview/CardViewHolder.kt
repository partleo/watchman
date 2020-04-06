package com.example.harmatek.cardrecyclerview

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import com.example.harmatek.R


class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val listLayout: Button = itemView.findViewById(R.id.list_layout_button)
    var view: View = itemView

    private var duration: Long = 200

    fun bindItems(card: Card) {
        listLayout.text = card.title
    }

    fun animationScaleIn(view: View, position: Int) {
        val animationScaleIn = ScaleAnimation(
            0.0f,
            1.0f,
            0.0f,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animationScaleIn.duration = duration + ((duration*position)/2)
        view.startAnimation(animationScaleIn)
    }
}