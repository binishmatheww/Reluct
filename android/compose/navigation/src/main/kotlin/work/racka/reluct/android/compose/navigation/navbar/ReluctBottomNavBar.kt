package work.racka.reluct.android.compose.navigation.navbar

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import work.racka.reluct.android.compose.theme.Typography

@Composable
fun ReluctBottomNavBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {

    val items = NavbarDestinations.values()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = NavBarElevation.Level2
    ) {
        NavigationBar(
            modifier = modifier
                .navigationBarsPadding(),
            tonalElevation = NavBarElevation.Level0
        ) {
            items.forEach { item ->
                val selected = currentDestination?.hierarchy?.any {
                    it.route == item.route
                } ?: false

                NavigationBarItem(
                    label = {
                        Text(text = item.label, style = Typography.labelMedium)
                    },
                    selected = selected,
                    //colors = ,
                    icon = {
                        Icon(
                            imageVector = if (selected) item.iconActive else item.iconInactive,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
internal fun ReluctBottomNavBarPrev() {
    MaterialTheme {
        ReluctBottomNavBar(navController = rememberNavController())
    }
}