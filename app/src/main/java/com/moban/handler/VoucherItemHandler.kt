package com.moban.handler

import com.moban.model.data.linkmart.Voucher

/**
 * Created by LenVo on 8/16/18.
 */
interface VoucherItemHandler {
    fun onClicked(voucher: Voucher)
}
