package com.example.harmatek

interface SendSMSListener {
    fun onSMSSend(msg: String, list: ArrayList<Any>, passedFunction: ((ArrayList<Any>)->Unit)?)
}