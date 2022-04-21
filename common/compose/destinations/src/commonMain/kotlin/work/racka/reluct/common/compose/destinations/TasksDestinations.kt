package work.racka.reluct.common.compose.destinations

enum class TasksDestinations(
    val route: String,
    val label: String
) {
    Tasks(
        route = "TasksTasks",
        label = "Tasks"
    ),
    Done(
        route = "TasksDone",
        label = "Done"
    ),
    Statistics(
        route = "TasksStatistics",
        label = "Statistics"
    );

    enum class Paths(
        val route: String,
    ) {
        AddEditTask(
            route = "TasksDestinations-AddEditTask"
        ),
        TaskDetails(
            route = "TasksDestinations-TaskDetails"
        );
    }
}