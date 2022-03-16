package work.racka.reluct.common.features.tasks.completed_tasks.container

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import work.racka.reluct.common.model.states.tasks.TasksSideEffect
import work.racka.reluct.common.model.states.tasks.TasksState

interface CompletedTasksContainerHost {
    val uiState: StateFlow<TasksState>
    val sideEffect: Flow<TasksSideEffect>
    fun toggleDone(taskId: Long, isDone: Boolean)
    fun navigateToTaskDetails(taskId: Long)
}