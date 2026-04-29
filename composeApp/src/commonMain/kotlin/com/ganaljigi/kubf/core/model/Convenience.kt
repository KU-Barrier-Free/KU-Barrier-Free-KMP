package com.ganaljigi.kubf.core.model

import org.jetbrains.compose.resources.DrawableResource
import kubfandroid.composeapp.generated.resources.Res
import kubfandroid.composeapp.generated.resources.ic_convenience_store
import kubfandroid.composeapp.generated.resources.ic_feature_copy
import kubfandroid.composeapp.generated.resources.ic_feature_kcube
import kubfandroid.composeapp.generated.resources.ic_feature_it_service
import kubfandroid.composeapp.generated.resources.ic_feature_parking
import kubfandroid.composeapp.generated.resources.ic_feature_cafe
import kubfandroid.composeapp.generated.resources.ic_feature_elevator
import kubfandroid.composeapp.generated.resources.ic_feature_disable_toilet
import kubfandroid.composeapp.generated.resources.ic_feature_bank
import kubfandroid.composeapp.generated.resources.ic_feature_post
import kubfandroid.composeapp.generated.resources.ic_feature_culture
import kubfandroid.composeapp.generated.resources.ic_feature_welfare
import kubfandroid.composeapp.generated.resources.ic_feature_restaurant
import kubfandroid.composeapp.generated.resources.ic_feature_rest

enum class Convenience(
    val iconRes: DrawableResource,
    val label: String,
) {
    CONVENIENCE(
        iconRes = Res.drawable.ic_convenience_store,
        label = "편의점",
    ),
    COPY(
        iconRes = Res.drawable.ic_feature_copy,
        label = "복사실",
    ),
    K_CUBE(
        iconRes = Res.drawable.ic_feature_kcube,
        label = "K-CUBE",
    ),
    IT_SERVICE_CENTER(
        iconRes = Res.drawable.ic_feature_it_service,
        label = "IT-서비스센터",
    ),
    PARKING_LOT(
        iconRes = Res.drawable.ic_feature_parking,
        label = "주차장",
    ),
    CAFE(
        iconRes = Res.drawable.ic_feature_cafe,
        label = "카페",
    ),
    ELEVATOR(
        iconRes = Res.drawable.ic_feature_elevator,
        label = "엘리베이터",
    ),
    DISABLED_TOILET(
        iconRes = Res.drawable.ic_feature_disable_toilet,
        label = "장애인화장실",
    ),
    BANK(
        iconRes = Res.drawable.ic_feature_bank,
        label = "은행",
    ),
    POST_OFFICE(
        iconRes = Res.drawable.ic_feature_post,
        label = "우체국",
    ),
    CULTURE(
        iconRes = Res.drawable.ic_feature_culture,
        label = "문화시설",
    ),
    WELFARE_STORE(
        iconRes = Res.drawable.ic_feature_welfare,
        label = "복지매장",
    ),
    RESTAURANT(
        iconRes = Res.drawable.ic_feature_restaurant,
        label = "식당",
    ),
    K_HUB(
        iconRes = Res.drawable.ic_feature_kcube,
        label = "K-Hub",
    ),
    REST(
        iconRes = Res.drawable.ic_feature_rest,
        label = "휴게실",
    ),
}

fun fromLabel(label: String): Convenience? {
    return Convenience.entries.find { it.label == label }
}

fun getIconResByName(name: String): DrawableResource {
    return Convenience.entries.find { it.name.replace("_", "").contains(name) }?.iconRes
        ?: Res.drawable.ic_convenience_store
}
