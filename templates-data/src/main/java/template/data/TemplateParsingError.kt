package template.data

import noncore.error.FeatureError

class TemplateParsingError(
    message: String = "Template parsing error",
    throwable: Throwable? = null
) : FeatureError(message, throwable)