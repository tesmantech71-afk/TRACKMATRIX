package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppConfigEntity
import com.example.ui.theme.MatrixAlarmCrimson
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemSettingsScreen(
    currentConfig: AppConfigEntity,
    mainAdminUsername: String,
    onSaveBranding: (newName: String, newLogoPreset: String) -> Unit,
    onChangeMainAdminPassword: (newPass: String) -> Unit,
    onTestAlarmTone: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var appNameInput by remember { mutableStateOf(currentConfig.appName) }
    var selectedLogoPreset by remember { mutableStateOf(currentConfig.logoPreset) }

    var newMainAdminPassword by remember { mutableStateOf("") }
    var passwordChangeSuccessMessage by remember { mutableStateOf<String?>(null) }
    var brandingSaveSuccessMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070D1E))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "MAIN ADMIN SYSTEM CONFIG",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF070D1E)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: App Name & Logo Customization
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1730)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2C56))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "APP BRANDING & LOGO EMBLEM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = MatrixCyanPrimary
                    )
                    Text(
                        text = "Customize the global application brand name and emblem displayed across all devices",
                        fontSize = 11.sp,
                        color = Color(0xFF90A4CE)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = appNameInput,
                        onValueChange = { appNameInput = it },
                        label = { Text("App Name (e.g. TRACKMATRIX)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("app_name_input_field")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "SELECT APP LOGO EMBLEM:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val presets = listOf(
                        Triple("MATRIX_BLUE", "Cyber Compass", Icons.Default.Navigation),
                        Triple("CYBER_AMBER", "Radar Grid", Icons.Default.Radar),
                        Triple("STEALTH_DARK", "Stealth Shield", Icons.Default.Shield),
                        Triple("EMERALD_RADAR", "Emerald GPS", Icons.Default.GpsFixed),
                        Triple("TECH_GRID", "Matrix Hub", Icons.Default.Hub)
                    )

                    presets.forEach { (presetKey, label, icon) ->
                        val isSelected = selectedLogoPreset == presetKey
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MatrixCyanPrimary.copy(alpha = 0.15f) else Color(0xFF091124))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MatrixCyanPrimary else Color(0xFF16254A),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedLogoPreset = presetKey }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MatrixCyanPrimary else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MatrixCyanPrimary else Color.White
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MatrixCyanPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    if (brandingSaveSuccessMessage != null) {
                        Text(
                            text = brandingSaveSuccessMessage!!,
                            color = MatrixEmeraldTertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (appNameInput.isNotBlank()) {
                                onSaveBranding(appNameInput.trim(), selectedLogoPreset)
                                brandingSaveSuccessMessage = "Branding & Logo updated successfully!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixCyanPrimary, contentColor = Color(0xFF001F2A)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_branding_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Branding & App Logo", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Section 2: Permanent Main Admin Credentials Change
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1730)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2C56))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PERMANENT MAIN ADMIN PASSWORD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = MatrixWarningAmber
                    )
                    Text(
                        text = "Main admin comes with default login (admin/admin123). Only Main Admin can permanently change this credential.",
                        fontSize = 11.sp,
                        color = Color(0xFF90A4CE)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newMainAdminPassword,
                        onValueChange = { newMainAdminPassword = it },
                        label = { Text("New Permanent Password") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MatrixWarningAmber) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_main_admin_pass_field")
                    )

                    if (passwordChangeSuccessMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = passwordChangeSuccessMessage!!,
                            color = MatrixEmeraldTertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (newMainAdminPassword.isNotBlank()) {
                                onChangeMainAdminPassword(newMainAdminPassword.trim())
                                passwordChangeSuccessMessage = "Permanent Main Admin password updated!"
                                newMainAdminPassword = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixWarningAmber, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("update_main_admin_pass_btn")
                    ) {
                        Text("Permanently Update Password", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Section 3: Notification Tone Test
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1730)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2C56))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "REPEATING ALERT TONE TEST",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = MatrixAlarmCrimson
                    )
                    Text(
                        text = "Main admin & assigned sub-admins receive an uninterrupted repeated alarm tone until action is taken.",
                        fontSize = 11.sp,
                        color = Color(0xFF90A4CE)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onTestAlarmTone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_repeating_alarm_btn")
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MatrixAlarmCrimson, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Trigger Test Alarm Tone", color = Color.White)
                    }
                }
            }
        }
    }
}
