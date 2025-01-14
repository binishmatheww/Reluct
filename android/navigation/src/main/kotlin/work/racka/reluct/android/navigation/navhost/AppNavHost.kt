package work.racka.reluct.android.navigation.navhost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import work.racka.reluct.android.navigation.navbar.NavbarDestinations
import work.racka.reluct.android.navigation.navbar.ReluctBottomNavBar
import work.racka.reluct.android.navigation.navhost.graphs.dashboard.DashboardNavHost
import work.racka.reluct.android.navigation.navhost.graphs.extras.otherScreenNavGraph
import work.racka.reluct.android.navigation.navhost.graphs.goals.GoalsNavHost
import work.racka.reluct.android.navigation.navhost.graphs.goals.goalDataNavGraph
import work.racka.reluct.android.navigation.navhost.graphs.screentime.ScreenTimeNavHost
import work.racka.reluct.android.navigation.navhost.graphs.screentime.appScreenTimeStatsNavGraph
import work.racka.reluct.android.navigation.navhost.graphs.tasks.TasksNavHost
import work.racka.reluct.android.navigation.transitions.scaleInEnterTransition
import work.racka.reluct.android.navigation.transitions.scaleInPopEnterTransition
import work.racka.reluct.android.navigation.transitions.scaleOutExitTransition
import work.racka.reluct.android.navigation.transitions.scaleOutPopExitTransition
import work.racka.reluct.android.navigation.util.SettingsCheck
import work.racka.reluct.android.screens.authentication.AuthenticationScreen
import work.racka.reluct.android.screens.onboarding.OnBoardingScreen
import work.racka.reluct.android.screens.settings.SettingsScreen
import work.racka.reluct.common.core.navigation.composeDestinations.onboarding.AuthenticationDestination
import work.racka.reluct.common.core.navigation.composeDestinations.onboarding.OnBoardingDestination
import work.racka.reluct.common.core.navigation.composeDestinations.settings.SettingsDestination
import work.racka.reluct.common.core.navigation.composeDestinations.tasks.PendingTasksDestination
import work.racka.reluct.compose.common.components.animations.slideInVerticallyFadeReversed
import work.racka.reluct.compose.common.components.animations.slideOutVerticallyFadeReversed
import work.racka.reluct.compose.common.components.util.BarsVisibility
import work.racka.reluct.compose.common.components.util.rememberBarVisibility
import work.racka.reluct.compose.common.theme.Dimens

