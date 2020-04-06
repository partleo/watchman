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
import android.widget.TextView
import android.widget.Toast
import com.example.harmatek.MainActivity
import com.example.harmatek.R
import kotlinx.android.synthetic.main.fragment_modify_password.*
import kotlinx.android.synthetic.main.fragment_modify_password.password_input
import com.example.harmatek.SendSMSListener
import com.example.harmatek.SharedPreferencesEditor


class ModifyPasswordFragment: Fragment() {

    private lateinit var v: View
    private lateinit var c: Context
    private var message = ""

    private var viewGroup: ViewGroup? = null

    private var sendSMSListener: SendSMSListener? = null

    private val sp = SharedPreferencesEditor()

    private val m = MainActivity()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_modify_password, container, false)
        c = v.context
        sp.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setTitle(R.string.card_text_1)
        m.toolbar(toolbar, 18f)
        change_password_button.setOnClickListener {
            val password = password_input.text.toString()
            if (validateForm(password, confirm_password_input.text.toString())) {
                openDialog(password)
            }
        }
    }

    private fun validateForm(password: String, confirmPassword: String): Boolean {
        when {
            TextUtils.isEmpty(password) -> {
                Toast.makeText(context!!, context!!.applicationContext.getText(R.string.enter_password), Toast.LENGTH_SHORT).show()
                return false
            }
            TextUtils.isEmpty(confirmPassword) -> {
                Toast.makeText(context!!, context!!.applicationContext.getText(R.string.confirm_password), Toast.LENGTH_SHORT).show()
                return false
            }
            password.length != 4 -> {
                Toast.makeText(context!!, context!!.applicationContext.getText(R.string.password_too_short), Toast.LENGTH_SHORT).show()
                return false
            }
            password != confirmPassword -> {
                Toast.makeText(context!!, context!!.applicationContext.getText(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is SendSMSListener) {
            sendSMSListener = context
        } else {
            throw RuntimeException(context!!.toString())
        }
    }

    private fun changePassword(newPassword: String) {
        if (m.checkPhoneNumber(sp)) {
            if (m.checkPermissions(c)) {
                message = sp.getPassword() +
                        "P$newPassword"
                if (sendSMSListener != null) {
                    val list = arrayListOf(c, newPassword)
                    sendSMSListener!!.onSMSSend(message, list, sp::setPassword)
                }
            }
            else {
                openDialog(newPassword)
            }
        }
    }

    private fun openDialog(newPassword: String) {
        val builder = AlertDialog.Builder(c, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)
        dialogText.text = c.getText(R.string.confirm_send_message_2)
        builder.setView(dialogView)
            .setPositiveButton(R.string.send) { _, _ ->
                val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(dialogView.windowToken, 0)
                changePassword(newPassword)
            }
            .setNegativeButton(R.string.back) { _, _ ->
            }.show()
    }
}