package com.example.harmatek.fragments.settings_fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.harmatek.MainActivity
import com.example.harmatek.R
import com.example.harmatek.SharedPreferencesEditor
import com.example.harmatek.SharedPreferencesEditor.Companion.FULL_SMS_REPORT_1
import com.example.harmatek.SharedPreferencesEditor.Companion.FULL_SMS_REPORT_2
import com.example.harmatek.SharedPreferencesEditor.Companion.ON_DUTY_1
import com.example.harmatek.SharedPreferencesEditor.Companion.ON_DUTY_2
import com.example.harmatek.SharedPreferencesEditor.Companion.SMS_ALARMS_1
import com.example.harmatek.SharedPreferencesEditor.Companion.SMS_ALARMS_2
import com.example.harmatek.SharedPreferencesEditor.Companion.TIMER_REPORT_1
import com.example.harmatek.SharedPreferencesEditor.Companion.TIMER_REPORT_2
import kotlinx.android.synthetic.main.fragment_setup_10_user_number.*


class Setup10UserNumberFragment: Fragment() {

    private lateinit var v: View
    private lateinit var c: Context
    private var message = ""

    private var viewGroup: ViewGroup? = null

    private val sp = SharedPreferencesEditor()

    private val m = MainActivity()

    private var sharedPreferences: SharedPreferences? = null
    private lateinit var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener

    companion object {
        const val empty = " --- "
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_setup_10_user_number, container, false)
        c = v.context
        sp.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setTitle(R.string.card_text_4)
        m.toolbar(toolbar, 18f)

        text_on_duty_1.setCompoundDrawablesWithIntrinsicBounds(null, null, getScaledDrawable(), null)
        text_on_duty_2.setCompoundDrawablesWithIntrinsicBounds(null, null, getScaledDrawable(), null)

