package com.ganaljigi.kubf.core.ui.util

import dev.icerock.moko.permissions.PermissionsController

expect suspend fun PermissionsController.ensureLocationPermission(): Boolean

expect suspend fun getLocationWithPermission(
    permissionsController: PermissionsController,
): Pair<Double, Double>?
