package work.racka.reluct.common.data.usecases.app_usage

import work.racka.reluct.common.model.domain.usagestats.AppUsageStats

interface GetDailyAppUsageInfo {
    suspend operator fun invoke(
        weekOffset: Int = 0,
        dayIsoNumber: Int,
        packageName: String
    ): AppUsageStats
}