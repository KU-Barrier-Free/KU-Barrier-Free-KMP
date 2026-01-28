package com.ganaljigi.kubf.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ganaljigi.kubf.feature.building.screen.BuildingInfoScreen
import com.ganaljigi.kubf.feature.helper.screen.DisableStudentHelperScreen
import com.ganaljigi.kubf.feature.helper.screen.HelperScreen
import com.ganaljigi.kubf.feature.helper.screen.JobInformationScreen
import com.ganaljigi.kubf.feature.helper.screen.SupportScreen
import com.ganaljigi.kubf.feature.home.screen.HomeRoute
import com.ganaljigi.kubf.feature.room.RoomInfoScreen

@Composable
fun MainNavHost(
    padding: PaddingValues,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
    ) {
        composable<Routes.Splash> {
//            SplashScreen(
//                padding = padding,
//                navigateToHome = {
//                    navController.navigate(Routes.Home) {
//                        popUpTo(Routes.Splash) { inclusive = true }
//                    }
//                },
//            )
        }

        composable<Routes.Home> {
            HomeRoute(
                padding = padding,
                navigateToHelper = { navController.navigate(Routes.Helper) },
                navigateToBuildingInfo = { navController.navigate(Routes.BuildingInfo(it)) },
            )
        }

        composable<Routes.Helper> {
            HelperScreen(
//                padding = padding,
//                navigateToNotice = { navController.navigate(Routes.Notice) },
                onBackClick = { navController.popBackStack() },
                navigateToDisableStudentHelper = { navController.navigate(Routes.DisableStudentHelper) },
                navigateToSupport = { navController.navigate(Routes.Support) },
                navigateToJobInformation = { navController.navigate(Routes.JobInformation) },
            )
        }

        composable<Routes.Notice> {
//            NoticeScreen(
//                padding = padding,
//                navigateBack = { navController.popBackStack() },
//            )
        }

        composable<Routes.BuildingInfo> { navBackStackEntry ->
            val route = navBackStackEntry.toRoute<Routes.BuildingInfo>()

            BuildingInfoScreen(
                onRoomClick = { room, buildingName ->
                    navController.navigate(
                        Routes.RoomInfo(
                            buildingId = route.number,
                            spaceId = room.id,
                            type = if (room.isLecture) 1 else 0,
                            buildingName = buildingName,
                        ),
                    )
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable<Routes.RoomInfo> { navBackStackEntry ->
            RoomInfoScreen(
                onBackClick = { navController.popBackStack() },
            )
        }

        composable<Routes.DisableStudentHelper> {
            DisableStudentHelperScreen(onBackClick = { navController.popBackStack() })
        }

        composable<Routes.Support> {
            SupportScreen(onBackClick = { navController.popBackStack() })
        }

        composable<Routes.JobInformation> {
            JobInformationScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
