package com.moban.model.data.secondary

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.Photo

class SecondaryHouse: BaseModel() {
    var id: Int = 0

    @SerializedName("district_Name")
    var district_Name: String = ""

    @SerializedName("city_Name")
    var city_Name: String = ""

    var price: String = ""

    @SerializedName("contract_code")
    var contract_code: String = ""

    @SerializedName("agent_Money")
    var agent_Money: String = ""

    @SerializedName("new_House")
    var new_House: String = ""

    @SerializedName("docquyen")
    var docquyen: String = ""

    @SerializedName("house_BedRoom")
    var house_BedRoom: Double = 0.0

    @SerializedName("house_Acreage")
    var house_Acreage: Double = 0.0

    @SerializedName("house_Texture")
    var house_Texture: String = ""

    @SerializedName("house_Deck")
    var house_Deck: Int = 0

    @SerializedName("house_Direction")
    var house_Direction: String = ""

    @SerializedName("booking")
    var booking: Int = 0

    @SerializedName("like")
    var like: Int = 0

    @SerializedName("phone")
    var phone: String = ""

    var image: String = ""

    @SerializedName("photos")
    var photos: List<Photo> = ArrayList()

    fun fullAddress(): String {
        return ( "$district_Name, $city_Name")
    }

    fun house_Acreage_String(): String {
        return if (house_Acreage.toInt() == 0)  "--" else (house_Acreage.toString() + " m2")
    }

    fun house_BedRoom_String(): String {
        return if(house_BedRoom.toInt() == 0) "--" else house_BedRoom.toString()
    }

    fun house_Deck_String(): String {
        return if(house_Deck == 0)  "--" else house_Deck.toString()
    }

    fun house_Texture_String(): String {
        return if(house_Texture.isNullOrEmpty()) "--" else house_Texture
    }

    fun house_Direction_String(): String {
        return if(house_Direction.isNullOrEmpty()) "--" else house_Direction
    }

    fun phone_String(): String {
        return if(phone.isNullOrEmpty()) "--" else phone
    }
}
