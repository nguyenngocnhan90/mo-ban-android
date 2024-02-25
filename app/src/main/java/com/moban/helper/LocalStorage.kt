package com.moban.helper

import com.moban.LHApplication
import com.moban.model.data.user.User
import com.moban.model.data.notification.Notification
import com.moban.model.data.user.Badge

/**
 * Created by neal on 3/3/18.
 */

class LocalStorage {

    companion object {
        private val config: Configuration = LHApplication.config

        fun saveBadges(badges: List<Badge>?) {
            config.putBadges(badges)
        }

        fun badges(): List<Badge>? {
            return config.getBadges()
        }

        fun updateNumFavoriteProduct(numFavorite: Int) {
            user()?.let {
                it.favorite_products_count = numFavorite
                LocalStorage.saveUser(it)
            }
        }

        fun saveSearchKeywordProject(keyword: String) {
            var currentList = config.getSearchKeywordProject()
            if (currentList == null) {
                currentList = ArrayList()
            }

            val keyArray = currentList.toMutableList()

            val keywordSet = HashSet(currentList)
            if (keywordSet.contains(keyword)) run {
                keyArray.remove(keyword)
            }
            if (keyArray.size >= 5) {
                keyArray.removeAt(0)
            }
            keyArray.add(keyword)

            config.putSearchKeywordProject(keyArray.toList())
        }

        fun searchKeywordSecondaryProject(): List<String>? {
            return config.getSearchKeywordSecondaryProject()?.reversed()
        }

        fun saveSearchKeywordSecondaryProject(keyword: String) {
            var currentList = config.getSearchKeywordSecondaryProject()
            if (currentList == null) {
                currentList = ArrayList()
            }

            val keyArray = currentList.toMutableList()

            val keywordSet = HashSet(currentList)
            if (keywordSet.contains(keyword)) run {
                keyArray.remove(keyword)
            }
            if (keyArray.size >= 5) {
                keyArray.removeAt(0)
            }
            keyArray.add(keyword)

            config.putSearchKeywordSecondaryProject(keyArray.toList())
        }

        fun searchKeywordProject(): List<String>? {
            return config.getSearchKeywordProject()?.reversed()
        }

        fun saveUser(user: User?) {
            config.putUser(user)
        }

        fun user(): User? {
            return config.getUser()
        }

        fun saveToken(token: String?) {
            config.putToken(token)
        }

        fun token(): String {
            return config.getToken()
        }

        fun saveGoogleRegistrationId(id: String?) {
            config.putGoogleRegistrationId(id)
        }

        fun googleRegistrationId(): String {
            return config.getGoogleRegistrationId()
        }

        fun saveOneSignalId(id: String?) {
            config.putOneSignalId(id)
        }

        fun oneSignalId(): String {
            return config.getOneSignalId()
        }

        fun saveNotification(notification: Notification?) {
            config.putNotification(notification)
        }

        fun savedNotification() : Notification? {
            return config.getNotification()
        }

        fun saveUnReadNotification(unRead: Int) {
            config.putUnReadNotification(unRead)
        }

        fun getUnReadNotification() : Int {
            return config.getUnReadNotification()
        }
    }

}
