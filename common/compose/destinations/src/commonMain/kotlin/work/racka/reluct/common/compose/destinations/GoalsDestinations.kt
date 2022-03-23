package work.racka.reluct.common.compose.destinations

enum class GoalsDestinations(
    val route: String,
    val label: String
) {
    Ongoing(
        route = "GoalsOngoing",
        label = "Ongoing"
    ),
    Completed(
        route = "GoalsCompleted",
        label = "Completed"
    );

    enum class Paths(
        val route: String
    )
}