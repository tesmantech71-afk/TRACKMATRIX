package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AdminUserEntity
import com.example.data.local.DispatchOrderEntity
import com.example.data.model.OrderStatus
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.MatrixAlarmCrimson
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber

@Composable
fun SubAdminDashboardScreen(
    currentAdmin: AdminUserEntity,
    assignedOrders: List<DispatchOrderEntity>,
    onAcceptOrder: (orderId: String) -> Unit,
    onAdvanceOrderStatus: (orderId: String, currentStatus: OrderStatus) -> Unit,
    onViewOrderDetails: (orderId: String) -> Unit,
    onOpenChat: (orderId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 86.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E3F)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D3264))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ASSIGNED DISPATCH ORDERS (${assignedOrders.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = MatrixWarningAmber
                        )
                        Text(
                            text = "Admin Unit: ${currentAdmin.fullName}",
                            fontSize = 11.sp,
                            color = Color(0xFFB0BEC5)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MatrixEmeraldTertiary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "STATUS: ${currentAdmin.status}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MatrixEmeraldTertiary
                        )
                    }
                }
            }
        }

        if (assignedOrders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Color(0xFF5D719E),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No active jobs dispatched to your unit yet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF90A4CE)
                        )
                        Text(
                            text = "Standby. Repeating alarm will sound when Main Admin dispatches a job.",
                            fontSize = 11.sp,
                            color = Color(0xFF5D719E)
                        )
                    }
                }
            }
        } else {
            items(assignedOrders, key = { it.id }) { order ->
                SubAdminOrderCard(
                    order = order,
                    onAccept = { onAcceptOrder(order.id) },
                    onAdvanceStatus = {
                        onAdvanceOrderStatus(order.id, OrderStatus.valueOf(order.status))
                    },
                    onViewDetails = { onViewOrderDetails(order.id) },
                    onOpenChat = { onOpenChat(order.id) }
                )
            }
        }
    }
}

@Composable
fun SubAdminOrderCard(
    order: DispatchOrderEntity,
    onAccept: () -> Unit,
    onAdvanceStatus: () -> Unit,
    onViewDetails: () -> Unit,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentStatus = OrderStatus.valueOf(order.status)
    val isPendingAcceptance = currentStatus == OrderStatus.DISPATCHED

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("subadmin_order_card_${order.id}")
            .clickable { onViewDetails() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D1730)
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPendingAcceptance) MatrixAlarmCrimson else Color(0xFF1B2C56)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.trackingCode,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MatrixCyanPrimary
                )

                OrderStatusBadge(status = currentStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.itemTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Client: ${order.customerName} (${order.customerPhone})",
                fontSize = 11.sp,
                color = Color(0xFF90A4CE)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Shared Addresses from Main Admin
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF091024))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MatrixEmeraldTertiary))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("PICK UP ADDRESS (SHARED BY MAIN ADMIN)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MatrixEmeraldTertiary)
                        Text(order.pickupAddress, fontSize = 11.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF2979FF)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("DROP OFF ADDRESS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF82B1FF))
                        Text(order.dropoffAddress, fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chat with User (available once accepted)
                if (currentStatus != OrderStatus.PENDING_MAIN_ADMIN && currentStatus != OrderStatus.DISPATCHED) {
                    OutlinedButton(
                        onClick = onOpenChat,
                        modifier = Modifier.height(36.dp).testTag("chat_with_user_btn_${order.id}")
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chat with User", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                }

                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Map & ETA", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Primary Next Stage Action
                if (isPendingAcceptance) {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixAlarmCrimson,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("accept_job_btn_${order.id}")
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ACCEPT DISPATCH", fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                } else if (currentStatus != OrderStatus.DELIVERED) {
                    val nextLabel = when (currentStatus) {
                        OrderStatus.ACCEPTED -> "En Route to Pickup"
                        OrderStatus.EN_ROUTE_PICKUP -> "Item Collected"
                        OrderStatus.ITEM_COLLECTED -> "In Transit to Dropoff"
                        OrderStatus.IN_TRANSIT -> "Mark Delivered"
                        else -> "Update Status"
                    }

                    Button(
                        onClick = onAdvanceStatus,
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixCyanPrimary, contentColor = Color(0xFF001F2A)),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("advance_status_btn_${order.id}")
                    ) {
                        Text(nextLabel, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
