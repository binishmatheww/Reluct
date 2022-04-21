package work.racka.reluct.android.compose.components.bottom_sheet.add_edit_task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import work.racka.reluct.android.compose.components.R
import work.racka.reluct.android.compose.components.bottom_sheet.SheetButton
import work.racka.reluct.android.compose.components.cards.settings.EntryWithCheckbox
import work.racka.reluct.android.compose.components.textfields.ReluctTextField
import work.racka.reluct.android.compose.theme.Dimens
import work.racka.reluct.android.compose.theme.Shapes
import work.racka.reluct.common.model.domain.tasks.EditTask
import work.racka.reluct.common.model.util.time.TimeUtils.plus
import java.util.*

// This provided here so that it doesn't leak DateTime dependencies to the
// screens modules.
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddEditTaskFields(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    editTask: EditTask?,
    saveButtonText: String,
    discardButtonText: String,
    onSave: (EditTask) -> Unit,
    onDiscard: () -> Unit = { },
) {
    val setReminder = remember {
        mutableStateOf(false)
    }

    val taskTitleError = remember {
        mutableStateOf(false)
    }

    val sheetTitle = if (editTask == null) stringResource(id = R.string.add_task)
    else stringResource(id = R.string.edit_task)

    val advancedDateTime = remember {
        derivedStateOf {
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .plus(hours = 1)
        }
    }

    val task = remember {
        val taskToEdit = editTask
            ?: EditTask(
                id = UUID.randomUUID().toString(),
                title = "",
                description = null,
                done = false,
                overdue = false,
                dueDateLocalDateTime = advancedDateTime.value.toString(),
                timeZoneId = TimeZone.currentSystemDefault().id,
                completedLocalDateTime = null,
                reminderLocalDateTime = null
            )
        mutableStateOf(taskToEdit)
    }

    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement
            .spacedBy(Dimens.MediumPadding.size),
        modifier = modifier
            .animateContentSize()
            .fillMaxWidth()
    ) {
        item {
            ReluctTextField(
                hint = stringResource(R.string.task_title_hint),
                isError = taskTitleError.value,
                errorText = stringResource(R.string.task_title_error_text),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                onTextChange = { text ->
                    task.value = task.value.copy(title = text)
                }
            )
        }

        item {
            ReluctTextField(
                modifier = Modifier
                    .height(200.dp),
                hint = stringResource(R.string.task_description_hint),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                onTextChange = { text ->
                    task.value = task.value.copy(title = text)
                }
            )
        }

        item {
            Text(
                modifier = Modifier
                    .padding(horizontal = Dimens.MediumPadding.size)
                    .fillMaxWidth(),
                text = stringResource(R.string.task_to_be_done_at_text),
                style = MaterialTheme.typography.titleMedium,
                color = LocalContentColor.current,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Dimens.SmallPadding.size))
            DateTimePills(
                onLocalDateTimeChange = { dateTimeString ->
                    task.value = task.value.copy(dueDateLocalDateTime = dateTimeString)
                }
            )
        }

        item {
            EntryWithCheckbox(
                title = stringResource(R.string.set_reminder),
                description = stringResource(R.string.set_reminder_desc),
                onCheckedChanged = { checked ->
                    setReminder.value = checked
                    task.value = task.value.copy(
                        reminderLocalDateTime =
                        if (setReminder.value) advancedDateTime.value.toString()
                        else null
                    )
                }
            )
        }

        if (setReminder.value) {
            item {
                AnimatedVisibility(
                    visible = setReminder.value,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = Dimens.MediumPadding.size)
                                .fillMaxWidth(),
                            text = stringResource(R.string.reminder_at),
                            style = MaterialTheme.typography.titleMedium,
                            color = LocalContentColor.current,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(Dimens.SmallPadding.size))
                        DateTimePills(
                            onLocalDateTimeChange = { dateTimeString ->
                                task.value = task.value.copy(reminderLocalDateTime = dateTimeString)
                            }
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SheetButton(
                    buttonText = discardButtonText,
                    icon = Icons.Rounded.DateRange,
                    onButtonClicked = onDiscard,
                    shape = Shapes.large,
                    buttonColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(Dimens.MediumPadding.size))
                SheetButton(
                    buttonText = saveButtonText,
                    icon = Icons.Rounded.DateRange,
                    shape = Shapes.large,
                    buttonColor = MaterialTheme.colorScheme.primary,
                    onButtonClicked = {
                        val isTitleBlank = task.value.title.isBlank()
                        if (isTitleBlank) taskTitleError.value = true
                        else onSave(task.value)
                    }
                )
            }
        }
    }
}
