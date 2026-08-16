package com.ganaljigi.kubf.core.analytics

interface AnalyticsLogger {
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
}
enum class AnalyticsScreen(val screenName: String) {
    HOME("home"),
    HELPER("helper"),
    BUILDING("building"),
    ROOM("room"),
}

enum class AnalyticsPinType(val value: String) {
    BUILDING("building"),
    GATE("gate"),
    SPECIAL_MARK("special_mark"),
}
