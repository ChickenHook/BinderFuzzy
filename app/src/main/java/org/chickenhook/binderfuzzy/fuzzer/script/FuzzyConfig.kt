package org.chickenhook.binderfuzzy.fuzzer.script


class FuzzyConfig {


    var fields_ordered: List<ConfigField>? = null
    var call: ConfigCall? = null

    class ConfigField {
        var clazz: String? = null
        var field: String? = null
    }

    class ConfigCall {
        var clazz: String? = null
        var method: String? = null
        val params: List<String>? = null
    }

    override fun toString(): String {
        return "Config(fields_ordered=$fields_ordered, call=$call)"
    }


}