        button_phone_number_full_sms_report_1.setOnClickListener {
            openDialog("0", text_phone_number_full_sms_report_1.text.toString())
        }
        button_phone_number_full_sms_report_2.setOnClickListener {
            openDialog("1", text_phone_number_full_sms_report_2.text.toString())
        }
        button_phone_number_on_duty_1.setOnClickListener {
            openDialog("2", text_phone_number_on_duty_1.text.toString())
        }
        button_phone_number_on_duty_2.setOnClickListener {
            openDialog("3", text_phone_number_on_duty_2.text.toString())
        }
        button_phone_number_sms_alarms_1.setOnClickListener {
            openDialog("4", text_phone_number_sms_alarms_1.text.toString())
        }
        button_phone_number_sms_alarms_2.setOnClickListener {
            openDialog("5", text_phone_number_sms_alarms_2.text.toString())
        }
        button_phone_number_timer_report_1.setOnClickListener {
            openDialog("6", text_phone_number_timer_report_1.text.toString())
        }
        button_phone_number_timer_report_2.setOnClickListener {
            openDialog("7", text_phone_number_timer_report_2.text.toString())
        }
        inquiry_all_numbers_button.setOnClickListener {
            openDialog(null)
        }
    }

    override fun onStart() {
        super.onStart()
        createSharedPreferenceChangeListener()
        text_phone_number_full_sms_report_1.text = sp.getStatus(FULL_SMS_REPORT_1, empty)
        text_phone_number_full_sms_report_2.text = sp.getStatus(FULL_SMS_REPORT_2, empty)
        text_phone_number_on_duty_1.text = sp.getStatus(ON_DUTY_1, empty)
        text_phone_number_on_duty_2.text = sp.getStatus(ON_DUTY_2, empty)
        text_phone_number_sms_alarms_1.text = sp.getStatus(SMS_ALARMS_1, empty)
        text_phone_number_sms_alarms_2.text = sp.getStatus(SMS_ALARMS_2, empty)
        text_phone_number_timer_report_1.text = sp.getStatus(TIMER_REPORT_1, empty)
        text_phone_number_timer_report_2.text = sp.getStatus(TIMER_REPORT_2, empty)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (sharedPreferences != null) {
            sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
        }
    }

    private fun createSharedPreferenceChangeListener() {
        sharedPreferences = sp.sharedPreferences()
        sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { shared, _ ->
            text_phone_number_full_sms_report_1.text = shared.getString(FULL_SMS_REPORT_1, empty)
            text_phone_number_full_sms_report_2.text = shared.getString(FULL_SMS_REPORT_2, empty)
            text_phone_number_on_duty_1.text = shared.getString(ON_DUTY_1, empty)
            text_phone_number_on_duty_2.text = shared.getString(ON_DUTY_2, empty)
            text_phone_number_sms_alarms_1.text = shared.getString(SMS_ALARMS_1, empty)
            text_phone_number_sms_alarms_2.text = shared.getString(SMS_ALARMS_2, empty)
            text_phone_number_timer_report_1.text = shared.getString(TIMER_REPORT_1, empty)
            text_phone_number_timer_report_2.text = shared.getString(TIMER_REPORT_2, empty)
        }
        sharedPreferences!!.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }

    private fun getScaledDrawable(): BitmapDrawable {
        val width = c.resources.displayMetrics.widthPixels
        val drawable = ContextCompat.getDrawable(c, R.drawable.harmatek_phone)!!
        val bitmap = Bitmap.createScaledBitmap((drawable as BitmapDrawable).bitmap, width/16, width/16, true)
        val picture = Picture()
        val canvas = picture.beginRecording(width/16,width/16)
        val p = Paint()
        canvas.drawBitmap(bitmap, 0f, 0f, p)
        picture.endRecording()
        val pictureDrawable = PictureDrawable(picture)

        val newBitmap = Bitmap.createBitmap(
            pictureDrawable.intrinsicWidth,
            pictureDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val newCanvas = Canvas(newBitmap)
        newCanvas.drawPicture(pictureDrawable.picture)

        return BitmapDrawable(c.resources, newBitmap)
    }

    private fun setup10UserNumber(seriesNumber: String, phoneNumber: String, currentPhoneNumber: String, setup: Boolean?) {
        if (m.checkPhoneNumber(sp)) {
            if (m.checkPermissions(c)) {
                message = when {
                    setup == null -> {
                        sp.getPassword() +
                                "A"
                    }
                    setup -> {
                        sp.getPassword() +
                                "A$seriesNumber" +
                                "T$phoneNumber"
                    }
                    else -> {
                        sp.getPassword() +
                                "A$seriesNumber"
                    }
                }
                m.sendSMS(c, message, sp)
            }
            else {
                when {
                    setup == null -> openDialog(setup)
                    setup -> openDialog(seriesNumber, currentPhoneNumber)
                    else -> openDialog(seriesNumber, currentPhoneNumber)
                }
            }
        }
    }

    private fun openDialog(setup: Boolean?) {
        val builder = AlertDialog.Builder(c, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)
        dialogText.text = c.getText(R.string.confirm_send_message)

        builder.setView(dialogView)
            .setPositiveButton(R.string.send) { _, _ ->
                val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(dialogView.windowToken, 0)
                setup10UserNumber("", "", "", setup)
            }
            .setNegativeButton(R.string.back) { _, _ ->
            }.show()
    }

    private fun openDialog(seriesNumber: String, currentPhoneNumber: String) {
        val builder = AlertDialog.Builder(c, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_password_layout, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)
        val dialogEditText: EditText = dialogView.findViewById(R.id.password_input)
        dialogEditText.inputType = InputType.TYPE_CLASS_PHONE
        if (currentPhoneNumber != empty) {
            dialogEditText.setText(currentPhoneNumber)
        }
        dialogEditText.setSelection(dialogEditText.text.length)
        dialogText.text = c.getText(R.string.phone_number)
        builder.setView(dialogView)
            .setPositiveButton(R.string.setup) { _, _ ->
                val phoneNumber = dialogEditText.text.toString()
                if (phoneNumber.isNotEmpty()) {
                    val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(dialogView.windowToken, 0)
                    setup10UserNumber(seriesNumber, phoneNumber, currentPhoneNumber, true)
                }
                else {
                    Toast.makeText(c, c.getText(R.string.enter_phone_number), Toast.LENGTH_SHORT).show()
                    openDialog(seriesNumber, currentPhoneNumber)
                }
            }
            .setNegativeButton(R.string.back) { _, _ ->
                val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(dialogView.windowToken, 0)
            }
            .setNeutralButton(R.string.delete) { _, _ ->
                val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(dialogView.windowToken, 0)
                setup10UserNumber(seriesNumber, "", currentPhoneNumber, false)

            }.show()
    }
}