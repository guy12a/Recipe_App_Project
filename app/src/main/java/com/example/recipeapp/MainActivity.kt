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

        val searchUtils = SearchUtils()
        searchUtils.loadRecipes(this)

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
                startDestination = CookbookPageNav(),
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<CookbookPageNav>{ backStackEntry -> val args = backStackEntry.toRoute<CookbookPageNav>()
                    CookbookPage(searchUtils,
                        name = args.cookbookName,
                        Modifier.padding(innerPadding),
                        navController = navController)
                }
                composable<RecipePageNav> { backStackEntry -> val args = backStackEntry.toRoute<RecipePageNav>()
                    RecipePage(
                        searchUtils,
                        recipeId = args.recipeId,
                        Modifier.padding(innerPadding),
                        navController = navController,
                        from = args.from
                    )
                }
            }
        }
    }
}

@Serializable
data class CookbookPageNav(
    val cookbookName: String? = null
)

@Serializable
data class RecipePageNav(
    val recipeId: String,
    val from: String
)

//RecipePage(recipes[0], Modifier.padding(innerPadding))
/*AsyncImage(
    model = "file:///android_asset/pictures/apple_pie.jpg",
    contentDescription = null,
    placeholder = painterResource(R.drawable.placeholder)
)
        //each element is naturally taking enough space only to fit itself. If specifying "fillmaxsize"
        //it will fit more than it needs, based on other elements
        //!!!!!!!Modifier order matters.

                        //Image(painter,contentDesc, contentScale = ContentScale.Crop)


*/




