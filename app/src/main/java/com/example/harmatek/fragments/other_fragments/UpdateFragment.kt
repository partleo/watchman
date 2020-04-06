package com.example.harmatek.fragments.other_fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.harmatek.MainActivity
import com.example.harmatek.MainActivity.Companion.MAIN_FRAGMENT_TAG
import com.example.harmatek.R
import com.example.harmatek.SharedPreferencesEditor
import com.example.harmatek.SharedPreferencesEditor.Companion.ARMED_STATUS
import com.example.harmatek.SharedPreferencesEditor.Companion.PRESSURE_1
import com.example.harmatek.SharedPreferencesEditor.Companion.PRESSURE_2
import com.example.harmatek.SharedPreferencesEditor.Companion.TEMPERATURE
import com.example.harmatek.SharedPreferencesEditor.Companion.WATER
import com.example.harmatek.fragments.settings_fragments.Setup10UserNumberFragment.Companion.empty
import kotlinx.android.synthetic.main.fragment_update.*


class UpdateFragment: Fragment() {

    private lateinit var m: MainActivity
    private var viewGroup: ViewGroup? = null

    private lateinit var v: View
    private lateinit var c: Context
    private var message = ""

    private val sp = SharedPreferencesEditor()

    private var sharedPreferences: SharedPreferences? = null
    private lateinit var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_update, container, false)
        c = v.context
        m = MainActivity()
        sp.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setTitle(R.string.app_name_caps)

        m.changeToolbarIcon(c, toolbar, R.drawable.harmatek_exit, true)
        m.toolbar(toolbar, 24f)

        update_din0_status_button.setOnClickListener {
            openDialog(5)
        }
        update_ain_status_button.setOnClickListener {
            openDialog(0)
        }
        arm_status_button.setOnClickListener {
            openDialog(6)
        }
        disarm_status_button.setOnClickListener {
            openDialog(7)
        }
        setup_settings_button.setOnClickListener {
            if (sp.getMasterAccess()) {
                setupFragment()
            }
            else {
                openDialog()
            }
        }
        createSharedPreferenceChangeListener()

        armed_status.text = sp.getStatus(ARMED_STATUS, c.getString(R.string.no_status)).translate()
        armed_status.getIcon()
        water.text = getString(R.string.water, sp.getStatus(WATER, empty)).translate()
        temperature.text = getString(R.string.temp, sp.getStatus(TEMPERATURE, empty)).translate()
        pressure_1.text = getString(R.string.pressure1, sp.getStatus(PRESSURE_1, empty)).translate()
        pressure_2.text = getString(R.string.pressure2, sp.getStatus(PRESSURE_2, empty)).translate()
    }

    fun translateString(s: String): String {
        return s.translate()
    }

    private fun String.translate(): String {
        return this.replace(getString(R.string.status_armed).toRegex(), getString(R.string.armed))
            .replace(getString(R.string.status_disarmed).toRegex(), getString(R.string.disarmed))
            .replace(getString(R.string.no_status_en).toRegex(), getString(R.string.no_status))
            .replace(getString(R.string.normal_en).toRegex(), getString(R.string.normal))
            .replace(getString(R.string.lower_en).toRegex(), getString(R.string.lower))
            .replace(getString(R.string.higher_en).toRegex(), getString(R.string.higher))
            .replace(getString(R.string.full_en).toRegex(), getString(R.string.full))
            .replace(getString(R.string.temp_en).toRegex(), getString(R.string.temp))
            .replace(getString(R.string.pressure1_en).toRegex(), getString(R.string.pressure1))
            .replace(getString(R.string.pressure2_en).toRegex(), getString(R.string.pressure2))
    }

    private fun TextView.getIcon() {
        when (text) {
            c.getString(R.string.armed) -> {
                alpha = 1f
                setCompoundDrawablesWithIntrinsicBounds(null, null, getScaledDrawable(R.drawable.harmatek_armed), null)
            }
            c.getString(R.string.disarmed) -> {
                alpha = 1f
                setCompoundDrawablesWithIntrinsicBounds(null, null, getScaledDrawable(R.drawable.harmatek_disarmed), null)
            }
            else -> {
                alpha = 0.5f
                setCompoundDrawablesWithIntrinsicBounds(null, null, getScaledDrawable(R.drawable.harmatek_alert), null)
            }
        }
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
            armed_status.text = shared.getString(ARMED_STATUS, c.getString(R.string.no_status))!!.translate()
            armed_status.getIcon()
            water.text = getString(R.string.water, shared.getString(WATER, empty)).translate()
            temperature.text = getString(R.string.temp, shared.getString(TEMPERATURE, empty)).translate()
            pressure_1.text = getString(R.string.pressure1, shared.getString(PRESSURE_1, empty)).translate()
            pressure_2.text = getString(R.string.pressure2, shared.getString(PRESSURE_2, empty)).translate()
        }
        sharedPreferences!!.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }

    private fun getScaledDrawable(id: Int): BitmapDrawable {
        val width = c.resources.displayMetrics.widthPixels
        val drawable = ContextCompat.getDrawable(c, id)!!

        val bitmap = Bitmap.createScaledBitmap((drawable as BitmapDrawable).bitmap, width/6, width/6, true)
        val picture = Picture()
        val canvas = picture.beginRecording(width/6,width/6)
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

    private fun setupFragment() {
        fragmentManager!!
            .beginTransaction()
            .setCustomAnimations(R.anim.item_animation_from_right, R.anim.item_animation_to_left, R.anim.item_animation_from_left, R.anim.item_animation_to_right)
            .replace(R.id.fragment_container, MainFragment(), MAIN_FRAGMENT_TAG)
            .addToBackStack(null)
            .commit()
    }

    private fun inquiryStatus(statusNumber: Int) {
        if (m.checkPhoneNumber(sp)) {
            if (m.checkPermissions(c)) {
                message = when (statusNumber) {
                    4 -> {
                        sp.getPassword() +
                                "EE"
                    }
                    5 -> {
                        sp.getPassword() +
                                "DINE"
                    }
                    6 -> {
                        sp.getPassword() +
                                "AA"
                    }
                    7 -> {
                        sp.getPassword() +
                                "BB"
                    }
                    else -> {
                        sp.getPassword() +
                                "AINE"
                    }
                }
                if (message != "") {
                    m.sendSMS(c, message, sp)
                }
            }
            else {
                openDialog(statusNumber)
            }
        }
    }

    private fun openDialog(statusNumber: Int) {
        val builder = AlertDialog.Builder(c, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)
        dialogText.text = if (statusNumber > 5) {
            c.getText(R.string.confirm_send_message_2)
        }
        else {
            c.getText(R.string.confirm_send_message)
        }
        builder.setView(dialogView)
            .setPositiveButton(R.string.send) { _, _ ->
                inquiryStatus(statusNumber)
            }
            .setNegativeButton(R.string.back) { _, _ ->
            }.show()
    }

    private fun openDialog() {
        val builder = AlertDialog.Builder(c, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_password_layout, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)
        val dialogEditText: EditText = dialogView.findViewById(R.id.password_input)
        dialogText.text = c.getText(R.string.master_password)
        builder.setView(dialogView)
            .setPositiveButton(R.string.ok) { _, _ ->
                if (dialogEditText.text.toString() == sp.getMasterPassword()) {
                    sp.setMasterAccess()
                    setupFragment()
                }
                else {
                    Toast.makeText(c, c.getText(R.string.no_access_to_configuration), Toast.LENGTH_SHORT).show()
                    openDialog()
                }
            }
            .setNegativeButton(R.string.back) { _, _ ->
            }.show()
    }
}