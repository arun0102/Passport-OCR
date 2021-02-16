package template.data

import templates.domain.GetTemplateDataUseCase
import templates.domain.TemplatesRepo
import javax.inject.Inject

class GetTemplateDataUseCaseImpl @Inject constructor(
    private val templatesRepo: TemplatesRepo
) : GetTemplateDataUseCase {

    override fun getTemplateMap(
        parsedData: String,
        selectedTemplate: String
    ): String {
        return templatesRepo.getTemplateMapped(parsedData, selectedTemplate)
    }
}