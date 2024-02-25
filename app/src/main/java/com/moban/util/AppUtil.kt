package com.moban.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.moban.R
import com.moban.constant.Constant
import com.moban.enum.AppType


class AppUtil {
    companion object {
        fun openAppPackage(activity: Activity, packageName: String) {
            try {
                activity.startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")))
            } catch (anfe: android.content.ActivityNotFoundException) {
                activity.startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }

        fun isPackageExisted(context: Context, targetPackage: String): Boolean {
            try {
                context.packageManager.getPackageInfo(targetPackage,
                        PackageManager.GET_META_DATA)
            } catch (e: PackageManager.NameNotFoundException) {
                return false
            }

            return true
        }

        fun hasFacebookApp(context: Context): Boolean {
            return isPackageExisted(context, Constant.FACEBOOK_APP_PACKAGE_NAME)
        }

        fun getAppType(context: Context): AppType {
            if (Device.getPackageName(context) == context.getString(R.string.package_name) ||
                    Device.getPackageName(context) == context.getString(R.string.test_package_name)) {
                return AppType.LINKHOUSE
            }
            return AppType.CONNECT
        }

        fun openFacebookMessenger(context: Context, facebookId: String) {
            var intent =  try {
                context.packageManager.getPackageInfo(Constant.FACEBOOK_MESSAGE_PACKAGE_NAME, 0)
                Intent(Intent.ACTION_VIEW, Uri.parse(
                        "fb-messenger://user/$facebookId"))
            } catch (e: Exception) {
                Intent(Intent.ACTION_VIEW, Uri.parse(
                        "market://details?id=" + Constant.FACEBOOK_MESSAGE_PACKAGE_NAME))
            }

            var isError = false
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                isError = true
                intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://www.facebook.com/$facebookId"))
            }

            if (isError) {
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, context.getString(R.string.signIn_cannot_open_facebook_messenger),
                            Toast.LENGTH_LONG).show()
                }
            }
        }

        fun openSettingApp(context: Context) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", context.packageName, null)
            context.startActivity(intent)
        }

        fun openLocationSetting(context: Context) {
            val intent = Intent()
            intent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            context.startActivity(intent)
        }
    }
}
