package templates.domain

data class ConfigData(
    var name: String,
    var keysArr: Map<String, List<String>>,
    var patternArr: Map<String, String>
)

data class TemplatesDataMap(var configData: ConfigData, var keyValMap: Map<String, Any>)