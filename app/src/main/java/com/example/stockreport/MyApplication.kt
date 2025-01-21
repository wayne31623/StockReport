package com.example.stockreport

import android.app.Application
import android.content.Context

class MyApplication: Application() {
    companion object {
        lateinit var mContext: Context
    }

    init {
        mContext = this
    }
}