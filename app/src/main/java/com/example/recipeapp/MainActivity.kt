package com.example.recipeapp

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipeapp.ui.theme.RecipeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainStructure()
        }
    }
}


//=================== Composables for different views ===================
//Main scrollable page of app
@Composable
fun HomePage(name: String, modifier: Modifier = Modifier){
    val painter = painterResource(R.drawable.p1)
    //columns = GridCells.Adaptive(minSize = 128.dp)
    LazyVerticalGrid(GridCells.Adaptive(minSize = 100.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(20) {
            ImageCard(painter,"Cookie","Cookies", Modifier.padding(0.dp))
        }
    }
}

@Composable
fun RecipePage(recipe: Recipe, modifier: Modifier = Modifier){
    Column(modifier.fillMaxWidth().padding(7.dp).verticalScroll(rememberScrollState())) {
        Text(recipe.name, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        FlowRow() { }
        //Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {            for (tag in recipe.tags){
        //                Card(shape = RoundedCornerShape(15.dp)){
        //                    Text(tag,Modifier.padding(9.dp,4.dp),fontSize = 18.sp,)
        //                }
        //            }}Arrangement.spacedBy(3.dp)
    }
}

//Entrance for the whole composable structure
@Composable
fun MainStructure(){
    RecipeAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            //HomePage("Guy", Modifier.padding(innerPadding))
            RecipePage(Recipe("Cookie", "1. Chocolate chips", "bake", "12", mutableListOf("made","chocolate","hey","hey","hey","hey","hey"),mutableListOf("dessert"),5),Modifier.padding(innerPadding) )
        }
    }
}

//============================= Extras =============================
//Image of recipe and name
@Composable
fun ImageCard (painter: Painter, contentDesc: String, title: String, modifier: Modifier = Modifier){
    Card(
        modifier,
    ) {
        //each element is naturally taking enough space only to fit itself. If specifying "fillmaxsize"
        //it will fit more than it needs, based on other elements
        //!!!!!!!Modifier order matters.
        Column(Modifier.background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {

            //aspect ratio - 1< means more squat, 1>means more thin
            Card( Modifier.aspectRatio(1.2f),
                elevation = CardDefaults.cardElevation(5.dp),
                shape = RoundedCornerShape(15.dp)){
                Image(painter,contentDesc, contentScale = ContentScale.Crop)
            }
            Text(title,style = TextStyle(color = Color.Black, fontSize = 16.sp), maxLines = 2, overflow = TextOverflow.Ellipsis)
        }

    }
}

class Recipe (var name: String, var ingredients: String, var recipeInstruct: String,
                var servings: String, var tags: MutableList<String>,
                    var recipeBooks: MutableList<String>, var rating: Int){}

@Preview(showBackground = true, showSystemUi = true)
//@Preview(device = Devices.PIXEL_TABLET, showSystemUi = true)
@Composable
fun AppPreview() {
    MainStructure()
}