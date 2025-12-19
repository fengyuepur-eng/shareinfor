package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.LinkRepository
import com.example.myapplication.ui.addedit.AddEditLinkScreen
import com.example.myapplication.ui.home.HomeScreen

object AppDestinations {
    const val HOME_ROUTE = "home"
    const val ADD_EDIT_LINK_ROUTE = "add_edit_link"
    const val URL_ARG = "url"
}

@Composable
fun AppNavigation(sharedUrl: String?) {
    val navController = rememberNavController()
    val linkRepository = remember { LinkRepository() }

    val startDestination = if (sharedUrl != null) {
        "${AppDestinations.ADD_EDIT_LINK_ROUTE}?${AppDestinations.URL_ARG}=$sharedUrl"
    } else {
        AppDestinations.HOME_ROUTE
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen(
                homeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = ViewModelFactory(linkRepository = linkRepository)
                ),
                navController = navController
            )
        }
        composable(
            route = "${AppDestinations.ADD_EDIT_LINK_ROUTE}?${AppDestinations.URL_ARG}={${AppDestinations.URL_ARG}}",
            arguments = listOf(navArgument(AppDestinations.URL_ARG) {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            AddEditLinkScreen(
                navController = navController,
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = ViewModelFactory(
                        linkRepository = linkRepository,
                        sharedUrl = backStackEntry.arguments?.getString(AppDestinations.URL_ARG)
                    )
                )
            )
        }
    }
}
