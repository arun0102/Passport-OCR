package templates.domain

import non_core.lib.Result

interface GetTemplatesUseCase {
    fun getTemplatesList(): List<String>
}

interface GetTemplateDataUseCase {
    fun getTemplateMap(
        parsedData: String,
        selectedTemplate: String
    ): String
}
