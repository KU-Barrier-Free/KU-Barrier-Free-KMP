package com.ganaljigi.kubf.core.analytics

class AnalyticsTracker(
    private val logger: AnalyticsLogger,
) {
    fun logScreenView(
        screen: AnalyticsScreen,
        params: Map<String, Any> = emptyMap(),
    ) {
        logger.logEvent(
            name = "screen_view",
            params = mapOf(
                "firebase_screen" to screen.screenName,
                "firebase_screen_class" to screen.screenName,
            ) + params,
        )
    }

    fun logMapPinClick(
        pinType: AnalyticsPinType,
        pinId: Long,
        pinName: String? = null,
    ) {
        logger.logEvent(
            name = "map_pin_click",
            params = mapOfNotNullValues(
                "pin_type" to pinType.value,
                "pin_id" to pinId,
                "pin_name" to pinName,
            ),
        )
    }

    fun logMapToggleClick(toggle: String, selected: Boolean) {
        logger.logEvent(
            name = "map_toggle_click",
            params = mapOf(
                "toggle" to toggle,
                "selected" to selected,
            ),
        )
    }

    fun logFindWayClick(hasFrom: Boolean, hasTo: Boolean) {
        logger.logEvent(
            name = "find_way_click",
            params = mapOf(
                "has_from" to hasFrom,
                "has_to" to hasTo,
            ),
        )
    }

    fun logBuildingBottomSheetClick(
        buildingId: Long,
        buildingName: String? = null,
    ) {
        logger.logEvent(
            name = "building_bottom_sheet_click",
            params = mapOfNotNullValues(
                "building_id" to buildingId,
                "building_name" to buildingName,
            ),
        )
    }

    fun logBuildingRoomClick(
        buildingId: Long,
        buildingName: String? = null,
        roomId: Long,
        roomNumber: String? = null,
        roomName: String? = null,
    ) {
        logger.logEvent(
            name = "building_room_click",
            params = mapOfNotNullValues(
                "building_id" to buildingId,
                "building_name" to buildingName,
                "room_id" to roomId,
                "room_number" to roomNumber,
                "room_name" to roomName,
            ),
        )
    }

    private fun mapOfNotNullValues(vararg pairs: Pair<String, Any?>): Map<String, Any> =
        pairs.mapNotNull { (key, value) ->
            value?.let { key to it }
        }.toMap()
}
