package com.rsicarelli.homehunt.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.insets.statusBarsPadding
import com.rsicarelli.homehunt.presentation.favourites.FavouritesScreen
import com.rsicarelli.homehunt.presentation.home.components.HomeTopBar
import com.rsicarelli.homehunt.presentation.home.components.NavigationOptions
import com.rsicarelli.homehunt.presentation.map.MapScreen
import com.rsicarelli.homehunt.presentation.discover.DiscoverScreen
import com.rsicarelli.homehunt.presentation.filter.FilterScreen
import com.rsicarelli.homehunt.ui.navigation.Screen
import com.rsicarelli.homehunt.ui.state.AppState
import com.rsicarelli.homehunt.ui.theme.Green_500
import com.rsicarelli.homehunt.ui.theme.HomeHuntTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(appState: AppState) {
    HomeContent(
        onFilterClick = {
            appState.navigate(Screen.Filter.route)
        },
        favouritesScreen = { FavouritesScreen(appState = appState) },
        mapScreen = { MapScreen(appState = appState) },
        discoverScreen = { DiscoverScreen(appState = appState) },
        filterScreen = { FilterScreen(appState = appState) }
    )
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
private fun HomeContent(
    onFilterClick: () -> Unit,
    filterScreen: @Composable () -> Unit,
    discoverScreen: @Composable () -> Unit,
    favouritesScreen: @Composable () -> Unit,
    mapScreen: @Composable () -> Unit
) {

    val backdropState = rememberBackdropScaffoldState(initialValue = BackdropValue.Concealed)
    val selectedScreen = remember { mutableStateOf<Screen>(Screen.Discover) }
    var filterVisible by remember { mutableStateOf(false) }
    val coroutinesScope = rememberCoroutineScope()
    val contentVisibility = remember { mutableStateOf(true) }

    BackdropScaffold(
        modifier = Modifier.statusBarsPadding(),
        scaffoldState = backdropState,
        backLayerBackgroundColor = Green_500,
        gesturesEnabled = selectedScreen.value != Screen.Map,
        appBar = {
            HomeTopBar(
                coroutinesScope = coroutinesScope,
                backdropState = backdropState,
                currentDestination = selectedScreen.value,
                onFilterClick = {
                    coroutinesScope.launch {
                        if (filterVisible) {
                            backdropState.conceal()
                            filterVisible = false
                        } else {
                            filterVisible = true
                            backdropState.reveal()
                        }
                    }
                }
            )
        },
        backLayerContent = {
            if (filterVisible) {
                filterScreen()
            } else {
                NavigationOptions(selectedScreen.value) {
                    coroutinesScope.launch {
                        contentVisibility.value = false
                        backdropState.conceal()
                        selectedScreen.value = it
                        contentVisibility.value = true
                    }
                }
            }

        },
        frontLayerContent = {
            AnimatedVisibility(
                visible = contentVisibility.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                when (selectedScreen.value) {
                    Screen.Discover -> discoverScreen()
                    Screen.Map -> mapScreen()
                    Screen.Favourites -> favouritesScreen()
                }
            }
        }
    )
}

@Composable
@Preview()
private fun HomeScreenPreview() {
    HomeHuntTheme(isPreview = true) {
        HomeContent(

            onFilterClick = {},
            favouritesScreen = {},
            mapScreen = {},
            discoverScreen = {}, filterScreen = {

            }
        )
    }
}


