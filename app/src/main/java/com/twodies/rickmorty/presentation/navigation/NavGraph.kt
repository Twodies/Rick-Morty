package com.twodies.rickmorty.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.twodies.rickmorty.presentation.characters.CharactersScreen
import com.twodies.rickmorty.presentation.characters.CharactersViewModel
import com.twodies.rickmorty.presentation.detail.CharacterDetailScreen
import com.twodies.rickmorty.presentation.filter.FilterScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CharacterList.route
    ) {
        composable(route = Screen.CharacterList.route) { backStackEntry ->
            val viewModel: CharactersViewModel = hiltViewModel(backStackEntry)
            Log.d("NavGraph", "CharactersScreen - ViewModel instance: ${System.identityHashCode(viewModel)}")
            CharactersScreen(
                onCharacterClick = { characterId ->
                    navController.navigate(Screen.CharacterDetail.createRoute(characterId))
                },
                onFilterClick = {
                    navController.navigate(Screen.Filter.route)
                },
                viewModel = viewModel
            )
        }

        composable(
            route = Screen.CharacterDetail.route,
            arguments = listOf(
                navArgument("characterId") {
                    type = NavType.IntType
                }
            )
        ) {
            CharacterDetailScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable(route = Screen.Filter.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.CharacterList.route)
            }
            val viewModel: CharactersViewModel = hiltViewModel(parentEntry)
            Log.d("NavGraph", "FilterScreen - ViewModel instance: ${System.identityHashCode(viewModel)}")
            Log.d("NavGraph", "FilterScreen - Using ViewModel from CharacterList backstack entry")

            FilterScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onApplyFilter = {
                    navController.navigateUp()
                },
                viewModel = viewModel
            )
        }
    }
}
