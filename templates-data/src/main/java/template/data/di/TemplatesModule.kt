package template.data.di

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import dagger.Binds
import dagger.Module
import dagger.Provides
import org.json.JSONArray
import org.json.JSONObject
import template.data.GetTemplateDataUseCaseImpl
import template.data.GetTemplatesUseCaseImpl
import template.data.TemplatesRepoImpl
import templates.domain.*


@Module(includes = [TemplatesRepoModule::class, TemplateListModule::class])
interface TemplatesModule

@Module
interface TemplatesRepoModule {
    @Binds
    fun bindTemplatesUseCase(templatesUseCaseImpl: GetTemplatesUseCaseImpl): GetTemplatesUseCase

    @Binds
    fun bindTemplateDataUseCase(templateDataUseCaseImpl: GetTemplateDataUseCaseImpl): GetTemplateDataUseCase

    @Binds
    fun bindTemplatesRepo(templatesRepoImpl: TemplatesRepoImpl): TemplatesRepo
}

@Module
class TemplateListModule {
    @Provides
    fun providesTemplatesList(context: Context): List<String> {
        return getTemplateNamesFromConfig(context)
    }

    private fun getTemplateNamesFromConfig(context: Context): List<String> {
        val assetManager: AssetManager = context.assets
        val configStr = assetManager.readAssetsFile("config.json")
        val configJson = JSONObject(configStr)
        val templateNamesList = mutableListOf<String>()
        // templateNamesList.add(TEMPLATE_DEFAULT)
        val templateKeysItr = configJson.keys()
        while (templateKeysItr.hasNext()) {
            templateNamesList.add(templateKeysItr.next())
        }
        return templateNamesList
    }

    @Provides
    fun providesConfigData(context: Context): List<ConfigData> {
        return loadConfigFromAsset(context)
    }

    private fun loadConfigFromAsset(context: Context): List<ConfigData> {
        val assetManager: AssetManager = context.assets
        val configJson = assetManager.readAssetsFile("config.json")
        val configData = JSONObject(configJson).toConfigData()
        Log.d("TemplatesModule", "configData : $configData")
        return configData
    }

    private fun JSONObject.toConfigData(): List<ConfigData> {
        val configList = mutableListOf<ConfigData>()
        val templateKeysItr = keys()
        while (templateKeysItr.hasNext()) {
            var keysMap = mutableMapOf<String, List<String>>()
            var patternsMap = mutableMapOf<String, String>()
            val templateKey = templateKeysItr.next()
            var name = templateKey
            val templateObj = this[templateKey] as JSONObject
            val keysItr = templateObj.keys()
            while (keysItr.hasNext()) {
                val key = keysItr.next()
                if (key == "keys") {
                    val keyObj = templateObj[key] as JSONObject
                    val itr = keyObj.keys()
                    while (itr.hasNext()) {
                        val key = itr.next()
                        var value = keyObj[key] as JSONArray
                        keysMap[key] = value.toList()
                    }

                } else if (key == "pattern") {
                    val keyObj = templateObj[key] as JSONObject
                    val itr = keyObj.keys()
                    while (itr.hasNext()) {
                        val key = itr.next()
                        var value = keyObj[key] as String
                        patternsMap[key] = value
                    }
                }
            }
            configList.add(ConfigData(name = name, keysArr = keysMap, patternArr = patternsMap))
        }
        return configList
    }

    private fun JSONArray.toList() = List(length()) {
        getString(it)
    }

    // TODO Old logic, Needs to be removed later
//    @Throws(JSONException::class)
//    fun JSONObject.jsonToMap(): Map<String, Any>? {
//        var retMap: Map<String, Any> =
//            HashMap()
//        if (this !== JSONObject.NULL) {
//            retMap = toMap(this)
//        }
//        return retMap
//    }
//
//    @Throws(JSONException::class)
//    fun toMap(json: JSONObject): Map<String, Any> {
//        val map: MutableMap<String, Any> =
//            HashMap()
//        val keysItr = json.keys()
//        while (keysItr.hasNext()) {
//            val key = keysItr.next()
//            var value = json[key]
//            if (value is JSONArray) {
//                value = toList(value)
//            } else if (value is JSONObject) {
//                value = toMap(value)
//            }
//            map[key] = value
//        }
//        return map
//    }
//
//    @Throws(JSONException::class)
//    fun toList(array: JSONArray): List<Any> {
//        val list: MutableList<Any> = ArrayList()
//        for (i in 0 until array.length()) {
//            var value = array[i]
//            if (value is JSONArray) {
//                value = toList(value)
//            } else if (value is JSONObject) {
//                value = toMap(value)
//            }
//            list.add(value)
//        }
//        return list
//    }

//    private fun loadJSONFromAsset(context: Context): MutableList<Template> {
//        val templatesArr = mutableListOf<Template>()
//        val assetManager: AssetManager = context.assets
//        val files =
//            assetManager.list("") ?: emptyArray()
//        files.filter { name -> name.startsWith("template", true) }
//            .forEach {
//                val templateJson = assetManager.readAssetsFile(it)
//                val temp = Gson().fromJson(templateJson, Template::class.java)
//                templatesArr.add(temp)
//            }
//        return templatesArr
//    }

    private fun AssetManager.readAssetsFile(fileName: String): String =
        open(fileName).bufferedReader().use { it.readText() }
}