// Route used specifically for checking if the On Boarding flow was shown and User Signed In
private const val CHECKING_ROUTE = "dummy_checking_route"

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(settingsCheck: State<SettingsCheck?>, modifier: Modifier = Modifier) {
    val navController = rememberAnimatedNavController()
    val barsVisibility = rememberBarVisibility(defaultBottomBar = false)

    val mainPadding = PaddingValues(
        bottom = Dimens.ExtraLargePadding.size + Dimens.MediumPadding.size +
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    )
    /**
     * Don't use the bottomBar param of Scaffold for the AppBottomBar
     * It will cause the whole screen to recompose when you hide/show the bottomBar
     * with [BarsVisibility] since the innerPadding changes during this transition
     * The AppBottomBar is provided to Box inside of this Scaffold and the padding needed by inner
     * items is mainPadding from above
     */
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedNavHost(
                modifier = Modifier,
                navController = navController,
                startDestination = CHECKING_ROUTE,
                route = "root"
            ) {
                // Checking Route
                composable(
                    route = CHECKING_ROUTE,
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() },
                    popEnterTransition = { fadeIn() },
                    popExitTransition = { fadeOut() }
                ) {
                    HandleRouteChecks(settingsCheck, navController)

                    Box(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    SideEffect { barsVisibility.bottomBar.hide() }
                }

                // OnBoarding
                composable(
                    route = OnBoardingDestination.route,
                    enterTransition = { scaleInEnterTransition() },
                    exitTransition = { scaleOutExitTransition() },
                    popEnterTransition = { scaleInPopEnterTransition() },
                    popExitTransition = { scaleOutPopExitTransition() }
                ) {
                    OnBoardingScreen(
                        onNavigateToAuth = {
                            navController.navigate(AuthenticationDestination.route) {
                                popUpTo(
                                    OnBoardingDestination.route
                                ) { inclusive = true }
                            }
                        }
                    )

                    SideEffect { barsVisibility.bottomBar.hide() }
                }

                // Authentication
                composable(
                    route = AuthenticationDestination.route,
                    enterTransition = { scaleInEnterTransition() },
                    exitTransition = { scaleOutExitTransition() },
                    popEnterTransition = { scaleInPopEnterTransition() },
                    popExitTransition = { scaleOutPopExitTransition() }
                ) {
                    AuthenticationScreen(
                        onContinue = {
                            navController.navigate(NavbarDestinations.Dashboard.route) {
                                popUpTo(
                                    AuthenticationDestination.route
                                ) { inclusive = true }
                            }
                        }
                    )
                    SideEffect { barsVisibility.bottomBar.hide() }
                }

                // Dashboard
                composable(
                    route = NavbarDestinations.Dashboard.route,
                    enterTransition = { scaleInEnterTransition() },
                    exitTransition = { scaleOutExitTransition() },
                    popEnterTransition = { scaleInPopEnterTransition() },
                    popExitTransition = { scaleOutPopExitTransition() }
                ) {
                    DashboardNavHost(
                        mainNavController = navController,
                        barsVisibility = barsVisibility,
                        mainScaffoldPadding = mainPadding
                    )
                }

                // Tasks
                composable(
                    route = NavbarDestinations.Tasks.route,
                    deepLinks = PendingTasksDestination.deepLinks,
                    enterTransition = { scaleInEnterTransition() },
                    exitTransition = { scaleOutExitTransition() },
                    popEnterTransition = { scaleInPopEnterTransition() },
                    popExitTransition = { scaleOutPopExitTransition() }
                ) {
                    TasksNavHost(
                        mainNavController = navController,
                        mainScaffoldPadding = mainPadding,
                        barsVisibility = barsVisibility
                    )
                }

                // Screen Time
                composable(
                    route = NavbarDestinations.ScreenTime.route,
                    enterTransition = { scaleInEnterTransition() },
                    exitTransition = { scaleOutExitTransition() },
                    popEnterTransition = { scaleInPopEnterTransition() },
                    popExitTransition = { scaleOutPopExitTransition() }
                ) {
                    ScreenTimeNavHost(
                        mainNavController = navController,
                        barsVisibility = barsVisibility,
                        mainScaffoldPadding = mainPadding
                    )
                }

                // Goals
                composable(
                    route = NavbarDestinations.Goals.route,
                    enterTransition = { scaleInEnterTransition() },
                    exitTransition = { scaleOutExitTransition() },
                    popEnterTransition = { scaleInPopEnterTransition() },
                    popExitTransition = { scaleOutPopExitTransition() }
                ) {
                    GoalsNavHost(
                        mainNavController = navController,
                        barsVisibility = barsVisibility,
                        mainScaffoldPadding = mainPadding
                    )
                }

                // Goal Data Graph
                goalDataNavGraph(
                    navController = navController,
                    barsVisibility = barsVisibility
                )

                // Settings
                composable(
                    route = SettingsDestination.route,
                    enterTransition = { scaleInEnterTransition() },
                    exitTransition = { scaleOutExitTransition() },
                    popEnterTransition = { scaleInPopEnterTransition() },
                    popExitTransition = { scaleOutPopExitTransition() }
                ) {
                    barsVisibility.bottomBar.hide()

                    SettingsScreen(goBack = navController::popBackStack)
                }

                // Independent App Screen Time Stats and Limits Graph
                appScreenTimeStatsNavGraph(
                    navController = navController,
                    barsVisibility = barsVisibility
                )

                // All Other screens that don't share Scaffolds
                otherScreenNavGraph(
                    navController = navController,
                    barsVisibility = barsVisibility
                )
            }

            // Bottom Bar
            AppBottomBar(
                navController = navController,
                barsVisibility = barsVisibility
            )
        }
    }
}

@Composable
private fun BoxScope.AppBottomBar(
    navController: NavHostController,
    barsVisibility: BarsVisibility,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier.align(Alignment.BottomCenter),
        visible = barsVisibility.bottomBar.isVisible,
        enter = slideInVerticallyFadeReversed(),
        exit = slideOutVerticallyFadeReversed()
    ) {
        ReluctBottomNavBar(
            navController = navController,
            graphStartDestination = NavbarDestinations.Dashboard.route
        )
    }
}

@Composable
private fun HandleRouteChecks(
    settingsCheck: State<SettingsCheck?>,
    navController: NavHostController
) {
    LaunchedEffect(settingsCheck.value) {
        settingsCheck.value?.let { check ->
            val accountChecked =
                (check.accountCheck != null && check.accountCheck.isEmailVerified) ||
                    check.loginSkipped
            if (check.isOnBoardingDone && accountChecked) {
                // Everything is ok. Go to Dashboard
                navController.navigate(NavbarDestinations.Dashboard.route) {
                    popUpTo(CHECKING_ROUTE) { inclusive = true }
                }
            } else if (!check.isOnBoardingDone) {
                // Show On boarding flow
                navController.navigate(OnBoardingDestination.route) {
                    popUpTo(CHECKING_ROUTE) { inclusive = true }
                }
            } else if (check.accountCheck?.isEmailVerified != true) {
                // Email not verified or user not logged in. Go to Auth screen
                navController.navigate(AuthenticationDestination.route) {
                    popUpTo(CHECKING_ROUTE) { inclusive = true }
                }
            }
        }
    }
}
