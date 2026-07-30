package com.example.recipeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.emptyList

class CookbookViewModel (
    private val repository: RecipeRepository,
    private val cookbookName: String?
) : ViewModel() {

    val recipes =
        combine(
            repository.recipes,
            repository.cookBooks
        ) { _, _ ->
            if (cookbookName == null)
                repository.getCookBooksList()
            else
                repository.getBookRecipesSorted(cookbookName) { it.dateChanged ?: "" }
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
}

class BookViewModelFactory(
    private val recipeRepository: RecipeRepository,
    private val bookname: String?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CookbookViewModel(recipeRepository, bookname) as T
    }
}