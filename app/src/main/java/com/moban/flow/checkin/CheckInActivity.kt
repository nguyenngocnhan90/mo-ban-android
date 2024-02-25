package com.moban.flow.checkin

import android.app.Activity
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.ViewGroup
import com.moban.R
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.enum.ScanType
import com.moban.extension.isValidUrl
import com.moban.flow.linkmart.detail.LinkmartDetailActivity
import com.moban.flow.linkmart.list.LinkmartListActivity
import com.moban.flow.webview.WebViewActivity
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_check_in.*
import kotlinx.android.synthetic.main.nav.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class CheckInActivity : BaseMvpActivity<ICheckInView, ICheckInPresenter>(), ICheckInView {
    override var presenter: ICheckInPresenter = CheckInPresenterIml()
    private lateinit var mScannerView: ZXingScannerView

    companion object {
        const val BUNDLE_KEY_CODE = "BUNDLE_KEY_CODE"
        fun start(context: Activity) {
            val intent = Intent(context, CheckInActivity::class.java)
            val bundle = Bundle()
            intent.putExtras(bundle)
            context.startActivityForResult(intent, Constant.QC_SCAN_CODE_REQUEST)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_check_in
    }

    override fun initialize(savedInstanceState: Bundle?) {
        setGAScreenName("Scan QR", GACategory.MORE)
        check_in_nav.nav_tvTitle.text = getString(R.string.qr_code)
        check_in_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        val contentFrame = check_in_scanner as ViewGroup
        mScannerView = ZXingScannerView(this)
        mScannerView.setResultHandler { result ->
            checkScanResult(result.text)
        }
        window.setFormat(PixelFormat.TRANSLUCENT)
        contentFrame.addView(mScannerView)

    }

    private fun checkScanResult(code: String) {
        val codeArr = code.split(":")
        if (codeArr.size == 2) {
            val type = codeArr[0]
            val value = codeArr[1]
            if (ScanType.from(type) == ScanType.LINKMART){
                val linkmartId = value.toIntOrNull()
                linkmartId?.let {
                    LinkmartDetailActivity.start(this@CheckInActivity, it)
                    finish()
                    return
                }
            } else if (ScanType.from(type) == ScanType.LINKMART_CATEGORY){
                val linkmartCateId = value.toIntOrNull()
                linkmartCateId?.let {
                    LinkmartListActivity.start(this@CheckInActivity, it)
                    finish()
                    return
                }
            }
        }

        if (code.isValidUrl()) {
            WebViewActivity.start(this, getString(R.string.app_name), code, oneTimeToken = true)
            finish()
            return
        }
        val intent = Intent()
        intent.putExtra(BUNDLE_KEY_CODE, code)
        setResult(Constant.QC_SCAN_CODE_REQUEST, intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }

    override fun onResume() {
        super.onResume()
        mScannerView.startCamera()
    }

    override fun onBackPressed() {
        mScannerView.stopCamera()
        super.onBackPressed()
    }
}
