package com.example.journalapplication.navigation

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
        composable(route = JournalEntryScreenDestination.route) {
            JournalEntryScreen { navController.navigateUp() }
        }
        composable(
            route = JournalEntryDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(JournalEntryDetailsDestination.journalIdArg) {
                type = NavType.IntType
            })
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
            })
        ) {
            JournalEntryEditScreen { navController.navigateUp() }
        }
    }
}