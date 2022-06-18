package work.racka.reluct.common.features.screen_time.statistics

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import work.racka.reluct.common.data.usecases.app_usage.GetDailyUsageStats
import work.racka.reluct.common.data.usecases.app_usage.GetWeeklyUsageStats
import work.racka.reluct.common.data.usecases.time.GetWeekRangeFromOffset
import work.racka.reluct.common.features.screen_time.states.DailyUsageStatsState
import work.racka.reluct.common.features.screen_time.states.ScreenTimeStatsEvents
import work.racka.reluct.common.features.screen_time.states.ScreenTimeStatsState
import work.racka.reluct.common.features.screen_time.states.WeeklyUsageStatsState
import work.racka.reluct.common.model.util.time.TimeUtils
import work.racka.reluct.common.model.util.time.WeekUtils

internal class ScreenTimeStatsImpl(
    private val getWeeklyUsageStats: GetWeeklyUsageStats,
    private val getDailyUsageStats: GetDailyUsageStats,
    private val getWeekRangeFromOffset: GetWeekRangeFromOffset,
    private val scope: CoroutineScope
) : ScreenTimeStats {

    private val weekOffset: MutableStateFlow<Int> = MutableStateFlow(0)
    private val selectedWeekText: MutableStateFlow<String> = MutableStateFlow("...")
    private val selectedDay: MutableStateFlow<Int> =
        MutableStateFlow(WeekUtils.currentDayOfWeek().isoDayNumber)
    private val weeklyUsageStatsState: MutableStateFlow<WeeklyUsageStatsState> =
        MutableStateFlow(WeeklyUsageStatsState.Empty)
    private val dailyUsageStatsState: MutableStateFlow<DailyUsageStatsState> =
        MutableStateFlow(DailyUsageStatsState.Empty)

    override val uiState: StateFlow<ScreenTimeStatsState> = combine(
        weekOffset, selectedWeekText, selectedDay, weeklyUsageStatsState, dailyUsageStatsState
    ) { weekOffset, selectedWeekText, selectedDay, weeklyUsageStatsState, dailyUsageStatsState ->
        ScreenTimeStatsState(
            weekOffset = weekOffset,
            selectedWeekText = selectedWeekText,
            selectedDay = selectedDay,
            weeklyData = weeklyUsageStatsState,
            dailyData = dailyUsageStatsState
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScreenTimeStatsState()
    )

    private val _events = Channel<ScreenTimeStatsEvents>(capacity = Channel.UNLIMITED)
    override val events: Flow<ScreenTimeStatsEvents>
        get() = _events.receiveAsFlow()

    private lateinit var dailyScreenTimeStatsJob: Job
    private lateinit var weeklyScreenTimeStatsJob: Job

    init {
        getData()
    }

    private fun getDailyData() {
        dailyUsageStatsState.update { DailyUsageStatsState.Loading() }
        dailyScreenTimeStatsJob = scope.launch {
            val dailyData = getDailyUsageStats(
                weekOffset = weekOffset.value,
                dayIsoNumber = selectedDay.value
            )
            if (dailyData.appsUsageList.isEmpty()) {
                dailyUsageStatsState.update { DailyUsageStatsState.Empty }
            } else {
                dailyUsageStatsState.update {
                    DailyUsageStatsState.Data(
                        dailyUsageStats = dailyData
                    )
                }
            }
        }
    }

    private fun getWeeklyData() {
        weeklyUsageStatsState.update { WeeklyUsageStatsState.Loading() }
        weeklyScreenTimeStatsJob = scope.launch {
            selectedWeekText.update { getWeekRangeFromOffset(weekOffset.value) }
            val weeklyData = getWeeklyUsageStats(weekOffset = weekOffset.value)
            if (weeklyData.isEmpty()) {
                weeklyUsageStatsState.update { WeeklyUsageStatsState.Empty }
            } else {
                val totalWeeklyTimeInMillis = weeklyData.values.sumOf { it.totalScreenTime }
                val formattedTime = TimeUtils
                    .getFormattedTimeDurationString(totalWeeklyTimeInMillis)
                weeklyUsageStatsState.update {
                    WeeklyUsageStatsState.Data(
                        weeklyUsageStats = weeklyData,
                        weeklyFormattedTotalTime = formattedTime
                    )
                }
            }
        }
    }

    override fun getData() {
        getWeeklyData()
        getDailyData()
    }

    override fun selectDay(selectedDayIsoNumber: Int) {
        dailyUsageStatsState.update { DailyUsageStatsState.Loading(it.usageStat) }
        selectedDay.update { selectedDayIsoNumber }
        dailyScreenTimeStatsJob.cancel()
        getDailyData()
    }

    override fun updateWeekOffset(weekOffsetValue: Int) {
        weeklyUsageStatsState.update {
            WeeklyUsageStatsState.Loading(weeklyUsageStats = it.usageStats)
        }
        weekOffset.update { weekOffsetValue }
        dailyScreenTimeStatsJob.cancel()
        weeklyScreenTimeStatsJob.cancel()
        getData()
    }

    override fun navigateToAppInfo(packageName: String) {
        _events.trySend(
            ScreenTimeStatsEvents.Navigation.NavigateToAppInfo(packageName)
        )
    }

    override fun openAppTimerSettings(packageName: String) {
        _events.trySend(
            ScreenTimeStatsEvents.Navigation.OpenAppTimerSettings(packageName)
        )
    }
}