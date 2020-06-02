package org.chickenhook.binderfuzzy.reflectionbrowser.impl

import junit.framework.Assert.assertEquals
import org.junit.Test

class BrowserImplTest {
    @Test
    fun getValuesOfTypeApplicationThread() {
        val activityThreadCls = Class.forName("android.app.ActivityThread")
        val sCurrentActivityThreadField =
            activityThreadCls.getDeclaredField("sCurrentActivityThread")
        sCurrentActivityThreadField.isAccessible = true
        val sCurrentActivityThread = sCurrentActivityThreadField.get(null)
        assertEquals(
            1,
            BrowserImpl.getValuesOfType(
                sCurrentActivityThread,
                typeToSearchFor = Class.forName("android.app.IApplicationThread")
            ).size
        )
    }
}