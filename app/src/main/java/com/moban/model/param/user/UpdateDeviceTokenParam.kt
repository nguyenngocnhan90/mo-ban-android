package com.moban.model.param.user

import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.helper.LocalStorage
import com.moban.util.Device

/**
 * Created by neal on 3/25/18.
 */
class UpdateDeviceTokenParam {
    var device_token: String? = LocalStorage.googleRegistrationId()
    val device_model : String = Device.getDeviceName()
    val platform = Constant.platform
    val app_version: String = Device.appVersion(LHApplication.instance.applicationContext)

    var one_signal_id: String? = LocalStorage.oneSignalId()
}
