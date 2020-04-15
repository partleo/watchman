package com.example.harmatek.fragments.other_fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.harmatek.MainActivity
import com.example.harmatek.MainActivity.Companion.MODIFY_PASSWORD_FRAGMENT_TAG
import com.example.harmatek.MainActivity.Companion.SETUP_10_USER_NUMBER_FRAGMENT_TAG
import com.example.harmatek.MainActivity.Companion.SETUP_AIN_NAME_FRAGMENT_TAG
import com.example.harmatek.MainActivity.Companion.SET_RTU_TIME_FRAGMENT_TAG
import com.example.harmatek.R
import com.example.harmatek.cardrecyclerview.*
import com.example.harmatek.fragments.settings_fragments.*


class MainFragment: Fragment(), CardClickListener {

    private lateinit var v: View
    private lateinit var c: Context
    private lateinit var cardList: ArrayList<Card>

    private lateinit var m: MainActivity
    private lateinit var toolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_main, container, false)
        c = v.context
        inflateCardList(c)
        m = MainActivity()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = v.rootView.findViewById(R.id.main_toolbar)
        toolbar.setTitle(R.string.app_name_caps)

        m.changeToolbarIcon(c, toolbar, R.drawable.harmatek_back_arrow, false)

        m.toolbar(toolbar, 24f)
        val autoFitRecyclerView = view.findViewById<CardRecyclerView>(R.id.auto_fit_recycler_view)
        autoFitRecyclerView.adapter = RecyclerViewAdapter(cardList, this)
    }

    private fun inflateCardList(c: Context) {
        cardList = arrayListOf(
            Card(c.getString(R.string.card_text_1)),
            Card(c.getString(R.string.card_text_3)),
            Card(c.getString(R.string.card_text_4)),
            Card(c.getString(R.string.card_text_8))
        )
    }

    override fun onCardClicked(holder: CardViewHolder, position: Int) {
        when (position) {
            0 -> setupFragment(ModifyPasswordFragment(), MODIFY_PASSWORD_FRAGMENT_TAG)
            1 -> setupFragment(SetRTUTimeFragment(), SET_RTU_TIME_FRAGMENT_TAG)
            2 -> setupFragment(Setup10UserNumberFragment(), SETUP_10_USER_NUMBER_FRAGMENT_TAG)
            3 -> setupFragment(SetupAINNameFragment(), SETUP_AIN_NAME_FRAGMENT_TAG)
            else -> Log.d("sms", "Exception: Too many positions!!")
        }
    }

    private fun setupFragment(fragment: Fragment, tag: String) {
        fragmentManager!!
            .beginTransaction()
            .setCustomAnimations(R.anim.item_animation_from_right, R.anim.item_animation_to_left, R.anim.item_animation_from_left, R.anim.item_animation_to_right)
            .replace(R.id.fragment_container, fragment, tag)
            .addToBackStack(null)
            .commit()
    }
}