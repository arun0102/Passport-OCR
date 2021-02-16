package templates.domain

import non_core.lib.Result

interface TemplatesRepo {
    fun getTemplatesList(): List<String>

    fun getTemplateMapped(
        parsedData: String,
        selectedTemplate: String
    ): String
}