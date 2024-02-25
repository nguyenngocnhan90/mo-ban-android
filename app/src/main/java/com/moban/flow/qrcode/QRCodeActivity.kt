package com.moban.flow.qrcode

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.moban.R
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.flow.checkin.CheckInActivity
import com.moban.flow.linkmart.list.LinkmartListActivity
import com.moban.helper.LocalStorage
import com.moban.model.data.Checkin
import com.moban.model.param.user.CheckinParam
import com.moban.mvp.BaseMvpActivity
import com.moban.util.*
import kotlinx.android.synthetic.main.activity_qrcode.*
import kotlinx.android.synthetic.main.nav.view.*

class QRCodeActivity : BaseMvpActivity<IQRCodeView, IQRCodePresenter>(), IQRCodeView {
    override var presenter: IQRCodePresenter = QRCodePresenterIml()


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, QRCodeActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_qrcode
    }

    override fun initialize(savedInstanceState: Bundle?) {
        qr_code_nav.nav_tvTitle.text = getString(R.string.qr_code)
        qr_code_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        qc_code_btn_scan.setOnClickListener {
            if (Permission.checkPermissionCamera(this@QRCodeActivity)) {
                CheckInActivity.start(this@QRCodeActivity)
            }
        }

        loadQRCode()

        initLocationListener()

        setGAScreenName("QR Code", GACategory.MORE)
    }

    private fun initLocationListener() {
        CurrentLocationManager.checkLocationPermission(this@QRCodeActivity)
    }

    override fun onDestroy() {
        super.onDestroy()
        CurrentLocationManager.stopRequestLocationUpdates()
    }

    private fun loadQRCode() {
        LocalStorage.user()?.let {
            LoadQRCodeBitMap(this, it.id).execute()

            qc_code_employee_code.text = it.username
        }
    }

    override fun bindingQRCodeImage(bitmap: Bitmap) {
        qc_code_img.setImageBitmap(bitmap)
    }

    private class LoadQRCodeBitMap(iQRCodeView: IQRCodeView, id: Int) : AsyncTask<Void, Void, Bitmap?>() {
        val view: IQRCodeView = iQRCodeView

        val userId: Int = id

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg p0: Void?): Bitmap? {
            return QRCode.encodeAsBitmap(userId.toString())
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: Bitmap?) {
            result?.let {
                view.bindingQRCodeImage(it)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CurrentLocationManager.REQUEST_CODE_ACCESS_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                //denied

                DialogUtil.showErrorDialog(this, this.getString(R.string.no_location_permission),
                        com.moban.extension.getString(this, R.string.request_location_permission), this.getString(R.string.open_setting),
                        this.getString(R.string.skip), View.OnClickListener {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            )
                    ) {
                        //only deny
                        CurrentLocationManager.checkLocationPermission(this@QRCodeActivity)
                    } else {
                        //never ask again
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, CurrentLocationManager.REQUEST_CHECK_SETTINGS)
                    }
                }, null)
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CurrentLocationManager.requestLocationUpdates(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.QC_SCAN_CODE_REQUEST) {
            val code = data?.getStringExtra(CheckInActivity.BUNDLE_KEY_CODE) ?: return

            if (!Permission.checkPermissionLocation(this)) {
                return
            }

            val lastLocation = CurrentLocationManager.currentLocation ?: Location("")
            if (lastLocation.longitude == 0.0 || lastLocation.latitude == 0.0) {
                DialogUtil.showErrorDialog(this, getString(R.string.no_location_permission),
                        com.moban.extension.getString(this, R.string.request_location_permission), getString(R.string.open_setting),
                        getString(R.string.skip), View.OnClickListener {
                    if (Permission.checkLocationService(this)) {
                        AppUtil.openSettingApp(this)
                    } else {
                        AppUtil.openLocationSetting(this)
                    }
                }, null)
                return
            }

            val locationStr = lastLocation.latitude.toString() + "," + lastLocation.longitude.toString()

            val checkinParam = CheckinParam()
            LocalStorage.user()?.let {
                checkinParam.user_id = it.id
            }

            checkinParam.event_id = code
            checkinParam.location = locationStr
            Device.getLocalIp()?.let {
                checkinParam.ip = it
            }

            qccode_progress.visibility = View.VISIBLE
            presenter.checkIn(checkinParam)
        } else if (requestCode == Constant.LOCATION_REQUEST) {
            initLocationListener()
        }

        when (requestCode) {
            //case 1. After you allow the app access device location, Another dialog will be displayed to request you to turn on device location
            //case 2. Or You chosen Never Ask Again, you open device Setting and enable location permission
            CurrentLocationManager.REQUEST_CHECK_SETTINGS -> when (resultCode) {
                RESULT_OK -> {
                    Log.d("REQUEST_CHECK_SETTINGS", "RESULT_OK")
                    //case 1. You choose OK
                    CurrentLocationManager.startRequestLocationUpdate(applicationContext)
                }
                RESULT_CANCELED -> {
                    Log.d("REQUEST_CHECK_SETTINGS", "RESULT_CANCELED")
                    //case 1. You choose NO THANKS
                    //CurrentLocationManager.requestLocationUpdates(this)

                    //case 2. In device Setting screen: user can enable or not enable location permission,
                    // so when user back to this activity, we should re-call checkLocationPermission()
                    CurrentLocationManager.checkLocationPermission(this@QRCodeActivity)
                }
                else -> {
                    //do nothing
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun showCheckInFailed(message: String?) {
        qccode_progress.visibility = View.GONE
        DialogUtil.showErrorDialog(this, getString(R.string.check_in_fail),
                if(message.isNullOrEmpty()) getString(R.string.check_in_fail_desc) else message, getString(R.string.ok), null)
    }

    override fun showCheckInSuccess(data: Checkin?, message: String?) {
        qccode_progress.visibility = View.GONE

        val title = if (!message.isNullOrEmpty()) message else getString(R.string.check_in_success_desc)

        var dialog: Dialog? = null
        dialog = DialogUtil.showInfoDialog(this, getString(R.string.check_in_success),
                title, getString(R.string.ok), View.OnClickListener {
            dialog?.dismiss()
            navigate(data)
        })
    }

    private fun navigate(checkin: Checkin?) {
        checkin?.let {
            it.linkmart_category?.let {cate ->
                if (cate.id > 0) {
                    LinkmartListActivity.start(this@QRCodeActivity, cate.id)
                }
            }
        }
    }
}
