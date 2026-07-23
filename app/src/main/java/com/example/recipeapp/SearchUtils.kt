package com.example.recipeapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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

    //get recipes filtered based off filters and then sorted based off of lambda
    fun <T : Comparable<T>> getRecipesSortedFiltered(
        bookName: String,
        tags: Set<String> = emptySet(),
        negTags: Set<String> = emptySet(),
        keyword: String? = null,
        rating: Float? = null,
        selector: (AppRecipe) -> T
    ) : List<Pair<String, AppRecipe>> {

        return getBookRecipesFiltered(bookName,tags,negTags,keyword,rating)
            .sortedBy { (_, recipe) -> selector(recipe) }

    }

    //get recipes filtered based off of tags, negative tags, keyword, and rating
    fun getBookRecipesFiltered(
        bookName: String,
        tags: Set<String> = emptySet(),
        negTags: Set<String> = emptySet(),
        keyword: String? = null,
        rating: Float? = null
    ): List<Pair<String, AppRecipe>> {

        return getBookRecipesAsList(bookName)
            .filter { (_, recipe) ->

                val matchesTags = tags.isEmpty() || tags.all { tag ->
                                recipe.tags.contains(tag)
                            }

                val matchesNegTags = negTags.isEmpty() || negTags.none { tag ->
                    recipe.tags.contains(tag)
                }

                val matchesKeyword =
                    keyword.isNullOrBlank() ||
                            recipe.name.contains(keyword, ignoreCase = true)

                val matchesRating = rating == null || recipe.rating >= rating

                matchesTags && matchesKeyword && matchesRating && matchesNegTags
            }
    }

    //returns a list of recipes, sorted based off of lambda
    //{it.datePublished ?: ""} for String?, {it.name} for name...
    fun <T : Comparable<T>> getBookRecipesSorted(
            bookName: String,
            selector: (AppRecipe) -> T
    ) : List<Pair<String, AppRecipe>> {

        return getBookRecipesAsList(bookName)
            .sortedBy { (_, recipe) -> selector(recipe) }

    }

    //returns list of recipeName and AppRecipe, based on book
    //Used for a specific cookbook
    fun getBookRecipesAsList(bookName: String) : List<Pair<String, AppRecipe>>{
        val recipesInBook = mutableListOf<Pair<String, AppRecipe>>()
        if(bookName == allRecipesName){
            for(entry in recipes.entries){
                recipesInBook.add(entry.value.name to entry.value)
            }
        }
        else if(cookBooks.containsKey(bookName)){
            for(recId in cookBooks.getValue(bookName)) {
                val rec = recipes.getValue(recId)
                recipesInBook.add(rec.name to rec)
            }
        }
        return recipesInBook
    }

    //returns a list of cookBook name, and first recipe
    //Used for home page
    fun getCookBooksList() : List<Pair<String, AppRecipe>> {
        val list = mutableListOf<Pair<String, AppRecipe>>()
        var flag = false
        for(book in cookBooks.keys){
            if(cookBooks.getValue(book).isEmpty())
                list.add(book to exampleRec())
            else{
                list.add(book to recipes.getValue(cookBooks.getValue(book).first()))
                if(!flag){
                    flag = true
                    list.add(allRecipesName to recipes.getValue(cookBooks.getValue(book).first()))
                }
            }
        }
        if(!flag) list.add (allRecipesName to recipes.entries.toList().first().value)
        return list
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
            if(recipe.cookbooks.isEmpty()) cookBooks.getOrPut(toSortName){mutableListOf()}.add(recipe.id)
            for(book in recipe.cookbooks){
                cookBooks.getOrPut(book){mutableListOf()}.add(recipe.id)
            }
            for(tag in recipe.tags){
                tagsToRecipes.getOrPut(tag){mutableListOf()}.add(recipe.id)
            }
        }
    }

    fun getCookbooks():List<String>{
        return cookBooks.keys.toList().sorted()
    }

    fun getCookbooksWithout(recipe: AppRecipe): List<String>{
        return getCookbooks().filter { book -> book !in recipe.cookbooks && book!=toSortName}
    }

    //returns all tags sorted alphabetically
    fun getTags(): List<String>{
        return tagsToRecipes.keys.toList().sorted()
    }

    //returns all tags not contained in recipe
    fun getTagsWithout(recipe: AppRecipe) :List<String>{
        return getTags().filter {tag -> tag !in recipe.tags}
    }

    //rewrite a recipe to disk
    fun updateRecipe(context: Context,recipe: AppRecipe){
        var oldRecipe = recipes[recipe.id]

        if (oldRecipe != null) {
            if(oldRecipe.tags != recipe.tags){
                //remove recipe from all tags
                for(tag in oldRecipe.tags){
                    tagsToRecipes[tag]?.remove(oldRecipe.id)

                    if (tagsToRecipes[tag]?.isEmpty() == true) {
                        tagsToRecipes.remove(tag)
                    }
                }

                //add it back to all tags
                for (tag in recipe.tags) {
                    val list = tagsToRecipes.getOrPut(tag) { mutableListOf() }
                    if (!list.contains(recipe.id)) {
                        list.add(recipe.id)
                    }
                }
            }
            if(oldRecipe.cookbooks != recipe.cookbooks){
                cookBooks[toSortName]?.remove(oldRecipe.id)
                if(cookBooks[toSortName]?.isEmpty()==true) cookBooks.remove(toSortName)

                for(book in oldRecipe.cookbooks){
                    cookBooks[book]?.remove(oldRecipe.id)

                    /*
                    if (cookBooks[book]?.isEmpty() == true) {
                        cookBooks.remove(book)
                    }
                     */
                }

                if(recipe.cookbooks.isEmpty()) cookBooks.getOrPut(toSortName){mutableListOf()}.add(recipe.id)
                for (book in recipe.cookbooks) {
                    val list = cookBooks.getOrPut(book) { mutableListOf() }
                    if (!list.contains(recipe.id)) {
                        list.add(recipe.id)
                    }
                }
            }
        }

        recipes[recipe.id]=recipe

        saveRecipe(context,recipe)
    }

    fun createNewRecipe(context: Context, cookBook: String, newName: String) : String {
        var newRecipe = AppRecipe(newName, cookbooks = listOf(cookBook))
        recipes[newRecipe.id] = newRecipe

        val list = cookBooks.getOrPut(cookBook) { mutableListOf() }
        if (!list.contains(newRecipe.id)) {
            list.add(newRecipe.id)
        }

        saveRecipe(context,newRecipe)
        return newRecipe.id
    }

    fun createNewBook(newCookBook : String){
        cookBooks.getOrPut(newCookBook) {mutableListOf()}
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
            val ingred = mutableListOf<String>()
            ingred.add("flour")
            ingred.add("water")
            val instruct = mutableListOf<String>()
            instruct.add("mix")
            instruct.add("bake")

            val stages = mutableListOf<RecipeStage>()
            stages.add(RecipeStage("Prep",ingred,instruct))
            stages.add(RecipeStage("Bake", ingredients = emptyList(),emptyList()))

            return AppRecipe("30 min Choclate Chip Cookies",
                lst,
                "2023-10-06T14:21:57.559Z",
                "2023-10-06T14:21:57.559Z",tags, stages=stages,
                cookbooks = mutableListOf("Sweets & Desserts"))
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




