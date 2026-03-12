package com.example.recipeapp

import kotlinx.serialization.Serializable
import android.content.Context
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

/*
This file is the Recipe Class for the main use of the entire app.
It includes the Class itself,
but also functions to load the recipes the app comes with,
and also to write new ones to storage.
 */

@Serializable
data class AppRecipe (
    var name: String,
    var image: List<String> = emptyList(),

    var datePublished: String? = null,
    var dateChanged: String? = null,

    var tags: List<String> = emptyList(),

    var prepTime: String? = null,
    var cookTime: String? = null,
    var totalTime: String? = null,
    var recipeYield: String? = null,

    var recipeBooks: List<String> = emptyList(),

    var ingredients: List<String> = emptyList(),
    var recipeInstruct: List<String> = emptyList(),
    var rating: Float = 0f,

    val id: String = UUID.randomUUID().toString(),
) {

     fun getIngredAsText(): String{
        var str = ""
        for(ingr in ingredients){
            str += ingr + "\n"
        }
        return str
     }

     fun getInstructAsText(): String{
        var str = ""
        for(instr in recipeInstruct){
            str += instr + "\n"
        }
        return str
     }
}


//Writing a single recipe to disk, doesnt create copies cause of id - rewrites
fun saveRecipe(context: Context, recipe: AppRecipe) {

    val json = Json { prettyPrint = true }

    val folder = File(context.filesDir, "app_recipes")

    if (!folder.exists()) {
        folder.mkdirs()
    }

    val file = File(folder, "${recipe.id}.json")

    file.writeText(json.encodeToString(AppRecipe.serializer(), recipe))
}

//Loads recipes from disk (not assets)
fun loadSavedRecipes(context: Context): List<AppRecipe> {

    val json = Json { ignoreUnknownKeys = true }

    val folder = File(context.filesDir, "app_recipes")

    if (!folder.exists()) return emptyList()

    return folder.listFiles()?.map { file ->
        json.decodeFromString<AppRecipe>(file.readText())
    } ?: emptyList()
}


//Loads appRecipes from assets - dont really have any
fun loadAppRecipes(context: Context): List<AppRecipe> {
    val recipes = mutableListOf<AppRecipe>()

    val files = context.assets.list("app_recipes") ?: return recipes

    val json = Json { ignoreUnknownKeys = true }

    for (file in files) {
        val text = context.assets
            .open("app_recipes/$file")
            .bufferedReader()
            .use { it.readText() }

        val recipe = json.decodeFromString<AppRecipe>(text)
        recipes.add(recipe)
    }

    return recipes
}