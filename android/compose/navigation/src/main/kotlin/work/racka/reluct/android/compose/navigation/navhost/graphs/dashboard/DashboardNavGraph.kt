package work.racka.reluct.android.compose.navigation.navhost.graphs.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.composable
import timber.log.Timber
import work.racka.reluct.android.compose.components.ComponentsPreview
import work.racka.reluct.android.compose.components.buttons.AddButton
import work.racka.reluct.android.compose.components.tab.dashboard.DashboardTabBar
import work.racka.reluct.android.compose.components.textfields.search.ReluctSearchBar
import work.racka.reluct.android.compose.components.topBar.CollapsingToolbarBase
import work.racka.reluct.android.compose.components.topBar.ProfilePicture
import work.racka.reluct.common.compose.destinations.DashboardDestinations
import work.racka.reluct.common.compose.destinations.navbar.Graphs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@ExperimentalAnimationApi
internal fun NavGraphBuilder.dashboardNavGraph(
    navController: NavHostController,
) {
    navigation(
        route = Graphs.DashboardDestinations.route,
        startDestination = DashboardDestinations.Overview.route
    ) {
        Timber.d("Dashboard screen called")
        // Overview
        composable(
            route = DashboardDestinations.Overview.route
        ) {
            val tabPage = remember {
                mutableStateOf(DashboardDestinations.Overview)
            }
            val listState = rememberLazyListState()
            val buttonExpanded = remember {
                mutableStateOf(true)
            }

            // CollapsingToolbar Implementation
            val toolbarCollapsed = rememberSaveable {
                mutableStateOf(false)
            }
            val toolbarHeight = 120.dp
            val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
            val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource,
                    ): Offset {
                        val delta = available.y
                        val newOffset = toolbarOffsetHeightPx.value + delta
                        toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
                        // Returning Zero so we just observe the scroll but don't execute it
                        return Offset.Zero
                    }
                }
            }

            LaunchedEffect(key1 = nestedScrollConnection) {

            }

            Scaffold(
                topBar = {
                    DashboardScreenTopBar(
                        tabPage = tabPage.value,
                        profilePicUrl = null,
                        toolbarHeight = toolbarHeight,
                        toolbarOffset = toolbarOffsetHeightPx.value,
                        toolbarCollapsed = toolbarCollapsed.value,
                        onCollapsed = {
                            toolbarCollapsed.value = it
                        },
                        updateTabPage = {
                            tabPage.value = it
                        },
                    )
                },
                floatingActionButton = {
                    AddButton(
                        buttonText = "New Task",
                        onButtonClicked = { buttonExpanded.value = !buttonExpanded.value },
                        expanded = buttonExpanded.value
                    )
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LazyColumn(
                        Modifier
                            .nestedScroll(nestedScrollConnection)
                            .fillMaxWidth(.9f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        state = listState
                    ) {
                        item {
                            ComponentsPreview()
                        }

                        item {
                            Text(
                                text = "Dashboard: $route",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // Statistics
        composable(
            route = DashboardDestinations.Statistics.route
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Dashboard: $route",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DashboardScreenTopBar(
    tabPage: DashboardDestinations,
    profilePicUrl: String?,
    toolbarHeight: Dp,
    toolbarOffset: Float,
    toolbarCollapsed: Boolean,
    onCollapsed: (Boolean) -> Unit,
    updateTabPage: (DashboardDestinations) -> Unit,
) {
    CollapsingToolbarBase(
        modifier = Modifier
            .statusBarsPadding(),
        toolbarHeading = null,
        toolbarHeight = toolbarHeight,
        toolbarOffset = toolbarOffset,
        showBackButton = false,
        minShrinkHeight = 60.dp,
        onCollapsed = {
            onCollapsed(it)
        }
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth(),
            verticalArrangement = Arrangement
                .spacedBy(16.dp)
        ) {
            AnimatedVisibility(
                visible = !toolbarCollapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ReluctSearchBar(
                    extraButton = {
                        ProfilePicture(
                            modifier = Modifier,//.padding(4.dp),
                            pictureUrl = profilePicUrl
                        )
                    }
                )
            }
            DashboardTabBar(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                tabPage = tabPage,
                onTabSelected = {
                    updateTabPage(it)
                }
            )
        }
    }
}