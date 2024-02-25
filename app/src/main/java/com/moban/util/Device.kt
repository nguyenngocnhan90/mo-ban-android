package com.moban.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException


/**
 * Created by neal on 3/3/18.
 */
class Device {
    companion object {

        fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

        fun uniqueDeviceId(context: Context): String {
            return Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ANDROID_ID)
        }

        fun getVersionCode(context: Context): Int {
            var versionCode = 0
            try {
                val pInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0)
                versionCode = pInfo.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return versionCode
        }

        fun getPackageName(context: Context): String {
            var packageName = ""
            try {
                val pInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0)
                packageName = pInfo.packageName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return packageName
        }

        fun appVersion(context: Context): String {
            val manager = context.packageManager
            try {
                val packageInfo = manager.getPackageInfo(
                        context.packageName, 0)
                return packageInfo.versionName
            } catch (e: Exception) {

            }

            return ""
        }

        fun getScreenWidth(): Int {
            return Resources.getSystem().displayMetrics.widthPixels
        }

        fun getScreenHeight(): Int {
            return Resources.getSystem().displayMetrics.heightPixels
        }

        private fun capitalize(s: String?): String {
            if (s == null || s.length == 0) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                Character.toUpperCase(first) + s.substring(1)
            }
        }

        fun getLocalIp(): String? {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            } catch (ex: SocketException) {
                ex.printStackTrace()
            }
            return null
        }
    }
}
