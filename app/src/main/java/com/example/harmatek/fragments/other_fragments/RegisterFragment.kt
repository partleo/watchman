package com.example.harmatek.fragments.other_fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.harmatek.*
import com.example.harmatek.MainActivity.Companion.UPDATE_FRAGMENT_TAG
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment: Fragment() {

    private lateinit var v: View
    private lateinit var c: Context

    private lateinit var m: MainActivity

    private val sp = SharedPreferencesEditor()

    private val viewGroup: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_register, container, false)
        c = v.context
        m = MainActivity()
        sp.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setTitle(R.string.app_name_caps)
        m.toolbar(toolbar, 24f)

        if (sp.getPhoneNumber() == "") {
            m.changeToolbarIcon(c, toolbar, R.drawable.harmatek_exit, true)
        }
        else {
            m.changeToolbarIcon(c, toolbar, R.drawable.harmatek_back_arrow, false)
        }

        setAdapter()

        register_button.setOnClickListener {
            val password = password_input.text.toString()
            val phoneNumber =
            if (select_saved_number_checkbox.isChecked) {
                if (spinner.selectedItem == null) {
                    ""
                } else {
                    spinner.selectedItem.toString()
                }
            } else {
                phone_number_input.text.toString()
            }
            if (validateForm(password, phoneNumber)) {
                sp.setPassword(password)
                sp.setPhoneNumber(phoneNumber)

                val list = sp.getPhoneNumberList()
                list.add(phoneNumber)
                sp.setPhoneNumberList(list)

                if (fragmentManager!!.findFragmentByTag(UPDATE_FRAGMENT_TAG) != null && list.size > 1) {
                    fragmentManager!!.popBackStack()
                } else {
                    (c as MainActivity).recreate()
                }
            }
        }
        select_saved_number_checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                phone_number_input.visibility = EditText.GONE
                spinner_background_layout.visibility = RelativeLayout.VISIBLE

            }
            else {
                spinner_background_layout.visibility = RelativeLayout.GONE
                phone_number_input.visibility = EditText.VISIBLE
            }
        }
        delete_button.setOnClickListener {
            val phoneNumber =
                if (select_saved_number_checkbox.isChecked) {
                    if (spinner.selectedItem == null) {
                        ""
                    } else {
                        spinner.selectedItem.toString()
                    }
                } else {
                    phone_number_input.text.toString()
                }
            if (phoneNumber != "") {
                openDialog(phoneNumber)
            }
            else {
                Toast.makeText(c, c.getString(R.string.no_phone_number), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAdapter() {
        val adapter = ArrayAdapter(c, android.R.layout.simple_spinner_item, sp.getPhoneNumberList())
        spinner.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        select_saved_number_checkbox.isChecked = false
        password_input.setText(sp.getPassword())
        phone_number_input.setText(sp.getPhoneNumber())
    }

    private fun validateForm(password: String, phoneNumber: String): Boolean {
        return when {
            TextUtils.isEmpty(password) -> {
                Toast.makeText(context!!, context!!.applicationContext.getText(R.string.enter_password), Toast.LENGTH_SHORT).show()
                false
            }
            (password.length < 4) -> {
                Toast.makeText(context!!, context!!.applicationContext.getText(R.string.password_too_short), Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(phoneNumber) -> {
                Toast.makeText(context!!, context!!.applicationContext.getText(R.string.enter_phone_number), Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun openDialog(phoneNumber: String) {
        val builder = AlertDialog.Builder(c, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)
        dialogText.text = c.getString(R.string.delete_number, phoneNumber)
        builder.setView(dialogView)
            .setPositiveButton(R.string.yes) { _, _ ->
                sp.deletePhoneNumberFromList(phoneNumber)
                setAdapter()
            }
            .setNegativeButton(R.string.no) { _, _ ->
            }.show()
    }
}