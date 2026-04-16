package com.example.recipeapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.recipeapp.ui.theme.RecipeAppTheme
import com.gowtham.ratingbar.RatingBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment

//https://github.com/a914-gowtham/compose-ratingbar


/*Add editing options, editing json file:
    Edit Title
    Edit Stars
    Edit Tags
    Edit Ingredients
    Edit Instructions
* */

@Composable
fun RecipePageLayout(recipe : AppRecipe,
                     modifier: Modifier = Modifier,
                     navController: NavController,
                     from:String)
{
    Column(
        modifier.fillMaxWidth().
        padding(10.dp).
        verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp))
    {
        //return button - add text?
        //Add more icons: right click 'res' -> 'new' -> 'Vector Asset'
        Row(modifier = Modifier.clickable{navController.popBackStack()}, verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(R.drawable.baseline_arrow_back_ios_24), contentDescription = "")
            Text(from, style = StyleUtils.regularText)
        }
        //IconButton(, modifier = Modifier.size(30.dp))
        
        //Recipe Title
        Text(recipe.name, style = StyleUtils.bigTitle)

        //Star Rating
        var rating: Float by remember { mutableStateOf(recipe.rating) }
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Start) { RatingBar(value = rating, onValueChange = {rating = it}, onRatingChanged = {}) }

        //Tags Bar
        FlowRow(modifier= Modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            for (tag in recipe.tags){
                Card(shape = RoundedCornerShape(15.dp)){
                    Text(tag,Modifier.padding(9.dp,4.dp),fontSize = 18.sp)
                }
            }
        }

        //Image of Recipe
        Card( Modifier.fillMaxWidth().aspectRatio(1.25f).padding(0.dp,10.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            shape = RoundedCornerShape(10.dp)){
            AsyncImage(
                model = "file:///android_asset/pictures/"+ recipe.name + ".jpg",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder)
            )
            //Image(recipe.img,recipe.name, contentScale = ContentScale.Crop)
        }

        //Ingredients
        Text("Ingredients", style = StyleUtils.smallTitle)
        Text(recipe.getIngredAsText(), style = StyleUtils.regularText)

        Spacer(Modifier.height(25.dp))

        //Insturctions
        Text("Instructions", style = StyleUtils.smallTitle)
        Text(recipe.getInstructAsText(), style = StyleUtils.regularText)
    }
}

@Composable
fun RecipePage(searchUtils : SearchUtils,
               recipeId: String,
               modifier: Modifier = Modifier,
               navController: NavController,
               from: String
){
    var recipe = searchUtils.getRecipe(recipeId)
    RecipePageLayout(recipe,modifier,navController,from)
}





