package work.racka.reluct.common.data.usecases.app_info

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import work.racka.reluct.common.data.util.hasMainActivity
import work.racka.reluct.common.data.util.hasSystemFlag
import work.racka.reluct.common.model.domain.app_info.AppInfo

internal class AndroidGetInstalledApps(
    private val context: Context, private val getAppInfo: GetAppInfo
) : GetInstalledApps {
    private val packageManager = context.packageManager

    @SuppressLint("QueryPermissionsNeeded")
    override suspend fun invoke(): List<AppInfo> {
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA).filter {
            hasMainActivity(context = context, packageName = it.packageName) ||
                    !hasSystemFlag(context = context, appInfo = it, packageName = it.packageName)
        }.map {
            AppInfo(
                packageName = it.packageName,
                appName = getAppInfo.getAppName(it.packageName),
                appIcon = getAppInfo.getAppIcon(it.packageName)
            )
        }.sortedBy { it.appName }
    }
}