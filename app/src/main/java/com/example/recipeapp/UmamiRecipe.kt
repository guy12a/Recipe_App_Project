package com.example.recipeapp

import android.content.Context
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

@Serializable
data class UmamiRecipe(
    @SerialName("@context")
    var context: String? = null,

    @SerialName("@type")
    var type: String? = null,

    var name: String,
    var url: String? = null,
    var image: List<String> = emptyList(),

    var author: Author? = null,

    var datePublished: String? = null,
    var description: String? = null,

    var prepTime: String? = null,
    var cookTime: String? = null,
    var totalTime: String? = null,

    var keywords: String? = null,
    var recipeYield: String? = null,

    var recipeCategory: String? = null,
    var recipeCuisine: String? = null,

    var nutrition: Nutrition? = null,

    var recipeIngredient: List<String> = emptyList(),
    var recipeInstructions: List<Instruction> = emptyList(),
    //===========================
    //actual use, might change
    var rating: Float = 0f,
    var tags: List<String> = emptyList(),
    var ingredients: String = "",
    var recipeInstruct: String = "",
    @Transient var img: Painter? = null
)

@Serializable
data class Author(
    @SerialName("@type")
    val type: String? = null,
    val name: String? = null
)

@Serializable
data class Nutrition(
    @SerialName("@context")
    val context: String? = null,

    @SerialName("@type")
    val type: String? = null
)

@Serializable
data class Instruction(
    @SerialName("@type")
    val type: String? = null,
    val text: String,
    val url: String? = null
)


fun getTextFromJson(context: Context, fileName: String): String {
    return context.assets.open(fileName)
        .bufferedReader()
        .use { it.readText() }
}

fun parseUmamiRecipe (context: Context, recipeName: String): UmamiRecipe{
    val jsonString = getTextFromJson(context, recipeName)

    val json = Json {
        ignoreUnknownKeys = true
    }

    val umamiRecipe = json.decodeFromString<UmamiRecipe>(jsonString)
    return umamiRecipe
}

fun getUmamiRecipes (context: Context): List<UmamiRecipe>{
    val umamiRecipes = mutableListOf<UmamiRecipe>()

    val files = context.assets.list("my_recipes_folder") ?: return umamiRecipes

    for (fileName in files) {
        val recipe = parseUmamiRecipe(context, "my_recipes_folder/$fileName")
        umamiRecipes.add(recipe)
    }

    return umamiRecipes
}


