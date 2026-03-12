package com.example.recipeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

/* Add options to:
    Add new recipe
    Edit cookbook name
    Make title part of the scrollable vertical column
* */

//Cookbook page just gets a list of recipes to present, and its name
@Composable
fun CookbookPage(cookBookName: String, recipes: List<AppRecipe>, modifier: Modifier = Modifier){
    //val painter = painterResource(R.drawable.placeholder)
    //columns = GridCells.Adaptive(minSize = 128.dp)

    Spacer(Modifier.height(50.dp))
    Text(cookBookName, style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
    Spacer(Modifier.height(50.dp))

    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(count = recipes.size) { index ->
            ImageCard(recipes[index], Modifier.padding(0.dp))
        }
    }
}

//Image + Recipe Name for the cookbook page
@Composable
fun ImageCard (recipe: AppRecipe, modifier: Modifier = Modifier){
    Card(
        modifier,
    ) {
        //each element is naturally taking enough space only to fit itself. If specifying "fillmaxsize"
        //it will fit more than it needs, based on other elements
        //!!!!!!!Modifier order matters.
        Column(Modifier.background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            //aspect ratio - 1< means more squat, 1>means more thin
            Card( Modifier.aspectRatio(1.2f),
                elevation = CardDefaults.cardElevation(5.dp),
                shape = RoundedCornerShape(15.dp)) {
                AsyncImage(
                    model = "file:///android_asset/pictures/"+ recipe.name + ".jpg",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder)
                )
                //Image(painter,contentDesc, contentScale = ContentScale.Crop)
            }
            Text(recipe.name,style = TextStyle(color = Color.Black, fontSize = 16.sp), maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}