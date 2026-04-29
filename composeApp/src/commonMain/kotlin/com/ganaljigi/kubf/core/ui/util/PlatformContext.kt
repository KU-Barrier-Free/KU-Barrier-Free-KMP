package com.ganaljigi.kubf.core.ui.util

import androidx.compose.runtime.Composable

/**
 * Platform context for platform-specific operations
 */
expect class PlatformContext

@Composable
expect fun getPlatformContext(): PlatformContext

/**
 * Show a short toast message
 */
expect fun PlatformContext.showToast(message: String)

/**
 * Copy text to clipboard
 */
expect fun PlatformContext.copyToClipboard(text: String)

/**
 * Open phone dialer
 */
expect fun PlatformContext.openPhoneDialer(phoneNumber: String)

/**
 * Open app settings
 */
expect fun PlatformContext.openAppSettings()
