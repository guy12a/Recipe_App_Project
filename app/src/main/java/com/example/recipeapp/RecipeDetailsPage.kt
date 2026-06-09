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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.recipeapp.ui.theme.RecipeAppTheme
import com.gowtham.ratingbar.RatingBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

//https://github.com/a914-gowtham/compose-ratingbar


/*Add editing options, editing Json file:
    Edit Title
    Edit Stars
    Edit Tags
    Edit Ingredients
    Edit Instructions
* */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipePageLayout(searchUtils : SearchUtils,
                     originalRecipe : AppRecipe,
                     modifier: Modifier = Modifier,
                     navController: NavController,
                     from:String)
{
    var openTagSheet by remember { mutableStateOf (false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    //creates a viewmodel, that works as a safe
    val viewModel: RecipeViewModel = viewModel(
        factory = RecipeViewModelFactory(searchUtils)
    )
    LaunchedEffect(originalRecipe.id) {
        viewModel.setRecipe(originalRecipe)
    }
    val currentRecipe by viewModel.recipe.collectAsState()
    val recipe = currentRecipe ?: return

    Column(
        modifier.fillMaxSize().
        padding(10.dp).
        verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp))
    {
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
            Card(shape = RoundedCornerShape(15.dp), modifier = Modifier.clickable(onClick = {openTagSheet = true})){
                Text("+ Add Tag",Modifier.padding(9.dp,4.dp),fontSize = 18.sp)
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

        if(recipe.stages.isEmpty()){
            StageCard(RecipeStage(""),true)
        }
        else {
            var onlyStage = false
            if(recipe.stages.size==1) onlyStage=true

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for(stage in recipe.stages){
                    StageCard(stage,onlyStage)
                }
                AddStageButtonCard()
            }
        }

        if(openTagSheet){
            ModalBottomSheet(
                onDismissRequest = { openTagSheet = false },
                sheetState = sheetState
            ){
                val context = LocalContext.current
                BottomTagSheet(recipe,searchUtils.getTagsWithout(recipe),{tag->viewModel.addTag(context,tag)},{openTagSheet=false})
            }
        }
    }
}

@Composable
fun StageCard(stage: RecipeStage, onlyStage: Boolean){
    //controls if the cards are expanded by default or not
    var expanded by remember { mutableStateOf (false) }
    Card(Modifier.fillMaxWidth()
        .clickable(onClick = {expanded = !expanded}),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp))
    {
        if(onlyStage){
            ExpandedStageContent(stage,true)
        }
        else if(expanded){
            ExpandedStageContent(stage,false)
        }
        else{
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp, horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stage.title, modifier=Modifier.weight(1f),style = StyleUtils.smallTitle)
                Icon(painter = painterResource(R.drawable.outline_arrow_drop_down_24), contentDescription = "expand")
            }

        }
    }
}

@Composable
fun ExpandedStageContent(stage: RecipeStage, onlyStage: Boolean){
    Column(Modifier.fillMaxWidth().padding(10.dp)) {
        if(!onlyStage){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stage.title, modifier=Modifier.weight(1f),style = StyleUtils.smallTitle)
                IconButton(onClick = { /* do something */ }) {
                    Icon(painter = painterResource(R.drawable.baseline_edit_24), contentDescription = "Edit Stage")
                }
                Icon(painter = painterResource(R.drawable.baseline_arrow_drop_up_24), contentDescription = "expand")
            }
        }
        //Ingredients
        Text("Ingredients",style = StyleUtils.smallTitle)
        Text(stage.getIngredAsText(), style = StyleUtils.regularText)

        Spacer(Modifier.height(25.dp))

        //Instructions
        Text("Instructions", style = StyleUtils.smallTitle)
        Text(stage.getInstructAsText(), style = StyleUtils.regularText)
    }
}

@Composable
fun AddStageButtonCard(){
    Card(Modifier.fillMaxWidth()
        .clickable(onClick = {/* do something */}),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp))
    {
        Row(Modifier.fillMaxWidth().padding(vertical = 5.dp, horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text("Add Stage", modifier=Modifier,style = StyleUtils.smallTitle)
            Icon(painter = painterResource(R.drawable.baseline_add_24), contentDescription = "expand")
        }
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
    RecipePageLayout(searchUtils,recipe,modifier,navController,from)
}


@Composable
fun BottomTagSheet(
    recipe: AppRecipe,
    tags:List<String>,
    onTagAdded: (String) -> Unit,
    onDismiss: () -> Unit
){
    val textFieldState = rememberTextFieldState()
    val query = textFieldState.text.toString()

    val filteredTags = tags.filter {
        it.contains(query, ignoreCase = true)
    }

    val showCreateTag = query.isNotEmpty() &&
            tags.none { it.equals(query, true) } &&
            recipe.tags.none { it.equals(query, true) }

    Column(
        modifier = Modifier.fillMaxSize().
        padding(10.dp).
        verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp))
    {
        Text("Add & Remove Tags", style = StyleUtils.bigTitle)

        OutlinedTextField(
            state = textFieldState,
            lineLimits = TextFieldLineLimits.SingleLine,
            placeholder = { Text("Add or search tag") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(10.dp))
        Text("Recipe Tags",modifier = Modifier)
        FlowRow(modifier= Modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            for (tag in recipe.tags){
                Card(shape = RoundedCornerShape(15.dp)){
                    Text(tag,Modifier.padding(9.dp,4.dp),fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        Text("Add tags",modifier = Modifier)
        FlowRow(modifier= Modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            for (tag in filteredTags){
                Card(shape = RoundedCornerShape(15.dp),modifier = Modifier.clickable(onClick = {onTagAdded(tag)
                                                                                                            onDismiss()})){
                    Text(tag,Modifier.padding(9.dp,4.dp),fontSize = 18.sp)
                }
            }

            if(showCreateTag){
                Card(shape = RoundedCornerShape(15.dp),modifier = Modifier.clickable(onClick = {onTagAdded(query)
                                                                                                                onDismiss()})){
                    Text("Add \"$query\"",Modifier.padding(9.dp,4.dp),fontSize = 18.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
//@Preview(device = Devices.PIXEL_TABLET, showSystemUi = true)
@Composable
fun RecipeDetailsPreview() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    RecipeAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
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
                                Icon(painter = painterResource(R.drawable.baseline_arrow_back_ios_24), contentDescription = "back")
                            }
                            Text("Home",style= StyleUtils.backButtonTitle)
                        }

                    },
                    actions = {},
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            val tags = mutableListOf<String>()
            tags.add("dessert")
            tags.add("asian")
            tags.add("sweet")
            tags.add("chocolate")
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            Text("",modifier = Modifier.padding(innerPadding))
            //ModalBottomSheet(onDismissRequest = { }, sheetState = sheetState)
            //{
                BottomTagSheet(SearchUtils.exampleRec(),tags,{tag -> tags.add(tag)},{})
            //}

            //RecipePageLayout(SearchUtils(),SearchUtils.exampleRec(),Modifier.padding(innerPadding),navController = rememberNavController(),"Back")
        }
    }
}




