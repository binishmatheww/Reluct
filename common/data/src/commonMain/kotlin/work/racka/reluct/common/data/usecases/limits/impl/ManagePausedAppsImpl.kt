package work.racka.reluct.common.data.usecases.limits.impl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import work.racka.reluct.common.data.usecases.app_info.GetInstalledApps
import work.racka.reluct.common.data.usecases.limits.GetPausedApps
import work.racka.reluct.common.data.usecases.limits.ManagePausedApps
import work.racka.reluct.common.data.usecases.limits.ModifyAppLimits
import work.racka.reluct.common.model.domain.app_info.AppInfo
import work.racka.reluct.common.system_service.haptics.HapticFeedback

internal class ManagePausedAppsImpl(
    private val getPausedApps: GetPausedApps,
    private val getInstalledApps: GetInstalledApps,
    private val modifyAppLimits: ModifyAppLimits,
    private val haptics: HapticFeedback,
    private val backgroundDispatcher: CoroutineDispatcher
) : ManagePausedApps {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun invoke(): Flow<Pair<List<AppInfo>, List<AppInfo>>> {
        val installedApps = getInstalledApps.invoke()
        return getPausedApps.invoke().mapLatest { pausedApps ->
            val pausedAppsInfo = pausedApps.map { it.appInfo }
            val nonPausedApps = installedApps.filterNot { installed ->
                pausedAppsInfo.any { it.packageName == installed.packageName }
            }
            Pair(first = pausedAppsInfo, second = nonPausedApps)
        }.flowOn(backgroundDispatcher)
    }

    override suspend fun pauseApp(packageName: String) {
        modifyAppLimits.pauseApp(packageName = packageName, isPaused = true)
        haptics.tick()
    }

    override suspend fun unPauseApp(packageName: String) {
        modifyAppLimits.pauseApp(packageName = packageName, isPaused = false)
        haptics.tick()
    }

    override suspend fun isPaused(packageName: String): Boolean =
        getPausedApps.isPaused(packageName)
}