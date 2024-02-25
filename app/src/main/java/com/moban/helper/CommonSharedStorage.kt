package com.moban.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class CommonSharedStorage {

    private val name = "LinkHouseCommonSharedStorage"

    private val mSharedPreferences: SharedPreferences
    private val mEditor: SharedPreferences.Editor
    private val gson: Gson

    constructor(context: Context) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        mEditor = mSharedPreferences.edit()
        mEditor.apply()

        gson = Gson()
    }

    fun getString(key: String): String? {
        return mSharedPreferences.getString(key, null)
    }

    fun putString(key: String, value: String) {
        mEditor.putString(key, value)
        mEditor.commit()
    }
}
