package com.example.harmatek.fragments.settings_fragments

import android.app.DatePickerDialog
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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.harmatek.MainActivity
import com.example.harmatek.R
import com.example.harmatek.SharedPreferencesEditor
import kotlinx.android.synthetic.main.fragment_authority_user_number.*
import java.text.SimpleDateFormat
import java.util.*


class AuthorityUserNumberFragment: Fragment() {

    private lateinit var v: View
    private lateinit var c: Context
    private var message = ""

    private var viewGroup: ViewGroup? = null

    private val sdf = SimpleDateFormat( "dd.MM.yyyy", Locale.getDefault())
    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val cal = Calendar.getInstance()
    private lateinit var startDateListener: DatePickerDialog.OnDateSetListener
    private lateinit var endDateListener: DatePickerDialog.OnDateSetListener
    private lateinit var startTimeListener: TimePickerDialog.OnTimeSetListener
    private lateinit var endTimeListener: TimePickerDialog.OnTimeSetListener

    private val m = MainActivity()
    private var sp = SharedPreferencesEditor()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_authority_user_number, container, false)
        c = v.context
        sp.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setTitle(R.string.card_text_5)
        m.toolbar(toolbar, 18f)

        setupDateSetListeners()
        setupTimeSetListeners()

        start_date_picker_text_view.text = sdf.format(System.currentTimeMillis())
        end_date_picker_text_view.text = sdf.format(System.currentTimeMillis())
        start_time_picker_text_view.text = stf.format(System.currentTimeMillis())
        end_time_picker_text_view.text = stf.format(System.currentTimeMillis())

        start_date_picker_text_view.setOnClickListener {
            showDatePicker(startDateListener, true)
        }
        end_date_picker_text_view.setOnClickListener {
            showDatePicker(endDateListener, false)
        }
        start_time_picker_text_view.setOnClickListener {
            showTimePicker(startTimeListener)
        }
        end_time_picker_text_view.setOnClickListener {
            showTimePicker(endTimeListener)
        }
        access_always_checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                access_time_views.animate().translationY(-access_time_views.height.toFloat())
                access_time_views.visibility = LinearLayout.GONE

            }
            else {
                access_time_views.animate().translationY(0f)
                access_time_views.visibility = LinearLayout.VISIBLE
            }
        }
        authorize_user_number_button.setOnClickListener {
            inflateDialog(true)
        }
        inquiry_user_number_button.setOnClickListener {
            inflateDialog(null)
        }
        delete_user_number_button.setOnClickListener {
            inflateDialog(false)
        }

    }

    private fun setupDateSetListeners() {
        startDateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            start_date_picker_text_view.text = sdf.format(cal.time)

            val date = sdf.parse(end_date_picker_text_view.text.toString())
            if (cal.time.after(date)) {
                end_date_picker_text_view.text = sdf.format(cal.time)
            }
        }
        endDateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            end_date_picker_text_view.text = sdf.format(cal.time)
        }
    }

    private fun setupTimeSetListeners() {
        startTimeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            start_time_picker_text_view.text = stf.format(cal.time)
        }
        endTimeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            end_time_picker_text_view.text = stf.format(cal.time)
        }
    }

    private fun showDatePicker(dateSetListener: DatePickerDialog.OnDateSetListener, startDate: Boolean) {
        val dialog = DatePickerDialog(c, R.style.PickerDialogTheme, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        if (startDate) {
            dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        }
        else {
            val dateInString = start_date_picker_text_view.text.toString()
            val date = sdf.parse(dateInString).time
            dialog.datePicker.minDate = date
        }
        dialog.show()
    }

    private fun showTimePicker(timeSetListener: TimePickerDialog.OnTimeSetListener) {
        TimePickerDialog(c, R.style.PickerDialogTheme, timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true).show()
    }

    private fun getDates(d: TextView, t: TextView): String {
        val dateParts = d.text.split(".")
        val timeParts = t.text.split(":")
        return "${dateParts[2]}${dateParts[1]}${dateParts[0]}${timeParts[0]}${timeParts[1]}"
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

    private fun authorityUserNumber(seriesNumber: String, setup: Boolean?) {
        if (m.checkPhoneNumber(sp)) {
            if (m.checkPermissions(c)) {
                message = when {
                    setup == null -> {
                        sp.getPassword() +
                                "B"
                    }
                    setup -> {
                        if (access_always_checkbox.isChecked) {
                            sp.getPassword() +
                                    "B$seriesNumber" +
                                    "P"
                        }
                        else {
                            sp.getPassword() +
                                    "B$seriesNumber" +
                                    "S${getDates(start_date_picker_text_view, start_time_picker_text_view)}" +
                                    "E${getDates(end_date_picker_text_view, end_time_picker_text_view)}"
                        }
                    }
                    else -> {
                        sp.getPassword() +
                                "B$seriesNumber"
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
                authorityUserNumber(seriesNumber, setup)
            }
            .setNegativeButton(R.string.back) { _, _ ->
            }.show()
    }
}