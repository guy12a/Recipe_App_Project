package com.example.recipeapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    //https://material-foundation.github.io/material-theme-builder/
    //primary = Purple40,
    primary = Primary1, //most important, positive actions; selected highlights; active states
    onPrimary = onPrimary1, //text color on primary background

    primaryContainer = PrimaryCon1, //Toned version of primary for larger surfaces, Tonal cards/sections, brand-forward areas that shouldn’t shout.
    onPrimaryContainer = onPrimaryCon1,

    //secondary = PurpleGrey40,
    secondary = Seconadary1,// Supporting accent. Less prominent actions, filters, secondary highlights
    onSecondary = onSecondary1,

    secondaryContainer = SecondCon1, //Toned secondary color. Where you want visual grouping without full-strength brand.
    onSecondaryContainer = onSecondary1,

    //tertiary = Pink40,
    tertiary = Tertiary1,// Additional accent (often used for content categories or visualizations). Tags, charts, avatars, decorative highlights
    onTertiary =onTertiary1,

    tertiaryContainer = TerCon1,
    onTertiaryContainer = onTertiary1

    //tertiaryContainer


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun RecipeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}