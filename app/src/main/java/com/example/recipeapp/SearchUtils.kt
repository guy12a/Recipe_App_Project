package com.example.recipeapp

import android.content.Context

class SearchUtils {
    //map recipeId -> Recipe
    var recipes : HashMap<String, AppRecipe> = HashMap()
    //map bookName -> List of recipeIds
    var cookBooks : HashMap<String, MutableList<String>> = HashMap()

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
            if(recipe.recipeBooks.isEmpty()) cookBooks.getOrPut("noBook"){mutableListOf()}.add(recipe.id)
            for(book in recipe.recipeBooks){
                cookBooks.getOrPut(book){mutableListOf()}.add(recipe.id)
            }
        }
    }

    //returns list of recipeName and AppRecipe
    fun getBookRecipesAsMap(bookName: String) : HashMap<String,AppRecipe>{
        val recipesInBook = HashMap<String,AppRecipe>()
        if(cookBooks.containsKey(bookName)){
            for(recId in cookBooks.getValue(bookName)) {
                val rec = recipes.getValue(recId)
                recipesInBook.put(rec.name,rec)
            }
        }
        return recipesInBook
    }

    //returns all recipes of a cookbook
    fun getBookRecipes(bookName: String) : MutableList<AppRecipe> {
        val recipesInBook = mutableListOf<AppRecipe>()
        if(cookBooks.containsKey(bookName)){
            for(recId in cookBooks.getValue(bookName))
                recipesInBook.add(recipes.getValue(recId))
        }
        return recipesInBook
    }

    //returns a map of cookBook name, and first recipe
    fun getCookBooksList() : HashMap<String,AppRecipe>{
        val map = HashMap<String,AppRecipe>()
        for(book in cookBooks.keys){
            if(cookBooks.getValue(book).isEmpty()) map.put(book,exampleRec())
            else map.put(book,recipes.getValue(cookBooks.getValue(book).first()))
        }
        return map
    }

    companion object {
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

