package com.ganaljigi.kubf.feature.building.model

import org.jetbrains.compose.resources.DrawableResource
import kubfandroid.composeapp.generated.resources.Res
import kubfandroid.composeapp.generated.resources.ic_feature_cafe
import kubfandroid.composeapp.generated.resources.ic_feature_conv
import kubfandroid.composeapp.generated.resources.ic_feature_print
import kubfandroid.composeapp.generated.resources.ic_feature_rest
import kubfandroid.composeapp.generated.resources.ic_feature_kcube
import kubfandroid.composeapp.generated.resources.ic_feature_itser
import kubfandroid.composeapp.generated.resources.ic_feature_park
import kubfandroid.composeapp.generated.resources.ic_feature_bank
import kubfandroid.composeapp.generated.resources.ic_feature_elevator
import kubfandroid.composeapp.generated.resources.ic_feature_post
import kubfandroid.composeapp.generated.resources.ic_feature_disable_toilet
import kubfandroid.composeapp.generated.resources.ic_feature_culture
import kubfandroid.composeapp.generated.resources.ic_feature_welfare
import kubfandroid.composeapp.generated.resources.ic_feature_restaurant

enum class Facility(val label: String, val iconResId: DrawableResource) {
    CAFE("카페", Res.drawable.ic_feature_cafe),
    CONV("편의점", Res.drawable.ic_feature_conv),
    PRINT("복사실", Res.drawable.ic_feature_print),
    REST("휴게실", Res.drawable.ic_feature_rest),
    KCUBE("K-CUBE", Res.drawable.ic_feature_kcube),
    KHUB("K-Hub", Res.drawable.ic_feature_kcube),
    SERVICE("IT-서비스센터", Res.drawable.ic_feature_itser),
    PARK("주차장", Res.drawable.ic_feature_park),
    BANK("은행", Res.drawable.ic_feature_bank),
    ELEV("엘리베이터", Res.drawable.ic_feature_elevator),
    POST("우체국", Res.drawable.ic_feature_post),
    TOILET("장애인화장실", Res.drawable.ic_feature_disable_toilet),
    CULF("문화시설", Res.drawable.ic_feature_culture),
    STORE("복지매장", Res.drawable.ic_feature_welfare),
    RES("식당", Res.drawable.ic_feature_restaurant),
}
