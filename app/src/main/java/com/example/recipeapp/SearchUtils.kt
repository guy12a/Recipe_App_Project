package com.example.recipeapp

import android.content.Context

class SearchUtils (val context: Context) {
    var recipes : MutableList<AppRecipe> = mutableListOf<AppRecipe>()
    var cookBooks: MutableList<String> = mutableListOf()

    fun getRecipe(recipeId: String): AppRecipe{
        for(recipe in recipes){
            if(recipe.id == recipeId){
                return recipe;
            }
        }
        return exampleRec()
    }

    //loads already formatted appRecipe jsons, ID based
    fun loadRecipes(){
        val recps = loadSavedRecipes(context)
        for (recipe in recps){
            recipes.add(recipe)
            for(book in recipe.recipeBooks){
                if(!cookBooks.contains(book))
                    cookBooks.add(book)
            }
        }
    }

    fun getCookBook(bookName: String) : MutableList<AppRecipe>? {
        val recipesInBook = mutableListOf<AppRecipe>()
        if(cookBooks.contains(bookName)){
            for(recipe in recipes){
                if(recipe.recipeBooks.contains(bookName))
                    recipesInBook.add(recipe)
            }
            return recipesInBook
        }
        return null
    }

    fun getCookBooksList() : MutableList<AppRecipe>{
        val lst = mutableListOf<AppRecipe>()
        var leng = 0
        for(book in cookBooks){
            var temp = leng
            for(recipe in recipes){
                if(recipe.recipeBooks.contains(book)) {
                    lst.add(recipe)
                    leng = leng+1
                    break
                }
            }
            if(temp==leng){
                lst.add(exampleRec())
            }
        }
        return lst
    }

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
            recipeBooks = mutableListOf<String>("Sweets & Desserts"))
    }
}