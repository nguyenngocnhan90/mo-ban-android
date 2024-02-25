package com.moban.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.moban.model.data.notification.Notification
import com.moban.model.data.user.Badge
import com.moban.model.data.user.User

/**
 * Created by neal on 3/3/18.
 */
class Configuration {

    private val name = "LinkHouse"

    private val mSharedPreferences: SharedPreferences
    private val mEditor: SharedPreferences.Editor
    private val gson: Gson
    
    constructor(context: Context) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        mEditor = mSharedPreferences.edit()
        mEditor.apply()

        gson = Gson()
    }

    fun putUser(user: User?) {
        if (user == null) {
            mEditor.remove(StorageKey.user)
            mEditor.apply()
            return
        }

        mEditor.putString(StorageKey.user, gson.toJson(user))
        mEditor.commit()
    }

    fun putBadges(badges: List<Badge>?) {
        if (badges == null) {
            mEditor.remove(StorageKey.badges)
            mEditor.apply()
            return
        }

        mEditor.putString(StorageKey.badges, gson.toJson(badges))
        mEditor.commit()
    }

    fun getBadges(): List<Badge>? {
        val json = mSharedPreferences.getString(StorageKey.badges, "")
        if (json?.isEmpty() == true) {
            return null
        }
        val arrBadges = gson.fromJson<Array<Badge>>(json, Array<Badge>::class.java)
        return arrBadges.asList()
    }

    fun putSearchKeywordProject(keywords: List<String>?) {
        if (keywords == null) {
            mEditor.remove(StorageKey.project_keywords)
            mEditor.apply()
            return
        }

        mEditor.putString(StorageKey.project_keywords, gson.toJson(keywords))
        mEditor.commit()
    }

    fun getSearchKeywordProject(): List<String>? {
        val json = mSharedPreferences.getString(StorageKey.project_keywords, "")
        if (json?.isEmpty() == true) {
            return null
        }
        val arrBadges = gson.fromJson<Array<String>>(json, Array<String>::class.java)
        return arrBadges.asList()
    }

    fun putSearchKeywordSecondaryProject(keywords: List<String>?) {
        if (keywords == null) {
            mEditor.remove(StorageKey.secondary_project_keywords)
            mEditor.apply()
            return
        }

        mEditor.putString(StorageKey.secondary_project_keywords, gson.toJson(keywords))
        mEditor.commit()
    }

    fun getSearchKeywordSecondaryProject(): List<String>? {
        val json = mSharedPreferences.getString(StorageKey.secondary_project_keywords, "")
        if (json?.isEmpty() == true) {
            return null
        }
        val arrBadges = gson.fromJson<Array<String>>(json, Array<String>::class.java)
        return arrBadges.asList()
    }

    fun getUser(): User? {
        val json = mSharedPreferences.getString(StorageKey.user, "")
        if (json?.isEmpty() == true) {
            return null
        }

        return gson.fromJson<User>(json, User::class.java)
    }

    fun putToken(token: String?) {
        if (token == null) {
            mEditor.remove(StorageKey.token)
            mEditor.apply()
            return
        }

        mEditor.putString(StorageKey.token, token)
        mEditor.commit()
    }

    fun getToken(): String {
        return mSharedPreferences.getString(StorageKey.token, "") ?: ""
    }

    fun putLinkmartToken(token: String?) {
        if (token == null) {
            mEditor.remove(StorageKey.linkmart_token)
            mEditor.apply()
            return
        }

        mEditor.putString(StorageKey.linkmart_token, token)
        mEditor.commit()
    }

    fun getLinkmartToken(): String {
        return mSharedPreferences.getString(StorageKey.linkmart_token, "") ?: ""
    }

    fun putGoogleRegistrationId(id: String?) {
        if (id == null) {
            mEditor.remove(StorageKey.google_registration_id)
            mEditor.apply()
            return
        }

        mEditor.putString(StorageKey.google_registration_id, id)
        mEditor.commit()
    }

    fun getGoogleRegistrationId(): String {
        return mSharedPreferences.getString(StorageKey.google_registration_id, "") ?: ""
    }


    fun putOneSignalId(id: String?) {
        if (id == null) {
            mEditor.remove(StorageKey.one_signal_id)
            mEditor.apply()
            return
        }

        mEditor.putString(StorageKey.one_signal_id, id)
        mEditor.commit()
    }

    fun getOneSignalId(): String {
        return mSharedPreferences.getString(StorageKey.one_signal_id, "") ?: ""
    }

    fun putNotification(notification: Notification?) {
        if (notification == null) {
            mEditor.remove(StorageKey.saved_notification)
            mEditor.apply()
            return
        }

        mEditor.putString(StorageKey.saved_notification, gson.toJson(notification))
        mEditor.commit()
    }

    fun putUnReadNotification(unReadNotification: Int) {
        mEditor.putInt(StorageKey.unread_notification, unReadNotification)
        mEditor.commit()
    }

    fun getUnReadNotification() : Int {
        return mSharedPreferences.getInt(StorageKey.unread_notification, 0)
    }

    fun getNotification(): Notification? {
        val json = mSharedPreferences.getString(StorageKey.saved_notification, "")
        if (json?.isEmpty() == true) {
            return null
        }

        return gson.fromJson<Notification>(json, Notification::class.java)
    }

    fun putShowedReview(month: Int) {
        mEditor.putInt(StorageKey.showed_review, month)
        mEditor.commit()
    }

    fun getShowedReview() : Int {
        return mSharedPreferences.getInt(StorageKey.showed_review, -1)
    }
}
