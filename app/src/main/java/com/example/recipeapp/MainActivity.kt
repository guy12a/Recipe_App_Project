package com.example.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.recipeapp.ui.theme.RecipeAppTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //This sections loads the umami recipes from scratch
        //and saves them in app recipe format
        // figure out how to only load umami ones upon installation, or merge

        /*
        var recipes = getUmamiAsApp(this)

        for(recipe in recipes){
            saveRecipe(this,recipe)
        }
        */

        val searchUtils = SearchUtils(this)
        searchUtils.loadRecipes()

        enableEdgeToEdge()
        setContent {
            MainStructure(searchUtils)
        }
    }
}

//Entrance for the whole composable structure
@Composable
fun MainStructure(searchUtils :SearchUtils){
    RecipeAppTheme {
        val navController = rememberNavController()
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = CookbookScreen("Recipe Books"),
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<CookbookScreen>{ backStackEntry -> val args = backStackEntry.toRoute<CookbookScreen>()
                    CookbookPage(searchUtils,
                        name = args.cookbookName,
                        Modifier.padding(innerPadding),
                        navController = navController)
                }
                composable<RecipeScreen> { backStackEntry -> val args = backStackEntry.toRoute<RecipeScreen>()
                    RecipePage(
                        searchUtils,
                        recipeId = args.recipeId,
                        Modifier.padding(innerPadding),
                        navController = navController,
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
//@Preview(device = Devices.PIXEL_TABLET, showSystemUi = true)
@Composable
fun AppPreview() {
    //MainStructure(mutableListOf(exampleRec(),exampleRec(),exampleRec(),exampleRec()),"Home")
}

@Serializable
data class CookbookScreen(
    val cookbookName: String
)

@Serializable
data class RecipeScreen(
    val recipeId: String
)

//RecipePage(recipes[0], Modifier.padding(innerPadding))
/*AsyncImage(
    model = "file:///android_asset/pictures/apple_pie.jpg",
    contentDescription = null,
    placeholder = painterResource(R.drawable.placeholder)
)*/


