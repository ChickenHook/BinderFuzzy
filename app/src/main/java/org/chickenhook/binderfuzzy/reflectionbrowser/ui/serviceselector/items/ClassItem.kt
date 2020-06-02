package org.chickenhook.binderfuzzy.reflectionbrowser.ui.serviceselector.items

/**
 * A dummy item representing a piece of content.
 */
data class ClassItem(val id: String, val content: String, val details: String, val obj: Any) {
    override fun toString(): String = content
}