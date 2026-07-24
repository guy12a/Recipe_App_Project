package com.example.recipeapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.recipeapp.ui.theme.RecipeAppTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //This sections loads the umami recipes from scratch
        //and saves them in app recipe format
        // figure out how to only load umami ones upon installation, or merge

        /*
        var recipes = getUmamiAsApp(this)

        for(recipe in recipes){
            saveRecipe(this,recipe)
        }
         */




        val searchUtils = SearchUtils()
        searchUtils.loadRecipes(this)

        enableEdgeToEdge()
        setContent {
            //forces left to right
            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Ltr
            ) {
                MainStructure(searchUtils)
            }
        }
    }
}

//Entrance for the whole composable structure
@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainStructure(searchUtils :SearchUtils){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var topBarActions by remember {
        mutableStateOf<@Composable RowScope.() -> Unit>({})
    }



    RecipeAppTheme (
        darkTheme = false
    ){
        val navController = rememberNavController()
        Scaffold(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                //current backStack - current screen. There is also previous!
                val backStackEntry by navController.currentBackStackEntryAsState()

                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    title = {},
                    navigationIcon = {
                        if (backStackEntry?.destination?.hasRoute<MainPageNav>() != true){
                            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(painter = painterResource(R.drawable.baseline_arrow_back_ios_24), contentDescription = "back")
                                }
                                BackText(backStackEntry)
                            }
                        }
                    },
                    actions = {
                        topBarActions()
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = MainPageNav,
            ) {
                composable<MainPageNav>{ backStackEntry -> val args = backStackEntry.toRoute<CookbookPageNav>()
                    CookbookPage(
                        searchUtils,
                        name = null,
                        Modifier.padding(innerPadding),
                        navController = navController,
                        setTopBarActions = { actions ->
                            topBarActions = actions
                        }
                    )
                }
                composable<CookbookPageNav>{ backStackEntry -> val args = backStackEntry.toRoute<CookbookPageNav>()
                    CookbookPage(searchUtils,
                        name = args.cookbookName,
                        Modifier.padding(innerPadding),
                        navController = navController,
                        setTopBarActions = { actions ->
                            topBarActions = actions
                        })
                }
                composable<RecipePageNav> { backStackEntry -> val args = backStackEntry.toRoute<RecipePageNav>()
                    RecipePage(
                        searchUtils,
                        recipeId = args.recipeId,
                        Modifier.padding(innerPadding),
                        navController = navController,
                        from = args.from,
                        setTopBarActions = { actions ->
                            topBarActions = actions
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BackText(entry: NavBackStackEntry?) {
    val destination = entry?.destination
    if(destination?.hasRoute<CookbookPageNav>() == true){
        Text("Home",style= StyleUtils.backButtonTitle)
    }
    else if(destination?.hasRoute<RecipePageNav>() == true){
        Text(entry.toRoute<RecipePageNav>().from,style= StyleUtils.backButtonTitle)
    }
}

/*
@Composable
fun TopAppBarActions(entry: NavBackStackEntry?) {
    val destination = entry?.destination
    if(destination?.hasRoute<MainPageNav>() == true){
        IconButton(onClick = { /* do something */ }) {
            Icon(painter = painterResource(R.drawable.outline_more_vert_24), contentDescription = "More")
        }
    }
    else if(destination?.hasRoute<CookbookPageNav>() == true){
        IconButton(onClick = { /* do something */ }) {
            Icon(painter = painterResource(R.drawable.baseline_add_24), contentDescription = "Add")
        }
        IconButton(onClick = { /* do something */ }) {
            Icon(painter = painterResource(R.drawable.outline_more_vert_24), contentDescription = "More")
        }
    }
    else if(destination?.hasRoute<RecipePageNav>() == true){
        IconButton(onClick = { /* do something */ }) {
            Icon(painter = painterResource(R.drawable.baseline_edit_24), contentDescription = "Edit")
        }
        IconButton(onClick = { /* do something */ }) {
            Icon(painter = painterResource(R.drawable.outline_more_vert_24), contentDescription = "More")
        }
    }
}
 */

@Serializable
object MainPageNav

@Serializable
data class CookbookPageNav(
    val cookbookName: String? = null
)

@Serializable
data class RecipePageNav(
    val recipeId: String,
    val from: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
//@Preview(device = Devices.PIXEL_TABLET, showSystemUi = true)
@Composable
fun AppPreview() {
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
            CookbookPageLayout(SearchUtils(), list,null, Modifier.padding(innerPadding), navController = rememberNavController())

            //RecipePageLayout(SearchUtils.exampleRec(),Modifier.padding(innerPadding),navController = rememberNavController(),"Back")
        }
    }
}
//RecipePage(recipes[0], Modifier.padding(innerPadding))
/*AsyncImage(
    model = "file:///android_asset/pictures/apple_pie.jpg",
    contentDescription = null,
    placeholder = painterResource(R.drawable.placeholder)
)
        //each element is naturally taking enough space only to fit itself. If specifying "fillmaxsize"
        //it will fit more than it needs, based on other elements
        //!!!!!!!Modifier order matters.

                        //Image(painter,contentDesc, contentScale = ContentScale.Crop)


*/




