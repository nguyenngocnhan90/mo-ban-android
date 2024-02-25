package com.moban.flow.secondary.create

import com.moban.model.data.BitmapUpload
import com.moban.model.data.address.City
import com.moban.model.data.address.District
import com.moban.model.data.secondary.constant.Attribute
import com.moban.model.data.secondary.constant.BasicConstantType
import com.moban.model.data.secondary.constant.HouseType
import com.moban.model.data.secondary.constant.ProjectBase
import com.moban.model.param.NewSecondaryProjectParam

class NewSecondaryData {
    companion object {
        var district: District? = null
        var city: City? = null
        var address: String = ""

        //Step 2
        var projectBase: ProjectBase? = null
        var houseType: HouseType? = null
        var targetHouse: BasicConstantType? = null
        var priceUnit: BasicConstantType? = null
        var feeUnit: BasicConstantType? = null
        var direction: BasicConstantType? = null
        var name: String = ""
        var area: Double? = null
        var price: Int? = null
        var fee: Int? = null
        var agentFee: Int? = null
        var house_texture: String = ""
        var house_rear_hatch: Double? = null
        var house_acreage: Double? = null
        var house_acreage_build: Double? = null
        var house_deck: Double? = null
        var house_bedroom: Int? = null
        var house_wc: Int? = null
        var house_description: String = ""

        //Step 3
        var house_photos: MutableList<BitmapUpload> = ArrayList()
        var house_image: BitmapUpload? = null
        var furnitures: MutableList<Attribute> = ArrayList()
        var utilities: MutableList<Attribute> = ArrayList()

        //Step 4
        var host_photos: MutableList<BitmapUpload> = ArrayList()
        var type: Attribute? = null

        fun reset() {
            district = null
            address = ""
            city = null
            projectBase = null
            houseType = null
            targetHouse = null
            priceUnit = null
            feeUnit = null
            direction = null
            name = ""
            area = null
            price = null
            fee = null
            agentFee = null
            house_texture = ""
            house_rear_hatch = null
            house_acreage = null
            house_acreage_build = null
            house_deck = null
            house_bedroom = null
            house_wc = null
            house_description = ""
            house_photos = ArrayList()
            house_image = null
            furnitures = ArrayList()
            utilities = ArrayList()
            host_photos = ArrayList()
            type = null
        }

        fun getNewSecondaryParam() : NewSecondaryProjectParam {
            val param = NewSecondaryProjectParam()
            param.district_id = district?.id
            param.house_address = address

            param.house_name = name
            param.house_usable_area = area
            param.house_target_type = targetHouse?.id

            param.house_type_id = houseType?.id

            param.house_price = price
            param.unit_house_price = priceUnit?.id

            param.host_rate = fee

            param.unit_host_rate = feeUnit?.id

            param.house_agent_rate = agentFee

            param.product_id = projectBase?.id

            param.house_texture = house_texture
            param.house_rear_hatch = house_rear_hatch
            param.house_direction = direction?.id

            param.house_acreage = house_acreage
            param.house_acreage_build = house_acreage_build
            param.house_deck = house_deck
            param.house_bedroom = house_bedroom
            param.house_wc = house_wc
            param.house_description = house_description

            param.house_image = house_image?.url

            for(image in house_photos) {
                param.house_photos.add(image.url)
            }

            for(image in host_photos) {
                param.host_photos.add(image.url)
            }

            for(attribute in furnitures) {
                param.attributes.add(attribute.id)
            }
            for(attribute in utilities) {
                param.attributes.add(attribute.id)
            }

            type?.let {
                param.attributes.add(it.id)
            }

            return param
        }
    }
}
