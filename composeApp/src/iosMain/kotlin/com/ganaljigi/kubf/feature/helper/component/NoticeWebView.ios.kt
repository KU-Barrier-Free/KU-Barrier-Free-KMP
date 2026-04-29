package com.ganaljigi.kubf.feature.helper.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKWebView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NoticeWebView(
    url: String,
    modifier: Modifier,
) {
    UIKitView(
        factory = {
            WKWebView().apply {
                val nsUrl = NSURL.URLWithString(url)
                nsUrl?.let {
                    loadRequest(NSURLRequest.requestWithURL(it))
                }
            }
        },
        modifier = modifier.fillMaxSize(),
        update = { webView ->
            val nsUrl = NSURL.URLWithString(url)
            nsUrl?.let {
                webView.loadRequest(NSURLRequest.requestWithURL(it))
            }
        },
    )
}
