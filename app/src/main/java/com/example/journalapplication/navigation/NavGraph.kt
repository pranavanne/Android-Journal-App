package com.example.journalapplication.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.journalapplication.details.JournalEntryDetailsDestination
import com.example.journalapplication.details.JournalEntryDetailsScreen
import com.example.journalapplication.edit.JournalEntryEditDestination
import com.example.journalapplication.edit.JournalEntryEditScreen
import com.example.journalapplication.entry.JournalEntryScreen
import com.example.journalapplication.entry.JournalEntryScreenDestination
import com.example.journalapplication.home.HomeScreen
import com.example.journalapplication.home.HomeScreenDestination

@Composable
fun JournalApplicationNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreenDestination.route,
        modifier = Modifier
    ) {
        composable(route = HomeScreenDestination.route) {
            HomeScreen(
                navigateToEntryScreen = {navController.navigate(JournalEntryScreenDestination.route)},
                navigateToEntryDetailsScreen = {navController.navigate("${JournalEntryDetailsDestination.route}/${it}")}
            )

        }
        composable(
            route = JournalEntryScreenDestination.route,
            enterTransition = {
                slideInHorizontally(animationSpec = tween(durationMillis = 500)) { fullWidth ->
                    // Offsets the content by 1/3 of its width to the left, and slide towards right
                    // Overwrites the default animation with tween for this slide animation.
                    -fullWidth / 3
                } + fadeIn(
                    // Overwrites the default animation with tween
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessMedium)) {
                    fullWidth -> -fullWidth / 3
                } + fadeOut()
            }
        ) {
            JournalEntryScreen {
                navController.navigateUp()
            }
        }
        composable(
            route = JournalEntryDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(JournalEntryDetailsDestination.journalIdArg) {
                type = NavType.IntType
            }),
            enterTransition = {
                slideInHorizontally(animationSpec = tween(500)) {
                    fullWidth -> fullWidth / 3
                } + fadeIn(
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessMedium)) {
                    fullWidth -> fullWidth / 3
                } + fadeOut()
            }
        ) {
            JournalEntryDetailsScreen(
                navigateToEntryEditScreen = {navController.navigate("${JournalEntryEditDestination.route}/${it}")},
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = JournalEntryEditDestination.routeWithArgs,
            arguments = listOf(navArgument(JournalEntryEditDestination.journalIdArgs) {
                type = NavType.IntType
            }),
            enterTransition = {
                slideInHorizontally(animationSpec = tween(500)) {
                        fullWidth -> fullWidth / 3
                } + fadeIn(
                    animationSpec = tween(durationMillis = 500)
                )
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessMedium)) {
                        fullWidth -> fullWidth / 3
                } + fadeOut()
            }
        ) {
            JournalEntryEditScreen { navController.navigateUp() }
        }
    }
}