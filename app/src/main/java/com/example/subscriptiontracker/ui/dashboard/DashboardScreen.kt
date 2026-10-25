package com.example.subscriptiontracker.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.subscriptiontracker.ui.AppViewModelProvider
import com.example.subscriptiontracker.ui.components.SubscriptionAvatar
import com.example.subscriptiontracker.ui.components.SubscriptionRow
import com.example.subscriptiontracker.ui.dueLabel
import com.example.subscriptiontracker.ui.money
import com.example.subscriptiontracker.ui.formatShortDate
import com.example.subscriptiontracker.ui.theme.Amber
import com.example.subscriptiontracker.ui.theme.BrandGradient
import com.example.subscriptiontracker.ui.theme.Coral
import java.time.LocalTime

@Composable
fun DashboardScreen(
    onAddClick: () -> Unit,
    onSubscriptionClick: (Long) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (state.isLoading) return // nothing to show for the split second before data arrives

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            // bottom padding so the last row isn't hidden behind the + button
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Header(firstName = state.firstName, onProfileClick = onProfileClick) }

            item {
                Spacer(Modifier.height(8.dp))
                SpendCard(
                    monthlyTotal = state.monthlyTotal,
                    yearlyTotal = state.yearlyTotal,
                    activeCount = state.activeCount
                )
            }

            if (state.all.isEmpty()) {
                item { EmptyCard(onAddClick) }
            } else {
                item { SectionTitle("Due this week", trailing = "${state.dueThisWeek.size}") }
                item {
                    if (state.dueThisWeek.isEmpty()) {
                        NothingDueCard()
                    } else {
                        // A sideways-scrolling row inside the up-down list
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(state.dueThisWeek, key = { it.subscription.id }) { payment ->
                                UpcomingCard(payment, onClick = { onSubscriptionClick(payment.subscription.id) })
                            }
                        }
                    }
                }

                item { SectionTitle("All subscriptions", trailing = "${state.all.size}") }
                items(state.all, key = { it.subscription.id }) { payment ->
                    val dueSoon = payment.daysLeft <= UPCOMING_DAYS
                    SubscriptionRow(
                        subscription = payment.subscription,
                        onClick = { onSubscriptionClick(payment.subscription.id) },
                        subtitle = if (dueSoon) dueLabel(payment.daysLeft)
                        else "${payment.subscription.billingCycle.label} · ${formatShortDate(payment.date)}",
                        subtitleColor = if (dueSoon) dueColor(payment.daysLeft) else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add subscription")
        }
    }
}

// Red when it's 2 days or less, orange otherwise
private fun dueColor(daysLeft: Long): Color = if (daysLeft <= 2) Coral else Amber

@Composable
private fun Header(firstName: String, onProfileClick: () -> Unit) {
    // remember: work out the greeting once, not on every redraw
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(greeting, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = if (firstName.isBlank()) "Welcome 👋" else "$firstName 👋",
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .clickable(onClick = onProfileClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = firstName.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SpendCard(monthlyTotal: Double, yearlyTotal: Double, activeCount: Int) {
    // The number counts up/down smoothly when it changes
    val animatedMonthly by animateFloatAsState(
        targetValue = monthlyTotal.toFloat(),
        animationSpec = tween(durationMillis = 800),
        label = "monthlyTotal"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(BrandGradient)
    ) {
        // Two see-through circles in the corner, just for decoration
        Box(
            Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Box(
            Modifier
                .size(110.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = 50.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )

        Column(Modifier.padding(24.dp)) {
            Text("Monthly spend", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Text(money(animatedMonthly.toDouble()), color = Color.White, style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.height(20.dp))
            Row {
                CardStat(label = "Per year", value = money(yearlyTotal), modifier = Modifier.weight(1f))
                CardStat(label = "Active", value = "$activeCount", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CardStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
        Text(value, color = Color.White, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun SectionTitle(title: String, trailing: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        Text(trailing, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun UpcomingCard(payment: UpcomingPayment, onClick: () -> Unit) {
    val sub = payment.subscription
    val color = dueColor(payment.daysLeft)
    Column(
        modifier = Modifier
            .width(156.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        SubscriptionAvatar(sub.name, Color(sub.colorArgb), size = 40.dp)
        Spacer(Modifier.height(14.dp))
        Text(sub.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(money(sub.price), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Text(
            text = dueLabel(payment.daysLeft),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(color.copy(alpha = 0.14f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun NothingDueCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🎉", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(12.dp))
        Text("Nothing due in the next $UPCOMING_DAYS days", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun EmptyCard(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No subscriptions yet", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))
        Text(
            "Add Netflix, Spotify, your gym, or anything you pay for regularly",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onAddClick) {
            Icon(Icons.Rounded.Add, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Add your first one")
        }
    }
}
