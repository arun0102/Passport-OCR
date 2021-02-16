package template.data

import android.util.Log
import templates.domain.TemplatesRepo
import javax.inject.Inject

class TemplatesRepoImpl @Inject constructor(
    private val templateList: List<String>
) : TemplatesRepo {
    override fun getTemplatesList(): List<String> {
        return templateList
    }

    override fun getTemplateMapped(
        parsedData: String,
        selectedTemplate: String
    ): String {
        return mapParserFromTemplate(selectedTemplate, parsedData)
    }

    private fun mapParserFromTemplate(selectedTemplate: String?, parsedData: String): String {
        when (selectedTemplate) {
            "Passport" -> return parsedData.parsePassportData()
            "Aadhar Card" -> return parsedData.parseAadharCardData()
            "Bank Statement" -> return parsedData.parseBankStatementData()
        }
        return ""
    }
}