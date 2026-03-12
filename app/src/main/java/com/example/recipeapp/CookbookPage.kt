package com.example.recipeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

/* Add options to:
    Add new recipe
    Edit cookbook name
* */

//Cookbook page just gets a list of recipes to present, and its name
@Composable
fun CookbookPage(searchUtils : SearchUtils, name: String, modifier: Modifier = Modifier){
    //val painter = painterResource(R.drawable.placeholder)
    //columns = GridCells.Adaptive(minSize = 128.dp)
    var lst = mutableListOf<AppRecipe>()
    if(name == "Recipe Books")
        lst = searchUtils.getCookBooksList()
    else{
        val lst2 = searchUtils.getCookBook(name)
        if(lst2 != null)
            lst = lst2
    }

    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp))
    {
        item (span = { GridItemSpan(maxCurrentLineSpan) }){
            Text(name, style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
        }
        items(count = lst.size) { index ->
            if(name=="Recipe Books")
                ImageCard(lst[index],searchUtils.cookBooks[index], Modifier.padding(0.dp))
            else
                ImageCard(lst[index],lst[index].name, Modifier.padding(0.dp))
        }
    }
}

//Image + Recipe Name for the cookbook page
@Composable
fun ImageCard (recipe: AppRecipe,cardTxt : String, modifier: Modifier = Modifier){
    Card(
        modifier,
    ) {
        //each element is naturally taking enough space only to fit itself. If specifying "fillmaxsize"
        //it will fit more than it needs, based on other elements
        //!!!!!!!Modifier order matters.
        Column(Modifier.background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)) {

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
            Text(cardTxt,Modifier.padding(2.dp),style = TextStyle(color = Color.Black, fontSize = 15.sp, textAlign = TextAlign.Center), maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}

