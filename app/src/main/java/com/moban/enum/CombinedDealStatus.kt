package com.moban.enum

class CombinedDealStatus {
    var dealStatus: DealStatus? = null
    var approvedStatus: ApproveStatus? = null
    var name: String = ""

    companion object {
        fun all(): CombinedDealStatus {
            val status = CombinedDealStatus()
            status.name = "Tất cả"

            return status
        }

        fun waitingForApprove(): CombinedDealStatus {
            val status = CombinedDealStatus()
            status.dealStatus = DealStatus.BOOKED
            status.approvedStatus = ApproveStatus.Waiting
            status.name = "Chờ Duyệt"

            return status
        }

        fun datCho(): CombinedDealStatus {
            val status = CombinedDealStatus()
            status.dealStatus = DealStatus.BOOKED
            status.approvedStatus = ApproveStatus.Approved
            status.name = "Đặt Chỗ"

            return status
        }

        fun cocChoDuyet(): CombinedDealStatus {
            val status = CombinedDealStatus()
            status.dealStatus = DealStatus.DEPOSITED
            status.approvedStatus = ApproveStatus.Waiting
            status.name = "Chuyển Cọc Chờ Duyệt"

            return status
        }

        fun coc(): CombinedDealStatus {
            val status = CombinedDealStatus()
            status.dealStatus = DealStatus.DEPOSITED
            status.approvedStatus = ApproveStatus.Approved
            status.name = "Đã Cọc"

            return status
        }

        fun hopDongChoDuyet(): CombinedDealStatus {
            val status = CombinedDealStatus()
            status.dealStatus = DealStatus.CONTRACTED
            status.approvedStatus = ApproveStatus.Waiting
            status.name = "Hợp Đồng Chờ Duyệt"

            return status
        }

        fun hopDong(): CombinedDealStatus {
            val status = CombinedDealStatus()
            status.dealStatus = DealStatus.CONTRACTED
            status.approvedStatus = ApproveStatus.Approved
            status.name = "Hợp Đồng"

            return status
        }
    }
}