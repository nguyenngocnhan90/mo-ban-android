package com.moban.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager

/**
 * Created by neal on 3/3/18.
 */

class NetworkUtil : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        //        ComponentName componentName = activityManager.getRunningTasks(1).get(0).topActivity;
    }

    companion object {

        /**
         * Check if device has internet connection
         *
         * @param context
         * @return
         */
        fun hasConnection(context: Context): Boolean {
            val available = isAvailable(context)

//            if (!available) {
//                DialogUtil.showErrorDialog(context,
//                        R.string.mgs_warning_no_interner,
//                        R.string.mgs_check_and_try_again)
//            }

            return available
        }

        fun isAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val wifiNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (wifiNetwork != null && wifiNetwork.isConnected) {
                return true
            }

            val mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (mobileNetwork != null && mobileNetwork.isConnected) {
                return true
            }

            val activeNetwork = connectivityManager.activeNetworkInfo
            return if (activeNetwork != null && activeNetwork.isConnected) {
                true
            } else false

        }

        /**
         * check if GPS is enable or not
         *
         * @param mLocationManager
         * @return
         */
        fun isGPSProviderEnabled(mLocationManager: LocationManager): Boolean {
            return if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                true
            } else false
        }
    }

}
