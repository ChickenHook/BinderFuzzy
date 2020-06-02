package org.chickenhook.binderfuzzy.reflectionbrowser.ui.classobjectfragment.items

import java.lang.reflect.Member

/**
 * A dummy item representing a piece of content.
 */
data class ClassMemberItem(val id: String, val content: String, val details: String, val member: Member, val host:Any) {
    override fun toString(): String = content
}