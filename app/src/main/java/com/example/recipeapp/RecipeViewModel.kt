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
}

class RecipeViewModelFactory(
    private val searchUtils: SearchUtils
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeViewModel(searchUtils) as T
    }
}