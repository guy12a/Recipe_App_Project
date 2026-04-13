package com.example.recipeapp

import android.content.Context
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

/*
This file is the Umami recipes, exported as json, from the site.
It includes the Class itself,
and functions to process umami json recipes into standard AppRecipe class
 */

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

//===================================================================
//Loading Umami Jsons, transforming them into my Recipe Class
fun getUmamiAsApp (context: Context): List<AppRecipe>{
    val recipes = mutableListOf<AppRecipe>()

    val files = context.assets.list("umami_recipes") ?: return recipes

    val json = Json { ignoreUnknownKeys = true }

    for (file in files) {
        val jsonString = context.assets.
            open("umami_recipes/$file")
            .bufferedReader()
            .use { it.readText() }

        val umamiRecipe = json.decodeFromString<UmamiRecipe>(jsonString)
        recipes.add(umamiToApp(umamiRecipe))
    }

    return recipes
}

//getting unprocessed umami recipes - NOT as app recipe
fun getUmamiRecipes (context: Context): List<UmamiRecipe>{
    val recipes = mutableListOf<UmamiRecipe>()

    val files = context.assets.list("umami_recipes") ?: return recipes

    val json = Json { ignoreUnknownKeys = true }

    for (file in files) {
        val jsonString = context.assets.
        open("umami_recipes/$file")
            .bufferedReader()
            .use { it.readText() }

        val umamiRecipe = json.decodeFromString<UmamiRecipe>(jsonString)
        recipes.add(umamiRecipe)
    }

    return recipes
}

//===================================================================
//Transforming Umami recipes to AppRecipe
fun umamiToApp (umamiRecipe: UmamiRecipe): AppRecipe{
    val tags = formatTags(umamiRecipe.description)
    val recipe = AppRecipe(
        umamiRecipe.name,
        umamiRecipe.image,
        umamiRecipe.datePublished,
        umamiRecipe.datePublished,
        tags,
        umamiRecipe.prepTime,
        umamiRecipe.cookTime,
        umamiRecipe.totalTime,
        umamiRecipe.recipeYield,
        formatRecipeBooks(tags), //change this vvv
        umamiRecipe.recipeIngredient,
        formatInstructions(umamiRecipe.recipeInstructions),
        0f
        )
    return recipe
}

private fun formatTags(description: String?) : List<String>{
    if (description == null) {
        return emptyList()
    }
    val lower = description.lowercase()
    val tags = lower.split(", ")
    return tags
}

private fun formatRecipeBooks (tags: List<String>): List<String>{
    val books = mutableListOf<String>()
    for(tag in tags){
        if((tag == "dessert" || tag == "cookies" || tag == "tart") && !books.contains("Sweets & Desserts"))
            books.add("Sweets & Desserts")
        if((tag == "pizza" || tag == "bread") && !books.contains("Breads & Baked"))
            books.add("Breads & Baked")
        if((tag == "basic ingredients") && !books.contains("Ingredients & Basics"))
            books.add("Ingredients & Basics")
        if((tag == "pasta" || tag == "soup") && !books.contains("Cooking & Soups"))
            books.add("Cooking & Soups")
        if((tag == "meat" || tag == "chicken" || tag == "beef") && !books.contains("Meat & Lunch"))
            books.add("Meat & Lunch")
        if((tag == "appetizer" || tag == "salad") && !books.contains("Appetizers & Sides"))
            books.add("Appetizers & Sides")
    }
    return books
}

private fun formatInstructions(instructions: List<Instruction>): List<String>{
    var lst = mutableListOf<String>()
    for(instr in instructions){
        lst.add(instr.text)
    }
    return lst
}




