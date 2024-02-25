package com.moban.enum

enum class ReservationImageType(val value: Int) {
    BankInvoice(1),
    CashInvoice(2),

    Cmnd(3),
    CmndBack(4),

    HoKhau_TrangBia(5),
    HoKhau_TrangChuHo(6),
    HoKhau_TrangKhach(7);

    companion object {
        fun from(value: Int): ReservationImageType = values().first { it.value == value }
    }
}
