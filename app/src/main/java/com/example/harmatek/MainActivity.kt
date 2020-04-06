package com.example.harmatek

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.PermissionChecker
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import android.app.PendingIntent
import android.app.Activity
import android.content.*
import android.graphics.*
import android.graphics.Bitmap.createBitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.PictureDrawable
import android.provider.Telephony
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.TextView
import com.example.harmatek.fragments.other_fragments.RegisterFragment
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.Toolbar
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import android.telephony.TelephonyManager
import android.text.SpannableStringBuilder
import android.text.Spanned
import com.example.harmatek.SharedPreferencesEditor.Companion.ARMED_STATUS
import com.example.harmatek.SharedPreferencesEditor.Companion.PRESSURE_1
import com.example.harmatek.SharedPreferencesEditor.Companion.PRESSURE_2
import com.example.harmatek.SharedPreferencesEditor.Companion.TEMPERATURE
import com.example.harmatek.SharedPreferencesEditor.Companion.WATER
import com.example.harmatek.SharedPreferencesEditor.Companion.reportPhoneNumberList
import com.example.harmatek.fragments.other_fragments.UpdateFragment
import com.example.harmatek.fragments.settings_fragments.Setup10UserNumberFragment.Companion.empty
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), SendSMSListener {

    companion object {
        private const val SENT = "SMS_SENT"
        private const val DELIVERED = "SMS_DELIVERED"
        private const val RECEIVED = "android.provider.Telephony.SMS_RECEIVED"

        private const val ACTION = "Action"
        const val REGISTER_FRAGMENT_TAG = "RegisterFragment"
        const val UPDATE_FRAGMENT_TAG = "UpdateFragment"
        const val MAIN_FRAGMENT_TAG = "MainFragment"
        const val MODIFY_PASSWORD_FRAGMENT_TAG = "ModifyPasswordFragment"
        const val STATUS_FRAGMENT_TAG = "StatusFragment"
        const val SET_RTU_TIME_FRAGMENT_TAG = "SetRTUTimeFragment"
        const val SETUP_10_USER_NUMBER_FRAGMENT_TAG = "Setup10UserNumberFragment"
        //const val AUTHORITY_USER_NUMBER_FRAGMENT_TAG = "AuthorityUserNumberFragment"
        //const val SETUP_DAILY_REPORT_TIME_FRAGMENT_TAG = "SetupDailyReportTimeFragment"
        //const val INQUIRY_DIN_STATUS_FRAGMENT_TAG = "InquiryDINStatusFragment"
        const val SETUP_AIN_NAME_FRAGMENT_TAG = "SetupAINNameFragment"

        const val SYNE_EXTRA = "font/syne_extra.ttf"
        const val SYNE_BOLD = "font/syne_bold.ttf"

        private const val celsius = " °C "
        private const val bar = " Bar "
    }

    private lateinit var sentPI: PendingIntent
    private lateinit var deliveredPI: PendingIntent

    private lateinit var registerFragment: RegisterFragment
    private lateinit var updateFragment: UpdateFragment

    private var viewGroup: ViewGroup? = null

    private val sp = SharedPreferencesEditor()

    private lateinit var broadcastReceiverSent: BroadcastReceiver
    private lateinit var broadcastReceiverDelivered: BroadcastReceiver
    private lateinit var broadcastReceiverReceived: BroadcastReceiver

    private var delayedFunction: ((ArrayList<Any>)->Unit)? = null
    private var delayedFunctionParam: ArrayList<Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    override fun onResume() {
        super.onResume()
        sp.setupSharedPreferencesEditor(this)
    }

    private fun init() {
        updateFragment = UpdateFragment()
        registerFragment = RegisterFragment()
        sp.setupSharedPreferencesEditor(this)
        sp.setMasterPassword()

        createBroadcastReceivers()
        setupToolbar()
        brand_name_text.changeBrandNameFont()

        if (!hasPermissions(this)) { askPermissions(this) }

        if (sp.getPhoneNumber() == "") {
            setupFragment(registerFragment, REGISTER_FRAGMENT_TAG, true)
        }
        else {
            setupFragment(updateFragment, UPDATE_FRAGMENT_TAG, true)
        }
    }

    private fun hasPermissions(c: Context): Boolean {
        return PermissionChecker.checkSelfPermission(c, Manifest.permission.SEND_SMS) == PermissionChecker.PERMISSION_GRANTED
                && PermissionChecker.checkSelfPermission(c, Manifest.permission.READ_SMS) == PermissionChecker.PERMISSION_GRANTED
                && PermissionChecker.checkSelfPermission(c, Manifest.permission.RECEIVE_SMS) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun askPermissions(c: Context) {
        ActivityCompat.requestPermissions(c as Activity,
            arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS), 1)
    }

    fun checkPermissions(c: Context): Boolean {
        return if (hasPermissions(c)) {
            true
        } else {
            askPermissions(c)
            Toast.makeText(c, c.getString(R.string.no_permissions), Toast.LENGTH_LONG).show()
            false
        }
    }

    override fun onSMSSend(msg: String, list: ArrayList<Any>, passedFunction: ((ArrayList<Any>)->Unit)? ) {
        delayedFunction = passedFunction
        delayedFunctionParam = list
        sendSMS(this, msg, sp)
    }

    fun sendSMS(c: Context, message: String, sp: SharedPreferencesEditor) {
        if (checkPhoneNumber(sp)) {
            sentPI = PendingIntent.getBroadcast(c, 0, Intent(SENT), 0)
            deliveredPI = PendingIntent.getBroadcast(c, 0, Intent(DELIVERED), 0)
            SmsManager.getDefault().sendTextMessage(sp.getPhoneNumber(), null, message, sentPI, deliveredPI)
        }
        else {
            Toast.makeText(this, getString(R.string.no_phone_number), Toast.LENGTH_LONG).show()
        }
    }

    private fun createBroadcastReceivers() {
        broadcastReceiverSent = object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Log.d("sms", "SMS has been sent!")
                        Snackbar.make(window.decorView, getText(R.string.sms_sent), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
                    }
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                        Log.d("sms", "SMS ERROR! Generic failure!")
                        Snackbar.make(window.decorView, getText(R.string.sms_sending_canceled), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
                    }
                    SmsManager.RESULT_ERROR_NO_SERVICE -> {
                        Log.d("sms", "SMS ERROR! No service!")
                        Snackbar.make(window.decorView, getText(R.string.sms_sending_canceled), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
                    }
                    SmsManager.RESULT_ERROR_NULL_PDU -> {
                        Log.d("sms", "SMS ERROR! Null PDU!")
                        Snackbar.make(window.decorView, getText(R.string.sms_sending_canceled), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
                    }
                    SmsManager.RESULT_ERROR_RADIO_OFF -> {
                        Log.d("sms", "SMS ERROR! Radio off!")
                        Snackbar.make(window.decorView, getText(R.string.sms_sending_canceled), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
                    }
                }
            }
        }
        broadcastReceiverDelivered = object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Log.d("sms", "SMS has been delivered")
                        if (delayedFunction != null && delayedFunctionParam != null) {
                            delayedFunction!!(delayedFunctionParam!!)
                            delayedFunction = null
                            delayedFunctionParam = null
                        }
                        Snackbar.make(window.decorView, getText(R.string.sms_delivered), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
                    }
                    Activity.RESULT_CANCELED -> {
                        Log.d("sms", "SMS delivery cancelled!!!")
                        Snackbar.make(window.decorView, getText(R.string.sms_delivery_canceled), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
                    }
                }
            }
        }
        broadcastReceiverReceived = object : BroadcastReceiver() {

            override fun onReceive(arg0: Context, arg1: Intent) {
                val bundle = arg1.extras
                var phoneNumber = ""
                var message = ""
                var messageExtra = ""
                if (bundle != null) {
                    try {
                        val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(arg1)
                        for (i in smsMessages.indices) {
                            phoneNumber = smsMessages[i].originatingAddress!!.replace("[^0-9]".toRegex(), "")
                            if (message == "") {
                                message = smsMessages[i]!!.messageBody
                            } else {
                                messageExtra = smsMessages[i]!!.messageBody
                            }
                        }
                        message += messageExtra
                        val currentPhoneNumber = sp.getPhoneNumber().replace("[^0-9]".toRegex(), "")
                        if (message.isNotEmpty() || currentPhoneNumber == phoneNumber || "${getCurrentCountryCode(arg0)}${currentPhoneNumber.substring(1)}" == phoneNumber) {
                            showMessage(message)
                        }
                        Log.d("sms", "SMS received from $phoneNumber, message: $message")
                    } catch (e: Exception) {
                        Log.d("sms", e.message)
                    }
                }

            }
        }
        registerReceiver(broadcastReceiverSent, IntentFilter(SENT))
        registerReceiver(broadcastReceiverDelivered, IntentFilter(DELIVERED))
        registerReceiver(broadcastReceiverReceived, IntentFilter(RECEIVED))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiverSent)
        unregisterReceiver(broadcastReceiverDelivered)
        unregisterReceiver(broadcastReceiverReceived)
    }

    private fun getCurrentCountryCode(context: Context): Int {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryIso = telephonyManager.simCountryIso.toUpperCase(Locale.getDefault())
        return PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryIso)
    }

    fun checkPhoneNumber(sp: SharedPreferencesEditor): Boolean {
        return if (sp.getPhoneNumber() != "") {
            true
        } else {
            Toast.makeText(this, getString(R.string.no_phone_number), Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun setupFragment(fragment: Fragment, tag: String, onCreate: Boolean) {
        if (onCreate) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, tag).addToBackStack(null).commit()
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.item_animation_from_right, R.anim.item_animation_to_left, R.anim.item_animation_from_left, R.anim.item_animation_to_right)
            .replace(R.id.fragment_container, fragment, tag).addToBackStack(null).commit()
    }

    private fun findFragment(tag: String): Boolean = (supportFragmentManager.findFragmentByTag(tag) != null
            && supportFragmentManager.findFragmentByTag(tag)!!.isVisible)

    private fun openDialog(text: String) {
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)
        dialogText.text = text
        builder.setView(dialogView)
            .setPositiveButton(R.string.yes) { _, _ ->
                if (text == getText(R.string.change_phone_number)) {
                    setupFragment(registerFragment, REGISTER_FRAGMENT_TAG, false)
                }
                else {
                    finish()
                }
            }
            .setNegativeButton(R.string.no) { _, _ ->
            }.show()
    }

    override fun onBackPressed() {
        if (findFragment(UPDATE_FRAGMENT_TAG) || sp.getPhoneNumber() == ""){
            openDialog(getText(R.string.quit_the_app).toString())
        }
        else {
            super.onBackPressed()
        }
    }

    private fun String.containsWords(): Boolean {
        return (contains(getString(R.string.normal_en)) || contains(getString(R.string.lower_en)) || contains(getString(R.string.higher_en)))
    }

    private fun String.startsWithWords(): Boolean {
        return (startsWith("T0:") || startsWith("T1:") || startsWith("T2:") || startsWith("T3:")
                || startsWith("T4:") || startsWith("T5:") || startsWith("T6:") || startsWith("T7:"))
    }

    private fun modifyStatusText(s: String, mark: String, tag: String): String {
        val text = s.replace(getString(R.string.din0).toRegex(), getString(R.string.water, ""))
            .replace(getString(R.string.ain0).toRegex(), getString(R.string.temp, ""))
            .replace(getString(R.string.ain1).toRegex(), getString(R.string.pressure1, ""))
            .replace(getString(R.string.ain2).toRegex(), getString(R.string.pressure2, ""))
            .replace(",".toRegex(), mark)
            .replace(";".toRegex(), "")
        if (s.containsWords()) {
            val status = text.split(":")
            sp.setStatus(status[1], tag)
        }
        return text
    }

    private fun modifyStatusText(s: String, mark: String, tag: String, id: Int): String {
        val list = s.split(":")
        val value = list[2].split(";")
        val status = when {
            list[1].contains(getString(R.string.lower_en).toUpperCase(Locale.getDefault())) -> getString(R.string.lower_en)
            list[1].contains(getString(R.string.higher_en).toUpperCase(Locale.getDefault())) -> getString(R.string.higher_en)
            else -> getString(R.string.normal_en)
        }
        sp.setStatus("${value[0]}$mark$status", tag)
        return getString(id, status)
    }

    private fun showMessage(message: String) {
        var finishMessage = false
        var text = ""
        val s = message.split("\n").toMutableList()
        for (i in 0 until s.size) {
            when {
                s[i].startsWith(getString(R.string.din0)) -> {
                    s[i] = modifyStatusText(s[i], "", WATER)
                    finishMessage = true
                }
                s[i].startsWith(getString(R.string.tank_full_en)) -> {
                    s[i] = getString(R.string.tank_full)
                    sp.setStatus(getString(R.string.full_en), WATER)
                }
                s[i].startsWith(getString(R.string.tank_normal_en)) -> {
                    s[i] = getString(R.string.tank_normal)
                    sp.setStatus(getString(R.string.normal_en), WATER)
                }
                s[i].startsWith(getString(R.string.ain0)) -> {
                    s[i] = modifyStatusText(s[i], celsius, TEMPERATURE)
                }
                s[i].startsWith(getString(R.string.ain1)) -> {
                    s[i] = modifyStatusText(s[i], bar, PRESSURE_1)
                }
                s[i].startsWith(getString(R.string.ain2)) -> {
                    s[i] = modifyStatusText(s[i], bar, PRESSURE_2)
                    finishMessage = true
                }
                s[i].contains(getString(R.string.status_armed)) -> {
                    sp.setStatus(getString(R.string.status_armed), ARMED_STATUS)
                }
                s[i].contains(getString(R.string.status_disarmed)) -> {
                    sp.setStatus(getString(R.string.status_disarmed), ARMED_STATUS)
                }
                s[i].startsWith(getString(R.string.temp_en)) -> {
                    s[i] = modifyStatusText(s[i], celsius, TEMPERATURE, R.string.temp)
                }
                s[i].startsWith(getString(R.string.pressure1_en)) -> {
                    s[i] = modifyStatusText(s[i], bar, PRESSURE_1, R.string.pressure1)
                }
                s[i].startsWith(getString(R.string.pressure2_en)) -> {
                    s[i] = modifyStatusText(s[i], bar, PRESSURE_2, R.string.pressure2)
                }
                s[i].startsWithWords() -> {
                    s[i] = s[i].replace("Empty".toRegex(), empty)
                        .replace(";".toRegex(), "")
                    val startNumber = if (message.startsWith("T0:")) {
                        0
                    } else {
                        5
                    }
                    val number = s[i].split(":")
                    sp.setStatus(number[1], reportPhoneNumberList[i+startNumber])
                    if (s[i].startsWith("T7:")) {
                        finishMessage = true
                    }
                }
                else ->  {
                    s[i] = s[i].replace(";".toRegex(), "")
                }
            }
            s[i] = s[i].replace(";".toRegex(), "")
            text = text+s[i]+"\n"
            if (finishMessage) {
                break
            }
        }
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)

        dialogText.text = updateFragment.translateString(text)
        builder.setView(dialogView)
            .setNeutralButton(R.string.ok) { _, _ ->
            }.show()
    }

    private fun setupToolbar() {
        main_toolbar.setNavigationOnClickListener { onBackPressed() }
        main_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        main_toolbar.inflateMenu(R.menu.toolbar_menu)
        main_toolbar.setOnMenuItemClickListener {
            if (!findFragment(REGISTER_FRAGMENT_TAG)) {
                openDialog(getText(R.string.change_phone_number).toString())
            }
            true
        }
        main_toolbar.changeToolbarFont(24f)
    }

    fun changeToolbarIcon(c: Context, toolbar: Toolbar, id: Int, hasFilter: Boolean) {
        val width = c.resources.displayMetrics.widthPixels

        val drawable = ContextCompat.getDrawable(c, id)!!
        val bitmap = Bitmap.createScaledBitmap((drawable as BitmapDrawable).bitmap, width/12, width/12, true)
        val picture = Picture()
        val canvas = picture.beginRecording(width/12,width/12)
        val p = Paint()
        canvas.drawBitmap(bitmap, 0f, 0f, p)
        picture.endRecording()
        val pictureDrawable = PictureDrawable(picture)

        val newBitmap = createBitmap(pictureDrawable.intrinsicWidth, pictureDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val newCanvas = Canvas(newBitmap)
        newCanvas.drawPicture(pictureDrawable.picture)

        toolbar.navigationIcon = BitmapDrawable(c.resources, newBitmap)

        if (hasFilter) {
            val filter = LightingColorFilter(Color.BLACK, ContextCompat.getColor(c, R.color.colorAccent))
            (toolbar.navigationIcon as BitmapDrawable).colorFilter = filter
        }
    }

    fun toolbar(toolbar: Toolbar, textSize: Float) {
        toolbar.changeToolbarFont(textSize)
    }

    private fun Toolbar.changeToolbarFont(textSize: Float){
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view is TextView && view.text == title) {
                view.typeface = Typeface.createFromAsset(view.context.assets, SYNE_BOLD)
                view.textSize = textSize
                view.setShadowLayer(5f, 5f, 5f, ContextCompat.getColor(context, R.color.colorPrimaryDark))
                break
            }
        }
    }

    private fun TextView.changeBrandNameFont() {
        val font = Typeface.createFromAsset(context.assets, SYNE_EXTRA)
        val font2 = Typeface.createFromAsset(context.assets, SYNE_BOLD)
        val ss = SpannableStringBuilder(text)
        ss.setSpan (CustomTypefaceSpan("", font), 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        ss.setSpan (CustomTypefaceSpan("", font2), 4, 8, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        text = ss
    }
}