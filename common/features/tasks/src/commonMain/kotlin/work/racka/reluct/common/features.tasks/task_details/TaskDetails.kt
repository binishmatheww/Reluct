package work.racka.reluct.common.features.tasks.task_details

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import work.racka.reluct.common.model.domain.tasks.Task
import work.racka.reluct.common.model.states.tasks.TaskDetailsState
import work.racka.reluct.common.model.states.tasks.TasksEvents

interface TaskDetails {
    val uiState: StateFlow<TaskDetailsState>
    val events: Flow<TasksEvents>
    fun deleteTask(taskId: String)
    fun toggleDone(task: Task, isDone: Boolean)
    fun editTask(taskId: String)
    fun goBack()
}