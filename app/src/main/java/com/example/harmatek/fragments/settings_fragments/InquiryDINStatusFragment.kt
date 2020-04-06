package com.example.harmatek.fragments.settings_fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.harmatek.MainActivity
import com.example.harmatek.R
import com.example.harmatek.SharedPreferencesEditor
import kotlinx.android.synthetic.main.fragment_inquiry_din_status.*


class InquiryDINStatusFragment: Fragment() {

    private val m = MainActivity()
    private var viewGroup: ViewGroup? = null

    private lateinit var v: View
    private lateinit var c: Context
    private var message = ""

    private val sp = SharedPreferencesEditor()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_inquiry_din_status, container, false)
        c = v.context
        sp.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setTitle(R.string.card_text_7)
        m.toolbar(toolbar, 18f)
        inquiry_din_status_button.setOnClickListener {
            openDialog()
        }
    }

    private fun inquiryDINStatus() {
        if (m.checkPhoneNumber(sp)) {
            if (m.checkPermissions(c)) {
                message = sp.getPassword() +
                        "DINE"
                m.sendSMS(c, message, sp)
            }
            else {
                openDialog()
            }
        }
    }

    private fun openDialog() {
        val builder = AlertDialog.Builder(c, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)
        dialogText.text = c.getText(R.string.confirm_send_message)
        builder.setView(dialogView)
            .setPositiveButton(R.string.send) { _, _ ->
                inquiryDINStatus()
            }
            .setNegativeButton(R.string.back) { _, _ ->
            }.show()
    }
}