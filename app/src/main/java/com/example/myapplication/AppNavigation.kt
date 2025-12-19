package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.addedit.AddEditLinkScreen
import com.example.myapplication.ui.category.CategoryManagerScreen
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.home.LinkDetailScreen

object AppDestinations {
    const val HOME_ROUTE = "home"
    const val ADD_EDIT_LINK_ROUTE = "add_edit_link"
    const val CATEGORY_MANAGER_ROUTE = "category_manager"
    const val LINK_DETAIL_ROUTE = "link_detail"
    const val URL_ARG = "url"
    const val LINK_ID_ARG = "linkId"
}

@Composable
fun AppNavigation(sharedUrl: String?) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val linkRepository = remember { com.example.myapplication.data.LinkRepository(context.applicationContext) }
    val linkInfoFetcher = remember { com.example.myapplication.data.LinkInfoFetcher() }
    
    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME_ROUTE
    ) {
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen(
                navController = navController,
                homeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = ViewModelFactory(linkRepository = linkRepository, linkInfoFetcher = linkInfoFetcher)
                )
            )
        }
        composable(
            route = "${AppDestinations.ADD_EDIT_LINK_ROUTE}?${AppDestinations.URL_ARG}={${AppDestinations.URL_ARG}}&${AppDestinations.LINK_ID_ARG}={${AppDestinations.LINK_ID_ARG}}",
            arguments = listOf(
                navArgument(AppDestinations.URL_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(AppDestinations.LINK_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val linkIdArg = backStackEntry.arguments?.getString(AppDestinations.LINK_ID_ARG)
            AddEditLinkScreen(
                navController = navController,
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = ViewModelFactory(
                        linkRepository = linkRepository,
                        linkInfoFetcher = linkInfoFetcher,
                        sharedUrl = backStackEntry.arguments?.getString(AppDestinations.URL_ARG),
                        editingLinkId = linkIdArg
                    )
                ),
                url = backStackEntry.arguments?.getString(AppDestinations.URL_ARG)
            )
        }
        composable(AppDestinations.CATEGORY_MANAGER_ROUTE) {
            CategoryManagerScreen(
                navController = navController,
                categoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = ViewModelFactory(linkRepository = linkRepository, linkInfoFetcher = linkInfoFetcher)
                )
            )
        }
        composable(
            route = "${AppDestinations.LINK_DETAIL_ROUTE}/{${AppDestinations.LINK_ID_ARG}}",
            arguments = listOf(navArgument(AppDestinations.LINK_ID_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val linkId = backStackEntry.arguments?.getString(AppDestinations.LINK_ID_ARG)
            if (linkId != null) {
                LinkDetailScreen(
                    navController = navController,
                    linkId = linkId,
                    linkRepository = linkRepository
                )
            } else {
                navController.popBackStack()
            }
        }
    }
    
    // Handle shared URL navigation after NavHost is ready
    LaunchedEffect(sharedUrl) {
        if (!sharedUrl.isNullOrBlank()) {
            navController.navigate("${AppDestinations.ADD_EDIT_LINK_ROUTE}?${AppDestinations.URL_ARG}=$sharedUrl")
        }
    }
}
