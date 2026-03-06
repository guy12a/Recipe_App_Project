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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipeapp.ui.theme.RecipeAppTheme
//https://github.com/a914-gowtham/compose-ratingbar
import com.gowtham.ratingbar.RatingBar
import androidx.compose.runtime.*

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
    var headerStyles = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold)
    var textStyles = TextStyle(fontSize = 16.sp)
    Column(modifier.fillMaxWidth().padding(10.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(recipe.name, style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
        var rating: Float by remember { mutableStateOf(recipe.rating) }
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Start) { RatingBar(value = rating, onValueChange = {rating = it}, onRatingChanged = {}) }
        FlowRow(modifier= Modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            for (tag in recipe.tags){
                Card(shape = RoundedCornerShape(15.dp)){
                    Text(tag,Modifier.padding(9.dp,4.dp),fontSize = 18.sp,)
                }
            }
        }
        Card( Modifier.fillMaxWidth().aspectRatio(1.25f).padding(0.dp,10.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            shape = RoundedCornerShape(10.dp)){
            Image(recipe.img,recipe.name, contentScale = ContentScale.Crop)
        }
        Text("Ingredients", style = headerStyles)
        Text(recipe.ingredients, style = textStyles)
        Text("", fontSize = 26.sp)
        Text("Instructions", style = headerStyles)
        Text(recipe.recipeInstruct, style = textStyles)
    }
}

//Entrance for the whole composable structure
@Composable
fun MainStructure(){
    RecipeAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            //HomePage("Guy", Modifier.padding(innerPadding))
            RecipePage(Recipe("Shakshuka Recipe (Easy & Traditional)", "1 and 1/2 cups (180g) gingersnap cookie crumbs*\n" +
                    "\n" +
                    "1/4 teaspoon each: ground ginger and ground cinnamon\n" +
                    "1/4 cup (4 Tbsp; 56g) unsalted butter, melted\n" +
                    "\n" +
                    "1/4 cup (50g) granulated sugar\n" +
                    "\n" +
                    "32 ounces (904g) full-fat brick cream cheese, softened to room temperature\n" +
                    "\n" +
                    "1 and 1/2 cups (300g) granulated sugar\n" +
                    "\n" +
                    "1/3 cup (80g) full-fat sour cream, at room temperature\n" +
                    "\n" +
                    "1 teaspoon pure vanilla extract\n" +
                    "\n" +
                    "3 large eggs, at room temperature\n" +
                    "\n" +
                    "1 cup (227g) pumpkin puree*\n" +
                    "\n" +
                    "1 and 1/2 teaspoons ground cinnamon\n" +
                    "\n" +
                    "1 and 1/2 teaspoons store-bought or homemade pumpkin pie spice*\n" +
                    "\n" +
                    "topping suggestions: salted caramel and whipped cream",
                "Adjust the oven rack to the lower-middle position and preheat oven to 350°F (177°C).\n" +
                        "\n" +
                        "Make the crust\n" +
                        "\n" +
                        "Using a food processor, pulse the gingersnap cookies into crumbs. Pour into a medium bowl and stir in ginger, cinnamon, sugar, and melted butter until combined. (You can also pulse it all together in the food processor.) Mixture will be sandy. Press firmly into the bottom and slightly up the sides of a 9-inch or 10-inch springform pan. No need to grease the pan first. I use the bottom of a measuring cup to pack the crust down tightly. Pre-bake for 10 minutes. Remove from the oven and place the hot pan on a large piece of aluminum foil. The foil will wrap around the pan for the water bath in step 5. Allow crust to slightly cool as you prepare the filling.\n" +
                        "\n" +
                        "Make the filling:\n" +
                        "\n" +
                        "Using a handheld or stand mixer fitted with a paddle attachment, beat the cream cheese and granulated sugar together on medium-high speed in a large bowl until the mixture is smooth and creamy, about 2 minutes. Add the sour cream and vanilla extract, then beat until fully combined. On medium speed, add the eggs one at a time, beating after each addition until just blended. After the final egg is incorporated into the batter, stop mixing. To help prevent the cheesecake from deflating and cracking as it cools, avoid over-mixing the batter as best you can.\n" +
                        "\n" +
                        "Scoop out 2 cups of batter and place in a medium mixing bowl. Stir in the pumpkin, cinnamon, and pumpkin pie spice until combined. Begin adding spoonfuls of each batter, the plain and the pumpkin, on top of the crust. Alternate until all the batter is used and pan is filled. Using a toothpick or knife, swirl the batters together by dragging the toothpick top to bottom, then left to right.\n" +
                        "\n" +
                        "Prepare the simple water bath (see note)\n" +
                        "\n" +
                        "If needed for extra visuals, see my How to Make a Cheesecake Water Bath; the visual guide will assist you in this step. Boil a pot of water. You need 1 inch of water in your roasting pan for the water bath, so make sure you boil enough. I use an entire kettle of hot water. Place the pan inside of a large roasting pan. Carefully pour the hot water inside of the pan and place in the oven. (Or you can place the roasting pan in the oven first, then pour the hot water in. Whichever is easier for you.)\n" +
                        "\n" +
                        "(Note: if you notice the cheesecake browning too quickly on top, tent it with aluminum foil halfway through baking.) Bake cheesecake for 55-70 minutes or until the center is almost set. When it’s done, the center of the cheesecake will slightly wobble if you gently shake the pan. Turn the oven off and open the oven door slightly. Let the cheesecake sit in the oven for 1 hour as it cools down. Remove cheesecake from the oven and allow to cool completely at room temperature, then refrigerate the cheesecake for at least 4 hours or overnight.\n" +
                        "\n" +
                        "Use a knife to loosen the chilled cheesecake from the rim of the springform pan, then remove the rim. Add toppings, if desired. Using a clean sharp knife, cut into slices for serving. For neat slices, wipe the knife clean and dip into warm water between each slice.\n" +
                        "\n" +
                        "Cover and store leftover cheesecake in the refrigerator for up to 5 days.", "12",
                mutableListOf("didnt make","chocolate","brian","cookies","eggyolks","hey","hey"),
                mutableListOf("dessert"),5f, painterResource(R.drawable)),
                Modifier.padding(innerPadding) )
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
                    var recipeBooks: MutableList<String>, var rating: Float, var img: Painter){}

@Preview(showBackground = true, showSystemUi = true)
//@Preview(device = Devices.PIXEL_TABLET, showSystemUi = true)
@Composable
fun AppPreview() {
    MainStructure()
}