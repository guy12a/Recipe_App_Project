package com.example.recipeapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.recipeapp.ui.theme.RecipeAppTheme

/* Add options to:
    Edit cookbook name
* */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookbookPageLayout(
    searchUtils : SearchUtils,
    recipes:List<Pair<String, AppRecipe>>,
    name:String?,
    modifier: Modifier = Modifier,
    navController: NavController,
    setTopBarActions: (@Composable RowScope.() -> Unit) -> Unit
){
    val context = LocalContext.current

    var openAddRecipeDialog by remember { mutableStateOf (false) }
    var openAddBookDialog by remember { mutableStateOf (false) }

    //controls the editing of recipe name
    var openBulkAddSheet by remember { mutableStateOf (false) }
    val bulkSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, { newValue ->
        newValue != SheetValue.Hidden
    })

    if(name==null){
        LaunchedEffect(SearchUtils.homeName){
            setTopBarActions({
                IconButton(onClick = { /* do something */ }) {
                    Icon(painter = painterResource(R.drawable.outline_more_vert_24), contentDescription = "More")
                }
            })
        }
    }
    else{
        LaunchedEffect(name) {
            setTopBarActions({
                IconButton(onClick = { openBulkAddSheet=true}) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_24),
                        contentDescription = "bulk add by tag"
                    )
                }
            })
        }
    }

    //val painter = painterResource(R.drawable.placeholder)
    //columns = GridCells.Adaptive(minSize = 128.dp)
    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp))
    {
        item (span = { GridItemSpan(maxCurrentLineSpan) }){
            var title = name
            if(name!=null) title+= " - " + recipes.size + " recipes"
            else title = SearchUtils.homeName
            Text(title, style = StyleUtils.bigTitle)
        }
        //if home page
        if(name==null){
            item{
                AddRecipeOrBookCard(name,{openAddBookDialog=true}, "add new book","Add New Cookbook")
            }
            items(recipes) { (cardName, recipe) ->
                RecipeCard(
                    recipe.name,
                    cardName,
                    {
                        navController.navigate(
                            CookbookPageNav(cardName)
                        )
                    }
                )
            }
        }
        //if any cookbook besides all recipes
        else if(name!= SearchUtils.allRecipesName){
            item{
                AddRecipeOrBookCard(name,{openAddRecipeDialog=true},"add new recipe","Add New Recipe")
            }
            if(recipes.isEmpty()){
                item{
                    AddRecipeOrBookCard(name,
                        { openBulkAddSheet = true },
                        "bulk add by tag",
                        "Add Many Recipes by Tag"
                    )
                }
            }
            items(recipes) { (cardName, recipe) ->
                RecipeCard(recipe.name,
                    cardName,
                    {navController.navigate(
                        RecipePageNav(recipe.id,name)
                    )})
            }
        }
        //all recipes book
        else{
            items(recipes) { (cardName, recipe) ->
                RecipeCard(recipe.name,
                    cardName,
                    {navController.navigate(
                        RecipePageNav(recipe.id,name)
                    )})
            }
        }
    }

    if(openAddRecipeDialog && name!=null){
        CreateRecipeOrBookDialog(
            name,
            { newRecipeName -> navController.navigate(
                RecipePageNav(searchUtils.createNewRecipe(context,name,newRecipeName),name)) },
            {openAddRecipeDialog=false})
    }

    if(openAddBookDialog){
        CreateRecipeOrBookDialog(
            null,
            {newBookName ->
                searchUtils.createNewBook(newBookName)
                navController.navigate(CookbookPageNav(newBookName)) },
            {openAddBookDialog=false}
        )
    }

    if(openBulkAddSheet && name!=null){
        ModalBottomSheet(
            onDismissRequest = { openBulkAddSheet = false },
            sheetState = bulkSheetState
        ){
            BulkAddTagSheet(searchUtils.getTags(),
                { openBulkAddSheet=false },
                { addedTags -> searchUtils.addToBookByTags(name,addedTags,context) })
        }
    }
}

