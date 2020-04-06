package com.example.harmatek

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.Toast


class SharedPreferencesEditor {

    companion object {
        const val PASSWORD = "Password"
        const val PHONE_NUMBER = "Phone"
        const val PHONE_NUMBER_LIST = "Devices"
        const val WATER = "Water"
        const val TEMPERATURE = "Temperature"
        const val PRESSURE_1 = "Pressure1"
        const val PRESSURE_2 = "Pressure2"
        const val ARMED_STATUS = "Armed"
        const val MASTER_PASSWORD = "2808"
        const val MASTER_ACCESS = "Access"

        const val FULL_SMS_REPORT_1 = "Full1"
        const val FULL_SMS_REPORT_2 = "Full2"
        const val ON_DUTY_1 = "Duty1"
        const val ON_DUTY_2 = "Duty2"
        const val SMS_ALARMS_1 = "Alarms1"
        const val SMS_ALARMS_2 = "Alarms2"
        const val TIMER_REPORT_1 = "Timer1"
        const val TIMER_REPORT_2 = "Timer2"

        val reportPhoneNumberList = listOf(
            FULL_SMS_REPORT_1,
            FULL_SMS_REPORT_2,
            ON_DUTY_1,
            ON_DUTY_2,
            SMS_ALARMS_1,
            SMS_ALARMS_2,
            TIMER_REPORT_1,
            TIMER_REPORT_2
        )
    }

    private lateinit var c: Context
    private lateinit var sharedPreferences: SharedPreferences

    fun setupSharedPreferencesEditor(context: Context) {
        this.c = context
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c)
    }

    fun sharedPreferences(): SharedPreferences {
        return sharedPreferences
    }

    fun setPassword(list: ArrayList<Any>) {
        sharedPreferences.edit().putString(PASSWORD, list[1] as String).apply()
    }

    fun setPassword(password: String) {
        sharedPreferences.edit().putString(PASSWORD, password).apply()
    }

    fun getPassword(): String {
        return sharedPreferences.getString(PASSWORD, "1234") as String
    }

    fun setPhoneNumber(phoneNumber: String) {
        sharedPreferences.edit().putString(PHONE_NUMBER, phoneNumber).apply()
    }

    fun getPhoneNumber(): String {
        return sharedPreferences.getString(PHONE_NUMBER, "") as String
    }

    fun setPhoneNumberList(phoneNumberList: ArrayList<String>) {
        val set: Set<String> = HashSet(phoneNumberList)
        sharedPreferences.edit().putStringSet(PHONE_NUMBER_LIST, set).apply()
    }

    fun getPhoneNumberList(): ArrayList<String> {
        val set = sharedPreferences.getStringSet(PHONE_NUMBER_LIST, setOf())
        return ArrayList(set)
    }

    fun deletePhoneNumberFromList(phoneNumber: String) {
        val list = getPhoneNumberList()
        if (list.contains(phoneNumber)) {
            list.remove(phoneNumber)
            Toast.makeText(c, c.getText(R.string.number_deleted), Toast.LENGTH_SHORT).show()
            setPhoneNumberList(list)
            if (list.isEmpty()) {
                setPhoneNumber("")
                (c as MainActivity).recreate()
            }
            else {
                if (phoneNumber == getPhoneNumber()) {
                    setPhoneNumber(list[0])
                }
            }
        }
        else {
            Toast.makeText(c, c.getText(R.string.not_phone_number), Toast.LENGTH_SHORT).show()
        }
    }

    fun setStatus(status: String, key: String) {
        sharedPreferences.edit().putString(key, status).apply()
    }

    fun getStatus(key: String, defValue: String): String {
        return sharedPreferences.getString(key, defValue) as String
    }

    fun setMasterPassword() {
        sharedPreferences.edit().putString(MASTER_PASSWORD, MASTER_PASSWORD).apply()
    }

    fun getMasterPassword(): String {
        return sharedPreferences.getString(MASTER_PASSWORD, MASTER_PASSWORD) as String
    }

    fun setMasterAccess() {
        sharedPreferences.edit().putBoolean(MASTER_ACCESS, true).apply()
    }

    fun getMasterAccess(): Boolean {
        return sharedPreferences.getBoolean(MASTER_ACCESS, false)
    }
}