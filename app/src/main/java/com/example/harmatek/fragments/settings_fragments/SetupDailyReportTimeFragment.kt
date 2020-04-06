package com.example.harmatek.fragments.settings_fragments

import android.app.TimePickerDialog
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
import android.widget.TextView
import android.widget.Toast
import com.example.harmatek.MainActivity
import com.example.harmatek.R
import com.example.harmatek.SharedPreferencesEditor
import kotlinx.android.synthetic.main.fragment_setup_daily_report_time.*
import java.text.SimpleDateFormat
import java.util.*


class SetupDailyReportTimeFragment: Fragment() {

    private lateinit var v: View
    private lateinit var c: Context
    private var message = ""

    private var viewGroup: ViewGroup? = null

    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val cal = Calendar.getInstance()
    private lateinit var reportTimeListener: TimePickerDialog.OnTimeSetListener

    private val sp = SharedPreferencesEditor()

    private val m = MainActivity()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_setup_daily_report_time, container, false)
        c = v.context
        sp.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setTitle(R.string.card_text_6)
        m.toolbar(toolbar, 18f)

        setupTimeSetListeners()

        report_time_picker_text_view.text = stf.format(System.currentTimeMillis())

        report_time_picker_text_view.setOnClickListener {
            showTimePicker(reportTimeListener)
        }
        setup_daily_report_time_button.setOnClickListener {
            inflateDialog(true)
        }
        inquiry_daily_report_time_button.setOnClickListener {
            inflateDialog(null)
        }
        delete_daily_report_time_button.setOnClickListener {
            inflateDialog(false)
        }
    }

    private fun setupTimeSetListeners() {
        reportTimeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            report_time_picker_text_view.text = stf.format(cal.time)
        }
    }

    private fun showTimePicker(timeSetListener: TimePickerDialog.OnTimeSetListener) {
        TimePickerDialog(c, R.style.PickerDialogTheme, timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true).show()
    }

    private fun inflateDialog(setup: Boolean?) {
        val seriesNumber = series_number_input.text.toString()
        if (setup != null) {
            if (validateForm(seriesNumber)) {
                openDialog(seriesNumber, setup)
            }
        }
        else {
            openDialog(seriesNumber, setup)
        }
    }

    private fun validateForm(seriesNumber: String): Boolean {
        return when {
            TextUtils.isEmpty(seriesNumber) -> {
                Toast.makeText(context!!, context!!.applicationContext.getText(R.string.enter_series_number), Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun setupDailyReportTime(seriesNumber: String, setup: Boolean?) {
        if (m.checkPhoneNumber(sp)) {
            if (m.checkPermissions(c)) {
                message = when {
                    setup == null -> {
                        sp.getPassword() +
                                "DR"
                    }
                    setup -> {
                        sp.getPassword() +
                                "DR$seriesNumber" +
                                "T${report_time_picker_text_view.text}"
                    }
                    else -> {
                        sp.getPassword() +
                                "DR" +
                                "DEL"
                    }
                }
                m.sendSMS(c, message, sp)
            }
            else {
                openDialog(seriesNumber, setup)
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
                setupDailyReportTime(seriesNumber, setup)
            }
            .setNegativeButton(R.string.back) { _, _ ->
            }.show()
    }
}