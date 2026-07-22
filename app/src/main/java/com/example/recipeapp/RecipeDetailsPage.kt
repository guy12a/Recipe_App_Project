package com.example.recipeapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.gowtham.ratingbar.RatingBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
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
                     from:String,
                     setTopBarActions: (@Composable RowScope.() -> Unit) -> Unit)
{
    //creates a viewmodel, that works as a safe
    val viewModel: RecipeViewModel = viewModel(
        factory = RecipeViewModelFactory(searchUtils)
    )
    LaunchedEffect(originalRecipe.id) {
        viewModel.setRecipe(originalRecipe)
    }
    val currentRecipe by viewModel.recipe.collectAsState()
    val recipe = currentRecipe ?: return

    val context = LocalContext.current

    //controls the adding of tags using bottom sheet
    var openTagSheet by remember { mutableStateOf (false) }
    val tagSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, { newValue ->
        newValue != SheetValue.Hidden
    })

    //controls which tag has its expanded box opened - for removing tags and the like
    var openTagBox by remember { mutableStateOf<String?>(null) }

    //controls the editing of a stage info
    var editStage by remember { mutableStateOf<RecipeStage?>(null) }
    val editingStageSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,{ newValue ->
        newValue != SheetValue.Hidden
    })

    //remove stage alert
    var removedStage by remember { mutableStateOf<RecipeStage?>(null) }

    //controls the editing of recipe name
    var openNameSheet by remember { mutableStateOf (false) }
    val nameSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, { newValue ->
        newValue != SheetValue.Hidden
    })

    //controls if top bar dropdown is open
    var openTopDropDown by remember { mutableStateOf (false) }

    //controls editing recipe book
    var openEditBooksSheet by remember { mutableStateOf (false) }
    val editBooksSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, { newValue ->
        newValue != SheetValue.Hidden
    })


    Column(
        modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp))
    {
        //Recipe Title
        Text(recipe.name, style = StyleUtils.bigTitle)

        //Star Rating
        var rating: Float by remember { mutableStateOf(recipe.rating) }
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Start) {
            RatingBar(value = rating, onValueChange = {rating = it}, onRatingChanged = {viewModel.editRating(context,rating)})
        }

        //Tags Bar
        FlowRow(modifier= Modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            for (tag in recipe.tags){
                TagAndMenu(tag,
                    isExpanded = openTagBox == tag,
                    onOpen = { openTagBox = tag },
                    onDismiss = { openTagBox = null },
                    onTagRemoved = {t -> openTagBox = null
                        viewModel.removeTag(context,t)})
            }
            Card(shape = RoundedCornerShape(15.dp), modifier = Modifier.clickable(onClick = {openTagSheet = true})){
                Text("+ Add Tag",Modifier.padding(9.dp,4.dp),fontSize = 18.sp)
            }
        }

        //Image of Recipe
        Card( Modifier
            .fillMaxWidth()
            .aspectRatio(1.25f)
            .padding(0.dp, 10.dp),
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

        Button({}) {
            Text("Double of Halve")
        }

        if(recipe.stages.isEmpty()){
            StageCard(RecipeStage(""),true,{editStage=it},{})
        }
        else {
            var onlyStage = false
            if(recipe.stages.size==1) onlyStage=true

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for(stage in recipe.stages){
                    StageCard(stage,onlyStage,{editStage=it},{removedStage=it})
                }
                AddStageButtonCard({viewModel.addStage(context)})
            }
        }

        //opening stage editing sheet
        if(editStage!=null){
            ModalBottomSheet(
                onDismissRequest = { editStage = null },
                sheetState = editingStageSheetState
            ){
                EditStageBottomSheet(stage=editStage!!, { stage-> viewModel.editStage(context,stage) },{editStage=null})
            }
        }

        //opening a tag-adding sheet
        if(openTagSheet){
            ModalBottomSheet(
                onDismissRequest = { openTagSheet = false },
                sheetState = tagSheetState
            ){
                BottomTagSheet(recipe,searchUtils.getTagsWithout(recipe),{tag->viewModel.addTag(context,tag.lowercase())},{openTagSheet=false})
            }
        }

        //opens alert regarding deleting a stage
        if(removedStage != null){
            if(removedStage!!.recipeInstruct.isEmpty() && removedStage!!.ingredients.isEmpty()){
                viewModel.removeStage(context, removedStage!!)
                removedStage = null
            }
            else{
                StageRemoveAlert(
                    onDismissRequest = { removedStage = null },
                    onConfirmation = {
                        viewModel.removeStage(context, removedStage!!)
                        removedStage = null
                    }
                )
            }
        }

        LaunchedEffect(recipe.id) {
            setTopBarActions({
                IconButton(onClick = { openNameSheet=true }) {
                    Icon(painter = painterResource(R.drawable.baseline_edit_24), contentDescription = "Edit")
                }
                Box{
                    IconButton(onClick = { openTopDropDown=true }) {
                        Icon(painter = painterResource(R.drawable.outline_more_vert_24), contentDescription = "More")
                    }
                    TopBarDropDown(
                        openTopDropDown,
                        {openTopDropDown=false},
                        {
                            openEditBooksSheet=true
                            openTopDropDown=false
                    })
                }

            })
        }

        if(openNameSheet){
            ModalBottomSheet(
                onDismissRequest = { openNameSheet = false },
                sheetState = nameSheetState
            ){
                EditNameBottomSheet(recipe.name,{newName->viewModel.editName(context,newName)},{openNameSheet=false})
            }
        }

        if(openEditBooksSheet){
            ModalBottomSheet(
                onDismissRequest = { openEditBooksSheet = false },
                sheetState = editBooksSheetState
            ){
                EditCookbooksSheet(recipe,searchUtils.getCookbooksWithout(recipe),{ recipeBook->viewModel.addBook(context,recipeBook)},{recipeBook->viewModel.removeBook(context,recipeBook)},{openEditBooksSheet=false})
            }
        }
    }
}

