package com.example.recipeapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecipeViewModel (
    private val searchUtils: SearchUtils
) : ViewModel() {
    private val _recipe = MutableStateFlow<AppRecipe?>(null) //changeable
    val recipe: StateFlow<AppRecipe?> = _recipe //read only

    fun setRecipe(recipe: AppRecipe) {
        _recipe.value = recipe
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTag(context: Context, tag: String) {
        val current = _recipe.value ?: return
        val updated = current.copy(
            tags = current.tags + tag,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update state (this updates UI)
        _recipe.value = updated


        // update your existing system
        searchUtils.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeTag(context: Context, tag: String) {
        val current = _recipe.value ?: return
        val updated = current.copy(
            tags = current.tags - tag,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update state (this updates UI)
        _recipe.value = updated

        // update your existing system
        searchUtils.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editStage(context: Context, updatedStage: RecipeStage) {
        val current = _recipe.value ?: return

        val newStages = mutableListOf<RecipeStage>()
        for(stage in current.stages){
            if(stage.id == updatedStage.id){
                newStages.add(updatedStage)
            }
            else{
                newStages.add(stage)
            }
        }
        val updated = current.copy(
            stages = newStages,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        // update state (this updates UI)
        _recipe.value = updated

        // update your existing system
        searchUtils.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addStage(context: Context){
        val current = _recipe.value ?: return
        val updated = current.copy(
            stages = current.stages+ RecipeStage(""),
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        // update state (this updates UI)
        _recipe.value = updated

        // update your existing system
        searchUtils.updateRecipe(context,updated)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun removeStage(context: Context, removedStage: RecipeStage){
        val current = _recipe.value ?: return
        val updated = current.copy(
            stages = current.stages- removedStage,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        // update state (this updates UI)
        _recipe.value = updated

        // update your existing system
        searchUtils.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editRating(context: Context, updatedRating: Float) {
        val current = _recipe.value ?: return
        val updated = current.copy(
            rating = updatedRating,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        // update state (this updates UI)
        _recipe.value = updated

        // update your existing system
        searchUtils.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editName(context: Context, updatedName: String) {
        val current = _recipe.value ?: return
        val updated = current.copy(
            name = updatedName,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        // update state (this updates UI)
        _recipe.value = updated

        // update your existing system
        searchUtils.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addBook(context: Context, book: String) {
        val current = _recipe.value ?: return
        val updated = current.copy(
            recipeBooks = current.recipeBooks + book,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update state (this updates UI)
        _recipe.value = updated


        // update your existing system
        searchUtils.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeBook(context: Context, book: String) {
        val current = _recipe.value ?: return
        val updated = current.copy(
            recipeBooks = current.recipeBooks - book,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update state (this updates UI)
        _recipe.value = updated

        // update your existing system
        searchUtils.updateRecipe(context,updated)
    }

}

class RecipeViewModelFactory(
    private val searchUtils: SearchUtils
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeViewModel(searchUtils) as T
    }
}