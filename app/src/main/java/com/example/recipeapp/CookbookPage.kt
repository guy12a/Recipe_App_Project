package com.example.recipeapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil3.compose.AsyncImage

/* Add options to:
    Add new recipe
    Edit cookbook name
* */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookbookPageLayout(
    searchUtils : SearchUtils,
    title : String,
    recipes:List<Pair<String, AppRecipe>>,
    name:String?,
    modifier: Modifier = Modifier,
    navController: NavController
){
    val context = LocalContext.current

    //controls the adding of tags using bottom sheet
    var openAddRecipeSheet by remember { mutableStateOf (false) }
    val addRecipeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, { newValue ->
        newValue != SheetValue.Hidden
    })

    //val painter = painterResource(R.drawable.placeholder)
    //columns = GridCells.Adaptive(minSize = 128.dp)
    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp))
    {
        item (span = { GridItemSpan(maxCurrentLineSpan) }){
            var newTitle = title
            if(title!=SearchUtils.homeName) newTitle+= " - " + recipes.size + " recipes"
            Text(newTitle, style = StyleUtils.bigTitle)
        }
        if(name==null){

        }
        else if(name!= SearchUtils.allRecipesName){
            item{
                AddRecipeCard({openAddRecipeSheet=true})
            }
        }
        items(recipes) { (cardName, recipe) ->
            if(name==null)
                ImageCard(recipe.name,
                    cardName,
                    {navController.navigate(
                        CookbookPageNav(cardName)
                    )}
                )
            else
                ImageCard(recipe.name,
                    cardName,
                    {navController.navigate(
                        RecipePageNav(recipe.id,title)
                    )})
        }
    }

    if(openAddRecipeSheet){
        CreateRecipeDialog(
            { newRecipeName -> navController.navigate(
                RecipePageNav(searchUtils.createNewRecipe(context,title,newRecipeName),title)) },
            {openAddRecipeSheet=false})
    }
}

@Composable
fun CreateRecipeDialog(
    onCreate: (String) -> Unit,
    onDismiss: () -> Unit
){
    val textFieldState = rememberTextFieldState()
    val textFieldInput = textFieldState.text.toString()

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier.padding(10.dp)
            ) {
                Row() {
                    Text("Create a new recipe", modifier=Modifier.weight(1f),style = StyleUtils.smallTitle)

                    Button(onClick = {onDismiss()}) {
                        Text("Cancel")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    state = textFieldState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    placeholder = { Text("Enter New Recipe Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        onDismiss()
                        onCreate(textFieldInput) },
                    modifier = Modifier.fillMaxWidth()) {
                    Text("Create Recipe")
                }
            }
        }
    }
}

//Cookbook page gets a SearchUtils and the name of the cookbook
@Composable
fun CookbookPage(searchUtils : SearchUtils,
                 name: String?,
                 modifier: Modifier = Modifier,
                 navController: NavController,
                 setTopBarActions: (@Composable RowScope.() -> Unit) -> Unit){

    var list: List<Pair<String, AppRecipe>>
    var title = ""
    if(name == null){
        title = SearchUtils.homeName
        list = searchUtils.getCookBooksList()
        LaunchedEffect(title){
            setTopBarActions({
                IconButton(onClick = { /* do something */ }) {
                    Icon(painter = painterResource(R.drawable.outline_more_vert_24), contentDescription = "More")
                }
            })
        }
    }
    else{
        title = name
        list = searchUtils.getBookRecipesSorted(title,{it.dateChanged ?: ""})
        LaunchedEffect(title) {
            setTopBarActions({
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_24),
                        contentDescription = "Add"
                    )
                }
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        painter = painterResource(R.drawable.outline_more_vert_24),
                        contentDescription = "More"
                    )
                }
            })
        }
    }
    CookbookPageLayout(searchUtils,title,list,name,modifier,navController)

}

@Composable
fun AddRecipeCard(onClick: () -> Unit){
    Column(
        modifier = Modifier
            .background(Color.White)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Card(
            Modifier.aspectRatio(1.2f),
            elevation = CardDefaults.cardElevation(5.dp),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(1.dp, Color.Black),
        ) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(painter = painterResource(R.drawable.baseline_add_24), contentDescription = "add new recipe")
            }
        }
        Text("Add New Recipe",Modifier.padding(2.dp),style = StyleUtils.cardText, maxLines = 3, overflow = TextOverflow.Ellipsis)
    }
}

//Image + Recipe Name for the cookbook page
//Or image + cookbook name for the all books page
@Composable
fun ImageCard (recipeName: String,
               cardTxt : String,
               onClick: () -> Unit){
    Column(
        modifier = Modifier
            .background(Color.White)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
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
        Text(cardTxt,Modifier.padding(2.dp),style = StyleUtils.cardText, maxLines = 3, overflow = TextOverflow.Ellipsis)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
//@Preview(device = Devices.PIXEL_TABLET, showSystemUi = true)
@Composable
fun CookBookPagePreview() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    CreateRecipeDialog({},{})

    /*
    RecipeAppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    title = {Text("Hey")},
                    navigationIcon = {
                        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { /* do something */ }) {
                                Icon(painter = painterResource(R.drawable.baseline_arrow_back_ios_24), contentDescription = "")
                            }
                            Text("Home",style= StyleUtils.backButtonTitle)
                        }

                    },
                    actions = {},
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            var list = mutableListOf<Pair<String, AppRecipe>>()
            list.add("Sweets" to SearchUtils.exampleRec())
            list.add("Sweet" to SearchUtils.exampleRec())
            list.add("Swes" to SearchUtils.exampleRec())
            CookbookPageLayout(SearchUtils(),"Sweets & Desserts", list,"Name", Modifier.padding(innerPadding), navController = rememberNavController())

            //RecipePageLayout(SearchUtils.exampleRec(),Modifier.padding(innerPadding),navController = rememberNavController(),"Back")
        }
    }
    */
}






