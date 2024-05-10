package com.example.journalapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.journalapplication.navigation.JournalApplicationNavHost

@Composable
fun JournalApp(navController: NavHostController = rememberNavController()) {
    JournalApplicationNavHost(navController = navController)
}