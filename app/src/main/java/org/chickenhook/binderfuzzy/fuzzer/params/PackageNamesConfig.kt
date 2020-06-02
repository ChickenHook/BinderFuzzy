package org.chickenhook.binderfuzzy.fuzzer.params

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask


class PackageNamesConfig : ParamConfig() {
    override fun getName(): String {
        return "PackageNamesConfig"
    }

    override fun prozessRanges(
        context: Context,
        onFuzzTaskUpdateListener: FuzzTask.OnFuzzTaskUpdateListener
    ): List<ParamConfig> {
        val paramConfigs=ArrayList<ParamConfig>()
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos: List<ResolveInfo> =
            context.packageManager.queryIntentActivities(mainIntent, 0)
        for (info in resolveInfos) {
            val applicationInfo = info.activityInfo.applicationInfo
            paramConfigs.add(StringConstantConfig(applicationInfo.packageName))
        }
        return paramConfigs;
    }

    override fun getValue(): Any? {
        TODO("Not available")
    }

}