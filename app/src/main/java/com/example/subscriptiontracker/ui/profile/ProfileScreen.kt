package com.example.subscriptiontracker.ui.profile

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.subscriptiontracker.data.AppCurrency
import com.example.subscriptiontracker.data.ThemeMode
import com.example.subscriptiontracker.reminders.ReminderNotifications
import com.example.subscriptiontracker.ui.AppViewModelProvider
import com.example.subscriptiontracker.ui.components.AppTextField
import com.example.subscriptiontracker.ui.theme.BrandGradient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // Which dialog is open (only one at a time)
    var showEditName by rememberSaveable { mutableStateOf(false) }
    var showCurrency by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        ProfileCard(state, onEditClick = { showEditName = true })

        SectionLabel("Preferences")
        SettingsGroup {
            SettingsRow(
                icon = Icons.Rounded.Payments,
                title = "Currency",
                value = "${state.currency.symbol}  ${state.currency.name}",
                onClick = { showCurrency = true }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SettingsIcon(Icons.Rounded.DarkMode, MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(14.dp))
                    Text("Theme", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(Modifier.height(12.dp))
                // Three joined buttons where exactly one is selected
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = state.themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(index, ThemeMode.entries.size),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                activeContentColor = MaterialTheme.colorScheme.primary,
                            )
                        ) {
                            Text(mode.label)
                        }
                    }
                }
            }
        }

        SectionLabel("Reminders")
        RemindersGroup(
            enabled = state.remindersEnabled,
            onEnabledChange = viewModel::setRemindersEnabled,
            onSendTest = viewModel::sendTestReminder,
        )

        SectionLabel("Account")
        SettingsGroup {
            SettingsRow(
                icon = Icons.AutoMirrored.Rounded.Logout,
                title = "Log out",
                onClick = {
                    scope.launch {
                        viewModel.logout()
                        onLogout()
                    }
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(
                icon = Icons.Rounded.DeleteForever,
                title = "Delete account",
                tint = MaterialTheme.colorScheme.error,
                onClick = { showDelete = true }
            )
        }

        Spacer(Modifier.height(28.dp))
        AppVersion()
    }

    if (showEditName) {
        EditNameDialog(
            currentName = state.name,
            onDismiss = { showEditName = false },
            onSave = { newName ->
                viewModel.updateName(newName)
                showEditName = false
            }
        )
    }

    if (showCurrency) {
        CurrencyDialog(
            selected = state.currency,
            onDismiss = { showCurrency = false },
            onSelect = { currency ->
                viewModel.setCurrency(currency)
                showCurrency = false
            }
        )
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Delete your account?") },
            text = { Text("Your account and all ${state.subscriptionCount} subscriptions will be removed from this phone. You can't undo this.") },
            confirmButton = {
                TextButton(onClick = {
                    showDelete = false
                    scope.launch {
                        viewModel.deleteAccount()
                        onLogout()
                    }
                }) { Text("Delete everything", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ProfileCard(state: ProfileUiState, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(BrandGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.name.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(state.name, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                state.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${state.subscriptionCount} subscriptions tracked",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onEditClick) {
            Icon(Icons.Rounded.Edit, contentDescription = "Edit name", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Spacer(Modifier.height(24.dp))
    Text(text, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(8.dp))
}

// Rounded box that holds a few rows
@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        content = content
    )
}

@Composable
private fun SettingsIcon(icon: ImageVector, tint: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(MaterialTheme.shapes.small)
            .background(tint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    value: String? = null,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    val isDanger = tint == MaterialTheme.colorScheme.error
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIcon(icon, tint)
        Spacer(Modifier.width(14.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(4.dp))
        }
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RemindersGroup(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onSendTest: () -> Unit,
) {
    val context = LocalContext.current

    // Re-check the permission every time the screen comes back (e.g. after visiting system settings)
    var hasPermission by remember { mutableStateOf(ReminderNotifications.hasPermission(context)) }
    LifecycleResumeEffect(Unit) {
        hasPermission = ReminderNotifications.hasPermission(context)
        onPauseOrDispose { }
    }

    // True after the user taps "Don't allow", so we can explain how to fix it
    var deniedOnce by rememberSaveable { mutableStateOf(false) }

    // Shows Android's "Allow SubTrack to send you notifications?" popup and gives us the answer
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) onEnabledChange(true) else deniedOnce = true
    }

    val isOn = enabled && hasPermission

    SettingsGroup {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsIcon(Icons.Rounded.NotificationsActive, MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Renewal reminders", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "The day before each payment, around 9 AM",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isOn,
                onCheckedChange = { turnOn ->
                    when {
                        !turnOn -> onEnabledChange(false)
                        hasPermission -> onEnabledChange(true)
                        // Android 13+: ask first, the answer arrives in permissionLauncher above
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        else -> onEnabledChange(true)
                    }
                }
            )
        }

        if (isOn) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            TextButton(onClick = onSendTest, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text("Send a test reminder now")
            }
        }

        // Only after the user said no, or if reminders are on but notifications were later turned off
        val showBlockedHint = !hasPermission && (deniedOnce || enabled)
        if (showBlockedHint && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Notifications are off for SubTrack, so reminders can't be shown. You can allow them in system settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        context.startActivity(intent)
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Open notification settings")
                }
            }
        }
    }
}

@Composable
private fun EditNameDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    // A local copy so typing doesn't change the real name until Save is tapped
    var name by rememberSaveable { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit name") },
        text = {
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full name",
                leadingIcon = Icons.Rounded.Person,
                error = if (name.isBlank()) "Name can't be empty" else null,
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(name) }, enabled = name.isNotBlank()) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun CurrencyDialog(selected: AppCurrency, onDismiss: () -> Unit, onSelect: (AppCurrency) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Currency") },
        text = {
            Column {
                Text(
                    "Only the symbol changes. Prices aren't converted.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                AppCurrency.entries.forEach { currency ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            // selectable = the whole row is tappable, not just the small circle
                            .selectable(
                                selected = currency == selected,
                                onClick = { onSelect(currency) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = currency == selected, onClick = null)
                        Spacer(Modifier.width(8.dp))
                        Text(currency.symbol, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(28.dp))
                        Text(currency.label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun AppVersion() {
    val context = LocalContext.current
    val version = remember {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }
    Text(
        "SubTrack · version $version",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}
