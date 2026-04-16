package com.example.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.recipeapp.ui.theme.RecipeAppTheme
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
            MainStructure(searchUtils)
        }
    }
}

//Entrance for the whole composable structure
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainStructure(searchUtils :SearchUtils){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    RecipeAppTheme {
        val navController = rememberNavController()
        Scaffold(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(painter = painterResource(R.drawable.baseline_arrow_back_ios_24), contentDescription = "")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(painter = painterResource(R.drawable.baseline_arrow_back_ios_24), contentDescription = "")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = CookbookPageNav(),
            ) {
                composable<CookbookPageNav>{ backStackEntry -> val args = backStackEntry.toRoute<CookbookPageNav>()
                    CookbookPage(searchUtils,
                        name = args.cookbookName,
                        Modifier.padding(innerPadding),
                        navController = navController)
                }
                composable<RecipePageNav> { backStackEntry -> val args = backStackEntry.toRoute<RecipePageNav>()
                    RecipePage(
                        searchUtils,
                        recipeId = args.recipeId,
                        Modifier.padding(innerPadding),
                        navController = navController,
                        from = args.from
                    )
                }
            }
        }
    }
}

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
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(painter = painterResource(R.drawable.baseline_arrow_back_ios_24), contentDescription = "")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(painter = painterResource(R.drawable.outline_more_vert_24), contentDescription = "")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            var map = HashMap<String,AppRecipe>()
            map.put("Sweets", SearchUtils.exampleRec())
            map.put("Sweet", SearchUtils.exampleRec())
            map.put("Swes", SearchUtils.exampleRec())
            CookbookPageLayout(SearchUtils.homeName, map,null, Modifier.padding(innerPadding), navController = rememberNavController())

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




