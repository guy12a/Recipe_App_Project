package com.example.recipeapp

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

class SearchUtils {
    //map recipeId -> Recipe
    var recipes : HashMap<String, AppRecipe> = HashMap()
    //map bookName -> List of recipeIds
    var cookBooks : HashMap<String, MutableList<String>> = HashMap()
    //map tag -> list of recipeIds
    var tagsToRecipes : HashMap<String, MutableList<String>> = HashMap()


    //returns map of recipeName and AppRecipe, based on book
    fun getBookRecipesAsMap(bookName: String) : HashMap<String,AppRecipe>{
        val recipesInBook = HashMap<String,AppRecipe>()
        if(bookName == allRecipesName){
            for(entry in recipes.entries){
                recipesInBook.put(entry.value.name,entry.value)
            }
        }
        else if(cookBooks.containsKey(bookName)){
            for(recId in cookBooks.getValue(bookName)) {
                val rec = recipes.getValue(recId)
                recipesInBook.put(rec.name,rec)
            }
        }
        return recipesInBook
    }

    //returns a map of cookBook name, and first recipe
    fun getCookBooksList() : HashMap<String,AppRecipe> {
        val map = HashMap<String,AppRecipe>()
        var flag = false
        for(book in cookBooks.keys){
            if(cookBooks.getValue(book).isEmpty())
                map.put(book,exampleRec())
            else{
                map.put(book,recipes.getValue(cookBooks.getValue(book).first()))
                if(!flag){
                    flag = true
                    map.put(allRecipesName,recipes.getValue(cookBooks.getValue(book).first()))
                }
            }
        }
        if(!flag) map.put(allRecipesName,recipes.entries.toList().first().value)
        return map
    }

    //returns a map of recipeName and AppRecipe, based on tag and cookbook
    fun getRecipesByTag(tag: String, bookName: String) :HashMap<String,AppRecipe>{
        val recipesWithTag = HashMap<String,AppRecipe>()
        if(tagsToRecipes.containsKey(tag)){
            for(recId in tagsToRecipes.getValue(tag)) {
                val rec = recipes.getValue(recId)
                if(bookName == toSortName || bookName == allRecipesName || rec.recipeBooks.contains(bookName))
                    recipesWithTag.put(rec.name,rec)
            }
        }
        return recipesWithTag
    }

    //returns all recipes of a cookbook
    fun getBookRecipesAsList(bookName: String) : MutableList<AppRecipe> {
        val recipesInBook = mutableListOf<AppRecipe>()
        if(cookBooks.containsKey(bookName)){
            for(recId in cookBooks.getValue(bookName))
                recipesInBook.add(recipes.getValue(recId))
        }
        return recipesInBook
    }

    fun getRecipe(recipeId: String): AppRecipe{
        val rec = recipes.get(recipeId)
        if(rec == null)
            return exampleRec()
        return rec
    }

    //loads already formatted appRecipe jsons from memory, ID based
    fun loadRecipes(context: Context){
        val recps = loadSavedRecipes(context)
        for (recipe in recps){
            recipes.put(recipe.id,recipe)
            if(recipe.recipeBooks.isEmpty()) cookBooks.getOrPut(toSortName){mutableListOf()}.add(recipe.id)
            for(book in recipe.recipeBooks){
                cookBooks.getOrPut(book){mutableListOf()}.add(recipe.id)
            }
            for(tag in recipe.tags){
                tagsToRecipes.getOrPut(tag){mutableListOf()}.add(recipe.id)
            }
        }
    }

    companion object {
        val allRecipesName = "All Recipes"
        val homeName = "Home"
        val toSortName = "Unsorted Recipes"
        fun exampleRec(): AppRecipe{
            val lst = mutableListOf<String>()
            lst.add("https://www.umami.recipes/api/image/recipes/02WXpK1Tqiz7nDxmwwjY/images/5p3FPEt7vJ8mEK3FpSBQZc?w=2048&q=75")
            val tags = mutableListOf<String>()
            tags.add("chocolate")
            tags.add("chocolate")
            tags.add("chocolate")
            tags.add("chocolate")
            return AppRecipe("30 min Choclate Chip Cookies",
                lst,
                "2023-10-06T14:21:57.559Z",
                "2023-10-06T14:21:57.559Z",tags,
                recipeBooks = mutableListOf("Sweets & Desserts"))
        }
    }
}

class StyleUtils{
    companion object {
        val bigTitle = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
        val smallTitle = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold)
        val backButtonTitle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
        val regularText = TextStyle(fontSize = 16.sp)

        val cardText = TextStyle(color = Color.Black, fontSize = 15.sp, textAlign = TextAlign.Center)
    }
}