//========================== Top App Bar ==========================

@Composable
fun EditNameBottomSheet(
    recipeName:String,
    onNameEdited: (String) -> Unit,
    onDismiss: () -> Unit
){
    val nameField = rememberTextFieldState(initialText = recipeName)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp))
    {
        Row() {
            Text("Edit Recipe Name", modifier=Modifier.weight(1f),style = StyleUtils.bigTitle)
            Button(onClick = {
                onNameEdited(nameField.text.toString())
                onDismiss()})
            {
                Text("Save")
            }
            Button(onClick = {onDismiss()}) {
                Text("Cancel")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            state = nameField,
            placeholder = { Text("") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EditCookbooksSheet(
    recipe: AppRecipe,
    cookbooksWithout: List<String>,
    onBookAdded: (String) -> Unit,
    onBookRemoved: (String) -> Unit,
    onDismiss: () -> Unit
){
    Column(
        modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
        .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row() {
            Text("Add & Remove Tags",modifier=Modifier.weight(1f), style = StyleUtils.bigTitle)
            Button(onClick = {onDismiss()}) {
                Text("Exit")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Books the Recipe is in", modifier = Modifier, style = StyleUtils.regularText)
        Text("tap a book to remove the recipe from it", modifier = Modifier, style = StyleUtils.regularText)
        FlowRow(modifier= Modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            for (book in recipe.cookbooks){
                Card(shape = RoundedCornerShape(15.dp),modifier = Modifier.clickable(onClick = {
                    if(recipe.cookbooks.size<=1){

                    }
                    else{
                        onBookRemoved(book)
                    }
                })){
                    Text(book,Modifier.padding(9.dp,4.dp),fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Other Books in App", modifier = Modifier, style = StyleUtils.regularText)
        Text("tap a book to add the recipe to it", modifier = Modifier, style = StyleUtils.regularText)
        FlowRow(modifier= Modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            for (book in cookbooksWithout){
                Card(shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.clickable(onClick = { onBookAdded(book) }))
                {
                    Text(book,Modifier.padding(9.dp,4.dp),fontSize = 18.sp)
                }
            }
        }


    }
}

@Composable
fun TopBarDropDown(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    onEditCookbooks: () -> Unit
){
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { onDismiss() },
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    )
    {
        DropdownMenuItem(
            text = { Text("Add or Remove to Cookbooks") },
            onClick = { onEditCookbooks() }
        )
    }
}



//========================== Stages ==========================

@Composable
fun StageCard(stage: RecipeStage, onlyStage: Boolean, onStageEdited: (updatedStage: RecipeStage) -> Unit,onStageRemove: (removedStage: RecipeStage) -> Unit){
    //controls if the cards are expanded by default or not
    var expanded by remember { mutableStateOf (false) }
    Card(Modifier
        .fillMaxWidth()
        .clickable(onClick = { expanded = !expanded }),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp))
    {
        //if only stage - expanded by default
        if(onlyStage){
            ExpandedStageContent(stage,true,onStageEdited,onStageRemove)
        }
        //if more than one stage, can be expanded
        else if(expanded){
            ExpandedStageContent(stage,false,onStageEdited,onStageRemove)
        }
        //more than one stage, not expanded
        else{
            Row(Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stage.title, modifier=Modifier.weight(1f),style = StyleUtils.smallTitle)
                Icon(painter = painterResource(R.drawable.outline_arrow_drop_down_24), contentDescription = "expand")
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedStageContent(stage: RecipeStage, onlyStage: Boolean,onStageEdited: (updatedStage: RecipeStage) -> Unit, onStageRemove: (removedStage: RecipeStage) -> Unit){
    Column(Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
        if(!onlyStage){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stage.title, modifier=Modifier.weight(1f),style = StyleUtils.smallTitle)
                IconButton(onClick = {onStageRemove(stage)}) {
                    Icon(painter = painterResource(R.drawable.outline_delete_24), contentDescription = "Remove Stage")
                }
                IconButton(onClick = { onStageEdited(stage)}) {
                    Icon(painter = painterResource(R.drawable.baseline_edit_24), contentDescription = "Edit Stage")
                }
                Icon(painter = painterResource(R.drawable.baseline_arrow_drop_up_24), contentDescription = "expand")
            }
            Text("Ingredients",style = StyleUtils.smallTitle)
        }
        else{
            //Ingredients
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ingredients",modifier=Modifier.weight(1f),style = StyleUtils.smallTitle)
                IconButton(onClick = { onStageEdited(stage) }) {
                    Icon(painter = painterResource(R.drawable.baseline_edit_24), contentDescription = "expand")
                }
            }
        }
        Text(stage.getIngredAsText(), style = StyleUtils.regularText)

        Spacer(Modifier.height(25.dp))

        //Instructions
        Text("Instructions", style = StyleUtils.smallTitle)
        Text(stage.getInstructAsText(), style = StyleUtils.regularText)
    }
}

@Composable
fun EditStageBottomSheet(
    stage:RecipeStage,
    onStageEdited: (RecipeStage) -> Unit,
    onDismiss: () -> Unit
){
    val titleField = rememberTextFieldState(initialText = stage.title)
    val ingredField = rememberTextFieldState(initialText = stage.getIngredAsText())
    val instructField = rememberTextFieldState(initialText = stage.getInstructAsText())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp))
    {
        Row() {
            Text("Edit Stage", modifier=Modifier.weight(1f),style = StyleUtils.bigTitle)
            Button(onClick = {onStageEdited(stage.copy(
                title = titleField.text.toString(),
                ingredients = ingredField.text.toString().split("\n"),
                recipeInstruct = instructField.text.toString().split("\n")))
            onDismiss()})
            {
                Text("Save")
            }
            Button(onClick = {onDismiss()}) {
                Text("Cancel")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Edit Title",style = StyleUtils.smallTitle)
        OutlinedTextField(
            state = titleField,
            placeholder = { Text("Add or search tag") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text("Edit Ingredients",style = StyleUtils.smallTitle)
        OutlinedTextField(
            state = ingredField,
            placeholder = { Text("Add or search tag") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text("Edit Instructions",style = StyleUtils.smallTitle)
        OutlinedTextField(
            state = instructField,
            placeholder = { Text("Add or search tag") },
            modifier = Modifier.fillMaxWidth()
        )


    }

}

@Composable
fun AddStageButtonCard(onClick: () -> Unit){
    Card(Modifier
        .fillMaxWidth()
        .clickable(onClick = { onClick() }),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp))
    {
        Row(Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text("Add Stage", modifier=Modifier,style = StyleUtils.smallTitle)
            Icon(painter = painterResource(R.drawable.baseline_add_24), contentDescription = "expand")
        }
    }
}

@Composable
fun StageRemoveAlert(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    ) {
    AlertDialog(
        icon = { Icon(painter = painterResource(R.drawable.outline_delete_24), contentDescription = "Remove Stage")},
        title = { Text(text = "Remove Stage") },
        text = { Text(text = "Are you sure you would like to remove this stage?") },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

//======================= Tags Editing =======================

@Composable
fun TagAndMenu(tag: String,
               isExpanded: Boolean,
               onTagRemoved: (String) -> Unit,
               onOpen: () -> Unit,
               onDismiss: () -> Unit,)
{
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.clickable(onClick = { onOpen() })
    ) {
        Text(tag, Modifier.padding(9.dp, 4.dp), fontSize = 18.sp)
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onDismiss() },
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        )
        {
            DropdownMenuItem(
                text = { Text("Remove Tag") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_delete_24),
                        contentDescription = "expand"
                    )
                },
                onClick = { onTagRemoved(tag) }
            )
        }
    }
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
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
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
                Card(shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.clickable(onClick = {
                        onTagAdded(tag)
                        onDismiss()}))
                {
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

//=============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipePage(searchUtils : SearchUtils,
               recipeId: String,
               modifier: Modifier = Modifier,
               navController: NavController,
               from: String,
               setTopBarActions: (@Composable RowScope.() -> Unit) -> Unit
){
    var recipe = searchUtils.getRecipe(recipeId)
    RecipePageLayout(searchUtils,recipe,modifier,navController,from,setTopBarActions)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
//@Preview(device = Devices.PIXEL_TABLET, showSystemUi = true)
@Composable
fun RecipeDetailsPreview() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    //TagAndMenu("Sweets",true,{tag->},{},{})

    EditCookbooksSheet(SearchUtils.exampleRec(),arrayListOf("One","Two"),{ },{},{})

    /*
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = { }, sheetState = sheetState){
        EditNameBottomSheet(SearchUtils.exampleRec().name,{},{})
    }

     */

    /*
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = { }, sheetState = sheetState){
        EditStageBottomSheet(SearchUtils.exampleRec().stages[0],{},{})
    }
     */

    /*
    RecipeAppTheme {Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),topBar = { TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface,titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),title = {Text("Hey")},navigationIcon = {Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {IconButton(onClick = { /* do something */ }) {Icon(painter = painterResource(R.drawable.baseline_arrow_back_ios_24), contentDescription = "back") }
        Text("Home",style= StyleUtils.backButtonTitle)} }, actions = {
        IconButton(onClick = { /* do something */ }) {
            Icon(painter = painterResource(R.drawable.outline_more_vert_24), contentDescription = "More")
            DropdownMenu(
                expanded = true,
                onDismissRequest = { },
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ){
                DropdownMenuItem(
                    text = { Text("Remove Tag") },
                    onClick = {}
                )
            }
        }
    }, scrollBehavior = scrollBehavior) }) { innerPadding ->
            val tags = mutableListOf<String>()
            tags.add("dessert")
            tags.add("asian")
            tags.add("sweet")
            tags.add("chocolate")
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            //ModalBottomSheet(onDismissRequest = { }, sheetState = sheetState)
            //{
                //BottomTagSheet(SearchUtils.exampleRec(),tags,{tag -> tags.add(tag)},{})
            //}

            RecipePageLayout(SearchUtils(),SearchUtils.exampleRec(),Modifier.padding(innerPadding),navController = rememberNavController(),"Back",{})
        }
    }
     */
}




