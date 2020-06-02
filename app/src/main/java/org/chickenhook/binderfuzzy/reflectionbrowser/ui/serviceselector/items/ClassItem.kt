package org.chickenhook.binderfuzzy.reflectionbrowser.ui.serviceselector.items

/**
 * An item representing a service class
 */
data class ClassItem(val id: String, val content: String, val details: String, val obj: Any) {
    override fun toString(): String = content
}