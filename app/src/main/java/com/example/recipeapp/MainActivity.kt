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
        // figure out how to only load umami ones upon installation, or merge

        val searchUtils = SearchUtils(this)
        searchUtils.loadRecipes()

        /*
        var recipes = getUmamiAsApp(this)
        for(recipe in recipes){
            saveRecipe(this,recipe)
        }
        */

        val sweets = searchUtils.getCookBook("Sweets & Desserts")

        enableEdgeToEdge()
        setContent {
            if(sweets != null)
                MainStructure(searchUtils,"Recipe Books")
        }
    }
}

//Entrance for the whole composable structure
@Composable
fun MainStructure(searchUtils :SearchUtils, name: String){
    RecipeAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            CookbookPage(searchUtils, name, Modifier.padding(innerPadding))
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
    //MainStructure(mutableListOf(exampleRec(),exampleRec(),exampleRec(),exampleRec()),"Home")
}

