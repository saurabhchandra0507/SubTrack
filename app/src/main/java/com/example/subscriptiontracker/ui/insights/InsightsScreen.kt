package com.example.subscriptiontracker.ui.insights

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.subscriptiontracker.data.monthlyCost
import com.example.subscriptiontracker.ui.AppViewModelProvider
import com.example.subscriptiontracker.ui.components.PlaceholderScreen
import com.example.subscriptiontracker.ui.components.SubscriptionRow
import com.example.subscriptiontracker.ui.components.chartColor
import com.example.subscriptiontracker.ui.components.icon
import com.example.subscriptiontracker.ui.money
import kotlin.math.roundToInt

@Composable
fun InsightsScreen(
    onSubscriptionClick: (Long) -> Unit,
    viewModel: InsightsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (state.isLoading) return

    if (state.categories.isEmpty()) {
        PlaceholderScreen(
            icon = Icons.Rounded.PieChart,
            title = "No insights yet",
            subtitle = "Add a few subscriptions and you'll see where your money goes"
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Insights", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Where your money goes each month",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item { CategoryCard(state) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(label = "Per year", value = money(state.yearlyTotal), modifier = Modifier.weight(1f))
                StatTile(label = "Average per sub", value = money(state.averagePerSubscription) + "/mo", modifier = Modifier.weight(1f))
            }
        }

        item {
            Text("Most expensive", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
        }
        itemsIndexed(state.mostExpensive, key = { _, sub -> sub.id }) { index, sub ->
            SubscriptionRow(
                subscription = sub,
                onClick = { onSubscriptionClick(sub.id) },
                subtitle = "#${index + 1} · about ${money(sub.monthlyCost)} a month",
            )
        }
    }
}

// Big total + one bar split by category + a legend with exact numbers
@Composable
private fun CategoryCard(state: InsightsUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(20.dp)
    ) {
        Text("Total per month", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(money(state.monthlyTotal), style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(16.dp))
        CategoryBar(state.categories)
        Spacer(Modifier.height(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            state.categories.forEach { LegendRow(it) }
        }
    }
}

// One long bar, split into coloured pieces sized by each category's share
@Composable
private fun CategoryBar(categories: List<CategorySpend>) {
    // Grows from left to right when the screen opens
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { progress.animateTo(1f, tween(durationMillis = 700)) }

    // Colours must be read here: chartColor() is @Composable and Canvas drawing isn't
    val colors = categories.map { it.category.chartColor() }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(4.dp))
    ) {
        val gap = 2.dp.toPx() // small space between pieces so neighbours don't blend
        val fullWidth = size.width * progress.value
        var x = 0f
        categories.forEachIndexed { index, item ->
            val width = fullWidth * item.share
            val drawnWidth = if (index == categories.lastIndex) width else width - gap
            drawRect(
                color = colors[index],
                topLeft = Offset(x, 0f),
                size = Size(drawnWidth.coerceAtLeast(0f), size.height)
            )
            x += width
        }
    }
}

@Composable
private fun LegendRow(item: CategorySpend) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(item.category.chartColor())
        )
        Spacer(Modifier.width(10.dp))
        Icon(
            item.category.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(item.category.label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(money(item.monthly), style = MaterialTheme.typography.titleSmall)
        Text(
            "${(item.share * 100).roundToInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(44.dp)
        )
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}
