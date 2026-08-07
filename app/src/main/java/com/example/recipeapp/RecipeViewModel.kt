package com.example.recipeapp

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecipeViewModel (
    private val recipeRepository: RecipeRepository,
    recipeId: String
) : ViewModel() {

    val recipe =
        recipeRepository.recipes
            .map { it[recipeId] }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTag(context: Context, tag: String) {
        val current = recipe.value ?: return

        val updated = current.copy(
            tags = current.tags + tag,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update your existing system
        recipeRepository.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeTag(context: Context, tag: String) {
        val current = recipe.value ?: return
        val updated = current.copy(
            tags = current.tags - tag,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update your existing system
        recipeRepository.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editStage(context: Context, updatedStage: RecipeStage) {
        val current = recipe.value ?: return

        val newStages = mutableListOf<RecipeStage>()
        if(current.stages.isEmpty())
            newStages.add(updatedStage)

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

        // update your existing system
        recipeRepository.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addStage(context: Context){
        val current = recipe.value ?: return
        val updated = current.copy(
            stages = current.stages+ RecipeStage(""),
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update your existing system
        recipeRepository.updateRecipe(context,updated)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun removeStage(context: Context, removedStage: RecipeStage){
        val current = recipe.value ?: return
        val updated = current.copy(
            stages = current.stages- removedStage,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update your existing system
        recipeRepository.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editRating(context: Context, updatedRating: Float) {
        val current = recipe.value ?: return
        val updated = current.copy(
            rating = updatedRating,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update your existing system
        recipeRepository.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editName(context: Context, updatedName: String) {
        val current = recipe.value ?: return
        val updated = current.copy(
            name = updatedName,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update your existing system
        recipeRepository.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addBook(context: Context, book: String) {
        val current = recipe.value ?: return
        val updated = current.copy(
            cookbooks = current.cookbooks + book,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update your existing system
        recipeRepository.updateRecipe(context,updated)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeBook(context: Context, book: String) {
        val current = recipe.value ?: return
        val updated = current.copy(
            cookbooks = current.cookbooks - book,
            dateChanged = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )

        // update your existing system
        recipeRepository.updateRecipe(context,updated)
    }

    fun addImage(context: Context, image: Uri){

    }

}


class RecipeViewModelFactory(
    private val recipeRepository: RecipeRepository,
    private val recipeId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeViewModel(recipeRepository, recipeId) as T
    }
}