@Composable
fun CreateRecipeOrBookDialog(
    bookName: String?,
    onCreate: (String) -> Unit,
    onDismiss: () -> Unit
){
    val textFieldState = rememberTextFieldState()
    val textFieldInput = textFieldState.text.toString()

    var text1 = "Create a New Recipe"
    var text2 = "Enter the new recipe's name"
    var text3 = "Create Recipe"

    if(bookName == null){
        text1 = "Create a Cookbook"
        text2 = "Enter the new cookbook's name"
        text3 = "Create Cookbook"
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier.padding(10.dp)
            ) {
                Row() {
                    Text(text1, modifier=Modifier.weight(1f),style = StyleUtils.smallTitle)

                    Button(onClick = {onDismiss()}) {
                        Text("Cancel")
                    }
                }


                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    state = textFieldState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    placeholder = { Text(text2) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        onDismiss()
                        onCreate(textFieldInput) },
                    modifier = Modifier.fillMaxWidth()) {
                    Text(text3)
                }
            }
        }
    }
}

@Composable
fun BulkAddTagSheet(
    tags: List<String>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
){
    val textFieldState = rememberTextFieldState()
    val query = textFieldState.text.toString()

    var addedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var remainingTags by remember { mutableStateOf<List<String>>(tags.filter {
        it.contains(query, ignoreCase = true)
    }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp))
    {
        Row() {
            Text("Add Recipes by Tag", modifier = Modifier.weight(1f), style = StyleUtils.bigTitle)
            Button(onClick = {
                onSave(addedTags)
                onDismiss()
            }) {
                Text("Save")
            }
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }


        OutlinedTextField(
            state = textFieldState,
            lineLimits = TextFieldLineLimits.SingleLine,
            placeholder = { Text("Search tags to add") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text("Tags added to book",modifier = Modifier)
        FlowRow(modifier= Modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            for (tag in addedTags){
                Card(shape = RoundedCornerShape(15.dp), modifier = Modifier.clickable(onClick = {
                    addedTags -= tag
                    remainingTags += tag
                })){
                    Text(tag,Modifier.padding(9.dp,4.dp),fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        Text("All remaining tags",modifier = Modifier)
        FlowRow(modifier= Modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            for (tag in remainingTags){
                Card(shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.clickable(onClick = {
                        remainingTags -= tag
                        addedTags += tag
                    }))
                {
                    Text(tag,Modifier.padding(9.dp,4.dp),fontSize = 18.sp)
                }
            }
        }
    }
}

//========================== Recipes and Book Elements ==========================

@Composable
fun AddRecipeOrBookCard(bookName : String?, onClick: () -> Unit, buttonDesc : String, buttonBottomText: String){
    Column(
        modifier = Modifier
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
                Icon(painter = painterResource(R.drawable.baseline_add_24), contentDescription = buttonDesc)
            }
        }
        Text(buttonBottomText,Modifier.padding(2.dp),style = StyleUtils.cardText, maxLines = 3, overflow = TextOverflow.Ellipsis)
    }
}

//Image + Recipe Name for the cookbook page
//Or image + cookbook name for the all books page
@Composable
fun RecipeCard (recipeName: String,
                cardTxt : String,
                onClick: () -> Unit){
    Column(
        modifier = Modifier
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

//========================== General ==========================

//Main entry point to cookbook or homepage
@Composable
fun CookbookPage(searchUtils : SearchUtils,
                 name: String?,
                 modifier: Modifier = Modifier,
                 navController: NavController,
                 setTopBarActions: (@Composable RowScope.() -> Unit) -> Unit){

    var list: List<Pair<String, AppRecipe>>
    if(name == null)
        list = searchUtils.getCookBooksList()
    else
        list = searchUtils.getBookRecipesSorted(name,{it.dateChanged ?: ""})

    CookbookPageLayout(searchUtils,list,name,modifier,navController,setTopBarActions)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
//@Preview(device = Devices.PIXEL_TABLET, showSystemUi = true)
@Composable
fun CookBookPagePreview() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    //CreateRecipeDialog({},{})
    BulkAddTagSheet(listOf("Chocolate","Chicken"),{},{})

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
            CookbookPageLayout(SearchUtils(), list,"Hey", Modifier.padding(innerPadding), navController = rememberNavController())

            //RecipePageLayout(SearchUtils.exampleRec(),Modifier.padding(innerPadding),navController = rememberNavController(),"Back")
        }
    }
    */

}






