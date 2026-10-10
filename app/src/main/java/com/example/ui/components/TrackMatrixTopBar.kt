package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber

@Composable
fun TrackMatrixTopBar(
    appName: String,
    logoPreset: String,
    currentUserRole: UserRole,
    currentUserName: String,
    isAlarmActive: Boolean,
    onToggleSound: () -> Unit,
    onSwitchUserClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logoIcon: ImageVector = when (logoPreset) {
        "CYBER_AMBER" -> Icons.Default.Radar
        "STEALTH_DARK" -> Icons.Default.Shield
        "EMERALD_RADAR" -> Icons.Default.GpsFixed
        "TECH_GRID" -> Icons.Default.Hub
        else -> Icons.Default.Navigation // MATRIX_BLUE default
    }

    val logoAccentColor: Color = when (logoPreset) {
        "CYBER_AMBER" -> MatrixWarningAmber
        "STEALTH_DARK" -> Color(0xFFB0BEC5)
        "EMERALD_RADAR" -> MatrixEmeraldTertiary
        else -> MatrixCyanPrimary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // App Logo & Custom Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(logoAccentColor.copy(alpha = 0.15f))
                    .border(1.5.dp, logoAccentColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = logoIcon,
                    contentDescription = "Logo",
                    tint = logoAccentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = appName.ifBlank { "TRACKMATRIX" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val roleLabel = when (currentUserRole) {
                        UserRole.MAIN_ADMIN -> "COMMANDER (MAIN ADMIN)"
                        UserRole.SUB_ADMIN -> "DISPATCH ADMIN"
                        UserRole.CUSTOMER -> "CLIENT INQUIRY PORTAL"
                    }
                    val roleColor = when (currentUserRole) {
                        UserRole.MAIN_ADMIN -> MatrixCyanPrimary
                        UserRole.SUB_ADMIN -> MatrixWarningAmber
                        UserRole.CUSTOMER -> MatrixEmeraldTertiary
                    }
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(roleColor)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$roleLabel • $currentUserName",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = roleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Action controls
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Sound alarm indicator / mute button
            IconButton(
                onClick = onToggleSound,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("toggle_sound_btn")
            ) {
                Icon(
                    imageVector = if (isAlarmActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                    contentDescription = "Alarm Sound Status",
                    tint = if (isAlarmActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Quick Role Switcher (Crucial for test driving Main Admin <-> Sub Admin <-> Customer)
            IconButton(
                onClick = onSwitchUserClick,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("switch_user_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Switch Account / Role",
                    tint = MatrixCyanPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Logout
            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("logout_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
