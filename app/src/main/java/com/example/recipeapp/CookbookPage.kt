package com.example.recipeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.recipeapp.ui.theme.RecipeAppTheme
import kotlin.collections.mutableListOf

/* Add options to:
    Add new recipe
    Edit cookbook name
* */

//Cookbook page gets a SearchUtils and the name of the cookbook
@Composable
fun CookbookPage(searchUtils : SearchUtils,
                 name: String?,
                 modifier: Modifier = Modifier,
                 navController: NavController){

    //val painter = painterResource(R.drawable.placeholder)
    //columns = GridCells.Adaptive(minSize = 128.dp)
    var map = HashMap<String,AppRecipe>()
    var title = ""
    if(name == null){
        title = "Home"
        map = searchUtils.getCookBooksList()
    }
    else{
        title = name
        map = searchUtils.getBookRecipesAsMap(title)
    }


    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp))
    {
        item (span = { GridItemSpan(maxCurrentLineSpan) }){
            Text(title, style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
        }
        items(map.entries.toList()) { entry ->
            val (cardName, recipe) = entry
            if(name==null)
                ImageCard(recipe.name,
                    cardName,
                    Modifier.padding(0.dp),
                    {navController.navigate(
                        CookbookPageNav(cardName)
                    )})
            else
                ImageCard(recipe.name,
                    cardName,
                    Modifier.padding(0.dp),
                    {navController.navigate(
                        RecipePageNav(recipe.id)
                    )})
        }
    }

    /*
    if(name == "Recipe Books")
        lst = searchUtils.getCookBooksList()
    else{
        val lst2 = searchUtils.getBookRecipes(name)
        if(lst2 != null)
            lst = lst2
    }*/
}

//Image + Recipe Name for the cookbook page
//Or image + cookbook name for the all books page
@Composable
fun ImageCard (recipeName: String,
               cardTxt : String,
               modifier: Modifier = Modifier,
               onClick: () -> Unit){
    Card(modifier) {
        Column(Modifier.background(Color.White).
                clickable{
                    onClick()
                },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(7.dp))
        {
            //aspect ratio - 1< means more squat, 1>means more thin
            Card( Modifier.aspectRatio(1.2f),
                elevation = CardDefaults.cardElevation(5.dp),
                shape = RoundedCornerShape(15.dp)) {
                AsyncImage(
                    model = "file:///android_asset/pictures/$recipeName.jpg",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder)
                )
            }
            Text(cardTxt,Modifier.padding(2.dp),style = TextStyle(color = Color.Black, fontSize = 15.sp, textAlign = TextAlign.Center), maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
//@Preview(device = Devices.PIXEL_TABLET, showSystemUi = true)
@Composable
fun AppPreview() {
    RecipeAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            CookbookPage(SearchUtils(), "Home", Modifier.padding(innerPadding), navController = rememberNavController())

        }
    }
}

