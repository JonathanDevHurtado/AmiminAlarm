package com.amimin.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amimin.app.ui.screens.alarm.AlarmScreen
import com.amimin.app.ui.screens.calendar.CalendarScreen
import com.amimin.app.ui.screens.home.HomeScreen
import com.amimin.app.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Alarm : Screen("alarm")
    data object Calendar : Screen("calendar")
    data object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(500)) + slideInHorizontally(
                initialOffsetX = { it / 3 },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(500)) + slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(500)) + slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(500)) + slideOutHorizontally(
                targetOffsetX = { it / 3 },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAlarm = { navController.navigate(Screen.Alarm.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Alarm.route) {
            AlarmScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
