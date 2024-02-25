package com.moban.enum

enum class DocumentType(val value: Int) {
    CHUNG_TU_GIAO_DICH(3),
    CMND(4),
    INVOICE(5),
    CMND_BACK(7),

    hopDong_MuaBan(8),
    phieuCoc(9),

    hoKhau_TrangBia(17),
    hoKhau_TrangChuHo(18),
    hoKhau_TrangKhach(19),

    cashInvoice(21);

    companion object {
        fun from(value: Int): ReservationImageType? = ReservationImageType.values().firstOrNull { it.value == value }
    }
}
