package com.example.subscriptiontracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.subscriptiontracker.data.Category

// Icons are a UI thing, so they live here instead of in the data layer
val Category.icon: ImageVector
    get() = when (this) {
        Category.ENTERTAINMENT -> Icons.Rounded.Movie
        Category.MUSIC -> Icons.Rounded.Headphones
        Category.PRODUCTIVITY -> Icons.Rounded.Work
        Category.CLOUD -> Icons.Rounded.Cloud
        Category.FITNESS -> Icons.Rounded.FitnessCenter
        Category.NEWS -> Icons.AutoMirrored.Rounded.Article
        Category.GAMING -> Icons.Rounded.SportsEsports
        Category.EDUCATION -> Icons.Rounded.School
        Category.OTHER -> Icons.Rounded.Category
    }

// Chart colour for each category. Each category always keeps the same colour, so
// "Music" is orange on every screen. The palette is chosen so neighbouring colours
// stay tellable apart for colour-blind users; dark mode uses slightly adjusted shades.
@Composable
fun Category.chartColor(): Color {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val (light, darkShade) = when (this) {
        Category.ENTERTAINMENT -> 0xFF2A78D6 to 0xFF3987E5 // blue
        Category.MUSIC -> 0xFFEB6834 to 0xFFD95926         // orange
        Category.PRODUCTIVITY -> 0xFF1BAF7A to 0xFF199E70  // aqua
        Category.CLOUD -> 0xFFEDA100 to 0xFFC98500         // yellow
        Category.FITNESS -> 0xFFE87BA4 to 0xFFD55181       // magenta
        Category.NEWS -> 0xFF008300 to 0xFF008300          // green
        Category.GAMING -> 0xFF6250D6 to 0xFF9085E9        // violet
        Category.EDUCATION -> 0xFFE34948 to 0xFFE66767     // red
        Category.OTHER -> 0xFF8E8EA8 to 0xFF8E8EA8         // grey
    }
    return Color(if (dark) darkShade else light)
}
