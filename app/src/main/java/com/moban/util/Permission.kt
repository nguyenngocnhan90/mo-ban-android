package com.moban.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.view.View
import com.moban.R
import com.moban.constant.Constant
import com.moban.extension.getString


class Permission {
    companion object {

        fun checkPermissionReadExternalStorage(context: Context): Boolean {
            val currentAPIVersion = Build.VERSION.SDK_INT

            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    context as Activity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        DialogUtil.showErrorDialog(context, getString(context, R.string.ohio_request),
                                getString(context, R.string.read_storage_request), context.getString(R.string.open_setting),
                                context.getString(R.string.skip), View.OnClickListener {
                            AppUtil.openSettingApp(context)
                        }, null)
                    } else {
                        ActivityCompat.requestPermissions(context,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constant.READ_EXTERNAL_STORAGE_REQUEST)
                    }

                    return false
                }

                return true
            }

            return true
        }

        fun checkPermissionWriteExternalStorage(context: Context): Boolean {
            val currentAPIVersion = Build.VERSION.SDK_INT

            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    context as Activity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        DialogUtil.showErrorDialog(context, getString(context, R.string.ohio_request),
                                getString(context, R.string.read_storage_request), context.getString(R.string.open_setting),
                                context.getString(R.string.skip), View.OnClickListener {
                            AppUtil.openSettingApp(context)
                        }, null)
                    } else {
                        ActivityCompat.requestPermissions(context,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), Constant.WRITE_EXTERNAL_STORAGE_REQUEST)
                    }

                    return false
                }

                return true
            }

            return true
        }

        fun checkPermissionNotification(context: Context): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        fun checkPermissionCamera(context: Context): Boolean {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                context as Activity,
                                Manifest.permission.CAMERA)) {
                    DialogUtil.showErrorDialog(context, context.getString(R.string.no_camera_permission),
                            getString(context, R.string.request_camera_permission), context.getString(R.string.open_setting),
                            context.getString(R.string.skip), View.OnClickListener {
                        AppUtil.openSettingApp(context)
                    }, null)
                } else {
                    ActivityCompat.requestPermissions(context,
                            arrayOf(Manifest.permission.CAMERA), Constant.CAMERA_REQUEST)
                }
                return false
            }

            return true
        }

        fun checkLocationService(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

        fun checkPermissionLocation(context: Context, showDialog: Boolean = true): Boolean {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                context as Activity,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (showDialog) {
                        DialogUtil.showErrorDialog(context, context.getString(R.string.no_location_permission),
                                getString(context, R.string.request_location_permission), context.getString(R.string.open_setting),
                                context.getString(R.string.skip), View.OnClickListener {
                            AppUtil.openSettingApp(context)
                        }, null)
                    }
                } else {
                    if (showDialog) {
                        ActivityCompat.requestPermissions(context,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), Constant.LOCATION_REQUEST)
                    }
                }
                return false
            }

            if (!checkLocationService(context)) {
                if (showDialog) {
                    DialogUtil.showErrorDialog(context, context.getString(R.string.no_location_permission),
                            getString(context, R.string.request_location_permission), context.getString(R.string.open_setting),
                            context.getString(R.string.skip), View.OnClickListener {
                        AppUtil.openLocationSetting(context)
                    }, null)
                }
                return false
            }

            return true
        }

        fun openCalling(context: Context, phoneNumber: String) {
            val intent = Intent()
            intent.action = Intent.ACTION_DIAL
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION

            intent.data = Uri.parse("tel:$phoneNumber")
            context.startActivity(intent)
        }
    }

}
