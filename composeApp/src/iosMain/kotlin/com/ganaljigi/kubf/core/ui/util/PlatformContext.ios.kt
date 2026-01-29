package com.ganaljigi.kubf.core.ui.util

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard

actual class PlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext = PlatformContext()

actual fun PlatformContext.showToast(message: String) {
    // TODO: iOS Toast 구현 (UIAlertController 또는 커스텀 뷰)
    println("Toast: $message")
}

actual fun PlatformContext.copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
}

actual fun PlatformContext.openPhoneDialer(phoneNumber: String) {
    val url = NSURL.URLWithString("tel:$phoneNumber")
    url?.let {
        UIApplication.sharedApplication.openURL(it)
    }
}

actual fun PlatformContext.openAppSettings() {
    val url = NSURL.URLWithString("app-settings:")
    url?.let {
        UIApplication.sharedApplication.openURL(it)
    }
}
