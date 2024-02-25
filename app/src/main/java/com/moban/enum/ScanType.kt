package com.moban.enum

/**
 * Created by LenVo on 7/29/18.
 */
enum class ScanType (val value: String) {
    LINKMART("linkmart"),
    LINKMART_CATEGORY("linkmart-category"),
    NONE("none");

    companion object {
        fun from(type: String): ScanType {
            return when(type) {
                "linkmart" -> LINKMART
                "linkmart-category" -> LINKMART_CATEGORY
                else -> NONE
            }
        }
    }
}
