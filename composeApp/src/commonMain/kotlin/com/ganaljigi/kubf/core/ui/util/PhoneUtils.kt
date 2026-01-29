package com.ganaljigi.kubf.core.ui.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun normalizeForDial(raw: String): String {
    val t = raw.trim()
    val out = StringBuilder()
    t.forEachIndexed { i, c ->
        if (c.isDigit() || (i == 0 && c == '+')) out.append(c)
    }
    return out.toString()
}

fun copyToClipboard(ctx: Context, text: String) {
    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("전화번호", text))
}
