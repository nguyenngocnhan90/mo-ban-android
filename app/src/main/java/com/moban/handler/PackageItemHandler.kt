package com.moban.handler

import com.moban.model.data.project.SalePackage

/**
 * Created by LenVo on 3/11/18.
 */
interface PackageItemHandler {
    fun onViewDetail(salePackage: SalePackage)
    fun onSubscribed(salePackage: SalePackage)
}
