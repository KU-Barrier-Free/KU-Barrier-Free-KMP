package com.ganaljigi.kubf.core.ui.util

fun normalizeForDial(raw: String): String {
    val t = raw.trim()
    val out = StringBuilder()
    t.forEachIndexed { i, c ->
        if (c.isDigit() || (i == 0 && c == '+')) out.append(c)
    }
    return out.toString()
}
