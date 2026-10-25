package com.example.subscriptiontracker.ui.addedit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.subscriptiontracker.data.BillingCycle
import com.example.subscriptiontracker.data.Category
import com.example.subscriptiontracker.ui.AppViewModelProvider
import com.example.subscriptiontracker.ui.components.AppTextField
import com.example.subscriptiontracker.ui.components.PickerField
import com.example.subscriptiontracker.ui.components.PrimaryButton
import com.example.subscriptiontracker.ui.components.SubscriptionAvatar
import com.example.subscriptiontracker.ui.components.icon
import com.example.subscriptiontracker.ui.formatDate
import com.example.subscriptiontracker.ui.money
import com.example.subscriptiontracker.ui.theme.SubscriptionColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    onBack: () -> Unit,
    viewModel: AddEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Small on/off switches that only this screen cares about
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.isDone) {
        if (state.isDone) onBack()
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(if (state.isEditing) "Edit subscription" else "Add subscription") },
            windowInsets = WindowInsets(0), // the outer Scaffold already adds status bar space
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                if (state.isEditing) {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            PreviewCard(state)
            Spacer(Modifier.height(24.dp))

            AppTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = "Name (e.g. Netflix)",
                leadingIcon = Icons.Rounded.Subscriptions,
                error = state.nameError,
            )
            Spacer(Modifier.height(12.dp))
            AppTextField(
                value = state.price,
                onValueChange = viewModel::onPriceChange,
                label = "Price",
                leadingIcon = Icons.Rounded.Payments,
                error = state.priceError,
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            )

            SectionLabel("Billing cycle")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BillingCycle.entries.forEach { cycle ->
                    AppChip(
                        text = cycle.label,
                        selected = state.billingCycle == cycle,
                        onClick = { viewModel.onCycleChange(cycle) }
                    )
                }
            }

            SectionLabel("Next payment")
            PickerField(
                label = "Date",
                value = formatDate(state.nextPaymentDate),
                icon = Icons.Rounded.CalendarMonth,
                onClick = { showDatePicker = true }
            )

            SectionLabel("Category")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Category.entries.forEach { category ->
                    AppChip(
                        text = category.label,
                        selected = state.category == category,
                        onClick = { viewModel.onCategoryChange(category) },
                        leading = {
                            Icon(category.icon, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                }
            }

            SectionLabel("Colour")
            ColorPicker(selectedArgb = state.colorArgb, onColorClick = viewModel::onColorChange)

            SectionLabel("Notes (optional)")
            AppTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = "e.g. Family plan, shared with Rahul",
                leadingIcon = Icons.AutoMirrored.Rounded.Notes,
                singleLine = false,
                minLines = 2,
                imeAction = ImeAction.Default,
            )

            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                text = if (state.isEditing) "Save changes" else "Add subscription",
                onClick = viewModel::save,
                isLoading = state.isSaving
            )
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showDatePicker) {
        // The Material date picker works with UTC milliseconds, so we convert both ways
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = state.nextPaymentDate.toUtcMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { viewModel.onDateChange(it.toLocalDate()) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete ${state.name}?") },
            text = { Text("This subscription will be removed. You can't undo this.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete()
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// Live preview at the top, so you see how it will look in the list while typing
@Composable
private fun PreviewCard(state: AddEditUiState) {
    val price = state.price.toDoubleOrNull()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubscriptionAvatar(name = state.name, color = Color(state.colorArgb), size = 56.dp)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = state.name.ifBlank { "Subscription name" },
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = state.category.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = (if (price != null) money(price) else money(0.0)) + state.billingCycle.shortLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Spacer(Modifier.height(20.dp))
    Text(text, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun AppChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    leading: (@Composable () -> Unit)? = null,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        leadingIcon = leading,
        shape = MaterialTheme.shapes.small,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            selectedLabelColor = MaterialTheme.colorScheme.primary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
        )
    )
}

@Composable
private fun ColorPicker(selectedArgb: Int, onColorClick: (Int) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SubscriptionColors.forEach { color ->
            val argb = color.toArgb()
            val selected = argb == selectedArgb
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .then(
                        if (selected) Modifier.border(3.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                        else Modifier
                    )
                    .clickable { onColorClick(argb) },
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(Icons.Rounded.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
