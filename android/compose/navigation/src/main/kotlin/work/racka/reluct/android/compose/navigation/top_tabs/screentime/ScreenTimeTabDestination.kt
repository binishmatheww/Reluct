package work.racka.reluct.android.compose.navigation.top_tabs.screentime

import work.racka.reluct.common.core_navigation.compose_destinations.screentime.ScreenTimeLimitsDestination
import work.racka.reluct.common.core_navigation.compose_destinations.screentime.ScreenTimeStatsDestination

enum class ScreenTimeTabDestination(
    val route: String
) {
    Statistics(
        route = ScreenTimeStatsDestination.route
    ),
    Limits(
        route = ScreenTimeLimitsDestination.route
    );
}