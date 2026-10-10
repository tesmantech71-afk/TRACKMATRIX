package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DispatchOrderEntity
import com.example.data.model.OrderStatus
import com.example.ui.components.InteractiveMapView
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingDetailScreen(
    order: DispatchOrderEntity,
    currentUserId: String,
    currentUserRoleName: String,
    onOpenChat: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSimulating by remember { mutableStateOf(false) }

    val adminLat = order.currentAdminLat ?: order.pickupLat + 0.008
    val adminLng = order.currentAdminLng ?: order.pickupLng + 0.006
    val currentStatus = OrderStatus.valueOf(order.status)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070D1E))
    ) {
        TopAppBar(
            title = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = order.trackingCode,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = MatrixCyanPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OrderStatusBadge(status = currentStatus)
                    }
                    Text(
                        text = order.itemTitle,
                        fontSize = 11.sp,
                        color = Color.White,
                        maxLines = 1
                    )
                }
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
            actions = {
                // Chat button
                if (order.assignedAdminId != null) {
                    IconButton(onClick = onOpenChat, modifier = Modifier.testTag("top_bar_chat_btn")) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat",
                            tint = MatrixCyanPrimary
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070D1E))
        )

        // Interactive Vector Map with Google Maps connectivity & live ETA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp)
        ) {
            InteractiveMapView(
                adminLat = adminLat,
                adminLng = adminLng,
                adminName = order.assignedAdminName ?: "Awaiting Dispatch",
                pickupLat = order.pickupLat,
                pickupLng = order.pickupLng,
                pickupAddress = order.pickupAddress,
                dropoffLat = order.dropoffLat,
                dropoffLng = order.dropoffLng,
                dropoffAddress = order.dropoffAddress,
                etaPickupMinutes = order.etaPickupMinutes,
                etaDropoffMinutes = order.etaDropoffMinutes,
                isSimulatingMovement = isSimulating,
                onToggleSimulation = { isSimulating = !isSimulating },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Bottom Dispatch Details Summary Sheet
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A38)),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3264))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ASSIGNED COURIER / ADMIN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF90A4CE),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = order.assignedAdminName ?: "Unassigned (Main Admin Queue)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (order.assignedAdminName != null) MatrixCyanPrimary else MatrixWarningAmber
                        )
                    }

                    if (order.assignedAdminId != null) {
                        Button(
                            onClick = onOpenChat,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MatrixCyanPrimary,
                                contentColor = Color(0xFF001F2A)
                            ),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("open_live_chat_btn")
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open Live Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
