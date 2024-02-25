package com.moban.model.param.user

import com.moban.LHApplication
import com.moban.constant.Constant
import com.moban.helper.LocalStorage
import com.moban.util.Device

/**
 * Created by neal on 3/3/18.
 */
class LoginParam {
    var username: String = ""
    var password: String = ""

    var device_token: String? = LocalStorage.googleRegistrationId()
    var device_model : String? = Device.getDeviceName()
    val platform = Constant.platform
    val app_version = Device.appVersion(LHApplication.instance.applicationContext)
    var mac_address = Device.uniqueDeviceId(LHApplication.instance.applicationContext)

    var one_signal_id: String? = LocalStorage.oneSignalId()
}
