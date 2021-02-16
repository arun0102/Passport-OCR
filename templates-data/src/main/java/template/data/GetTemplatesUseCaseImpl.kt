package template.data

import templates.domain.GetTemplatesUseCase
import templates.domain.TemplatesRepo
import javax.inject.Inject

class GetTemplatesUseCaseImpl @Inject constructor(
    private val templatesRepo: TemplatesRepo
) : GetTemplatesUseCase {
    override fun getTemplatesList(): List<String> {
        return templatesRepo.getTemplatesList()
    }
}