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
import com.example.recipeapp.ui.theme.RecipeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //This sections loads the umami recipes from scratch
        //and saves them in app recipe format

        /*
        var recipes = getUmamiAsApp(this)
        for(recipe in recipes){
            saveRecipe(this,recipe)
        }
        */

        //loads already formatted appRecipe jsons, ID based
        var recipes = loadSavedRecipes(this)



        enableEdgeToEdge()
        setContent {
            MainStructure(recipes)
        }
    }
}

//Entrance for the whole composable structure
@Composable
fun MainStructure(recipes: List<AppRecipe>){
    RecipeAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            CookbookPage("Home",recipes, Modifier.padding(innerPadding))
            //RecipePage(recipes[0], Modifier.padding(innerPadding))
            /*AsyncImage(
                model = "file:///android_asset/pictures/apple_pie.jpg",
                contentDescription = null,
                placeholder = painterResource(R.drawable.placeholder)
            )*/
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
//@Preview(device = Devices.PIXEL_TABLET, showSystemUi = true)
@Composable
fun AppPreview() {
    MainStructure(mutableListOf(exampleRec()))
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

