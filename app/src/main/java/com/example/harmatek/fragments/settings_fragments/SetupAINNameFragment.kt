package com.example.harmatek.fragments.settings_fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.harmatek.MainActivity
import com.example.harmatek.R
import com.example.harmatek.SharedPreferencesEditor
import kotlinx.android.synthetic.main.fragment_setup_ain_name.*


class SetupAINNameFragment: Fragment() {

    private lateinit var v: View
    private lateinit var c: Context
    private var message = ""

    private var viewGroup: ViewGroup? = null

    private val sp = SharedPreferencesEditor()

    private val m = MainActivity()

    private var low = ""
    private var high = ""

    var seriesNumber: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_setup_ain_name, container, false)
        c = v.context
        sp.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setTitle(R.string.card_text_8)
        m.toolbar(toolbar, 18f)
        setup_threshold_button.setOnClickListener {
            inflateDialog(true)
        }
        inquiry_threshold_button.setOnClickListener {
            inflateDialog(null)
        }

        val adapter = ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, c.resources.getStringArray(R.array.ain_array))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_ain.adapter = adapter
        spinner_ain.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View?, position: Int, id: Long) {
                if (position >= 0) {
                    seriesNumber = position.toString()
                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
    }

    private fun inflateDialog(setup: Boolean?) {
        low = threshold_low_edit_text.text.toString()
        high = threshold_high_edit_text.text.toString()

        if (setup != null) {
            if (validateForm(setup)) {
                openDialog(seriesNumber, setup)
            }
        }
        else {
            openDialog(seriesNumber, setup)
        }

    }

    private fun validateForm(setup: Boolean): Boolean {
        when {
            setup -> {
                return when {
                    TextUtils.isEmpty(low) -> {
                        Toast.makeText(context!!, context!!.applicationContext.getText(R.string.enter_threshold_low), Toast.LENGTH_SHORT).show()
                        false
                    }
                    TextUtils.isEmpty(high) -> {
                        Toast.makeText(context!!, context!!.applicationContext.getText(R.string.enter_threshold_high), Toast.LENGTH_SHORT).show()
                        false
                    }
                    low.toFloat() >= high.toFloat() -> {
                        Toast.makeText(context!!, context!!.applicationContext.getText(R.string.incorrect_threshold), Toast.LENGTH_SHORT).show()
                        false
                    }
                    else -> true
                }
            }
            else -> return true
        }
    }

    private fun setupThreshold(channelNumber: String, setup: Boolean?) {
        if (m.checkPhoneNumber(sp)) {
            if (m.checkPermissions(c)) {
                message = when {
                    setup == null -> {
                        sp.getPassword() +
                                "AINR$channelNumber"
                    }
                    setup -> {
                        sp.getPassword() +
                                "AINR$channelNumber" +
                                "L$low" +
                                "H$high"
                    }
                    else -> {
                        sp.getPassword() +
                                "AINR$channelNumber" +
                                "DEL"
                    }
                }
                m.sendSMS(c, message, sp)
            }
            else {
                openDialog(channelNumber, setup)
            }
        }
    }

    private fun openDialog(seriesNumber: String, setup: Boolean?) {
        val builder = AlertDialog.Builder(c, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)
        dialogText.text = if (setup != null) {
            c.getText(R.string.confirm_send_message_2)
        }
        else {
            c.getText(R.string.confirm_send_message)
        }
        builder.setView(dialogView)
            .setPositiveButton(R.string.send) { _, _ ->
                val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(dialogView.windowToken, 0)
                setupThreshold(seriesNumber, setup)
            }
            .setNegativeButton(R.string.back) { _, _ ->
            }.show()
    }
}