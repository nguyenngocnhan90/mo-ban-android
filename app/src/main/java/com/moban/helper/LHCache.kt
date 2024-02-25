package com.moban.helper

import com.moban.model.data.linkmart.LinkmartBrand
import com.moban.model.data.linkmart.LinkmartCategory
import com.moban.model.data.popup.Popup
import com.moban.model.data.post.Topic
import com.moban.model.data.project.Project
import com.moban.model.data.secondary.constant.AttributeType
import com.moban.model.data.secondary.constant.BasicConstantType
import com.moban.model.data.secondary.constant.HouseType
import com.moban.model.data.secondary.constant.ProjectBase

/**
 * Created by H. Len Vo on 9/25/18.
 */
class LHCache {
    var brands: List<LinkmartBrand> = ArrayList()
    var linkmartCategories: List<LinkmartCategory> = ArrayList()
    var linkHubCategories: List<LinkmartCategory> = ArrayList()
    var linkBookCategories: List<LinkmartCategory> = ArrayList()

    var highlightProject: List<Project> = ArrayList()
    var postTopics: List<Topic> = ArrayList()
    var postFilterTopics: List<Topic> = ArrayList()

    var oneTimeToken: String = ""
    var popup: Popup? = null

    var isLoadedSecondaryConstant = false
    var secondaryAttributes: List<AttributeType> = ArrayList()
    var secondaryBaseProjects: List<ProjectBase> = ArrayList()
    var houseTypes: List<HouseType> = ArrayList()
    var targetHouseTypes: List<BasicConstantType> = ArrayList()
    var priceUnits: List<BasicConstantType> = ArrayList()
    var agentPriceUnits: List<BasicConstantType> = ArrayList()
    var directions: List<BasicConstantType> = ArrayList()

    fun getFurnitureList(): AttributeType {
        return secondaryAttributes.filter { it.attribute_Type_Name == "Nội thất" }.first()
    }

    fun getUtilitiesList(): AttributeType {
        return secondaryAttributes.filter { it.attribute_Type_Name == "Tiện ích" }.first()
    }

    fun getTypeList(): AttributeType {
        return secondaryAttributes.filter { it.attribute_Type_Name == "Hình thức ký gởi" }.first()
    }
}
