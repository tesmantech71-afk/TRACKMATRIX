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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.example.data.local.DispatchOrderEntity
import com.example.data.model.LocationPoint
import com.example.data.model.OrderStatus
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber

@Composable
fun CustomerInquiryScreen(
    customerName: String,
    orders: List<DispatchOrderEntity>,
    onSubmitInquiry: (
        itemTitle: String,
        itemDetails: String,
        itemCategory: String,
        customerPhone: String,
        pickupPoint: LocationPoint,
        dropoffPoint: LocationPoint
    ) -> Unit,
    onViewOrderDetails: (orderId: String) -> Unit,
    onOpenChat: (orderId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
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
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "CLIENT INQUIRY & DISPATCH PORTAL",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = MatrixEmeraldTertiary
                                )
                                Text(
                                    text = "Welcome, $customerName",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = { showCreateDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MatrixEmeraldTertiary, contentColor = Color.Black),
                                modifier = Modifier.testTag("new_inquiry_top_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Pickup", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Item location and addresses are routed directly to Main Admin for secure dispatch.",
                            fontSize = 11.sp,
                            color = Color(0xFF90A4CE)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "YOUR DISPATCH ORDERS (${orders.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF90A4CE),
                    letterSpacing = 1.sp
                )
            }

            if (orders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = MatrixEmeraldTertiary, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No active dispatches submitted yet", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tap 'New Pickup' below to dispatch an item to Main Admin.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(orders, key = { it.id }) { order ->
                    CustomerOrderCard(
                        order = order,
                        onViewDetails = { onViewOrderDetails(order.id) },
                        onOpenChat = { onOpenChat(order.id) }
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = MatrixEmeraldTertiary,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("customer_new_inquiry_fab")
        ) {
            Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = "New Inquiry")
                Spacer(modifier = Modifier.width(6.dp))
                Text("SEND INQUIRY", fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
        }

        if (showCreateDialog) {
            CreateInquiryDialog(
                customerName = customerName,
                onDismiss = { showCreateDialog = false },
                onConfirm = { title, details, category, phone, pickup, dropoff ->
                    onSubmitInquiry(title, details, category, phone, pickup, dropoff)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun CustomerOrderCard(
    order: DispatchOrderEntity,
    onViewDetails: () -> Unit,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentStatus = OrderStatus.valueOf(order.status)
    val hasAdminAccepted = currentStatus != OrderStatus.PENDING_MAIN_ADMIN && currentStatus != OrderStatus.DISPATCHED

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("customer_order_card_${order.id}")
            .clickable { onViewDetails() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1730)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2C56))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                    color = MatrixEmeraldTertiary
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

            if (order.assignedAdminName != null) {
                Text(
                    text = "Dispatched Courier: ${order.assignedAdminName}",
                    fontSize = 11.sp,
                    color = MatrixCyanPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF091024))
                    .padding(8.dp)
            ) {
                Text(text = "PICKUP: ${order.pickupAddress}", fontSize = 10.sp, color = MatrixEmeraldTertiary)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = "DROPOFF: ${order.dropoffAddress}", fontSize = 10.sp, color = Color(0xFF82B1FF))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chat button enabled once assigned admin accepts
                if (hasAdminAccepted) {
                    Button(
                        onClick = onOpenChat,
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixCyanPrimary, contentColor = Color(0xFF001F2A)),
                        modifier = Modifier.height(36.dp).testTag("customer_chat_btn_${order.id}")
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chat with Courier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                }

                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.height(36.dp).testTag("customer_map_btn_${order.id}")
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Live Map & ETA", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun CreateInquiryDialog(
    customerName: String,
    onDismiss: () -> Unit,
    onConfirm: (
        itemTitle: String,
        itemDetails: String,
        itemCategory: String,
        customerPhone: String,
        pickupPoint: LocationPoint,
        dropoffPoint: LocationPoint
    ) -> Unit
) {
    var itemTitle by remember { mutableStateOf("") }
    var itemDetails by remember { mutableStateOf("") }
    var itemCategory by remember { mutableStateOf("Urgent Parcel") }
    var customerPhone by remember { mutableStateOf("+1 555-0128") }

    var selectedPickupIndex by remember { mutableStateOf(0) }
    var selectedDropoffIndex by remember { mutableStateOf(2) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Send Item Dispatch to Main Admin", fontWeight = FontWeight.Black, fontSize = 15.sp, color = MatrixEmeraldTertiary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = itemTitle,
                    onValueChange = { itemTitle = it },
                    label = { Text("Item Title (e.g. Critical Hard Drive)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("inquiry_item_title")
                )

                OutlinedTextField(
                    value = itemDetails,
                    onValueChange = { itemDetails = it },
                    label = { Text("Item & Delivery Notes") },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("inquiry_item_details")
                )

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("Your Contact Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Pickup location preset
                Text(text = "Pick-Up Location (Sent to Main Admin):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MatrixEmeraldTertiary)
                LocationPickerRow(
                    selectedLocation = LocationPoint.PRESETS[selectedPickupIndex],
                    onSelect = { selectedPickupIndex = LocationPoint.PRESETS.indexOf(it) }
                )

                // Drop-off location preset
                Text(text = "Drop-Off Destination:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF82B1FF))
                LocationPickerRow(
                    selectedLocation = LocationPoint.PRESETS[selectedDropoffIndex],
                    onSelect = { selectedDropoffIndex = LocationPoint.PRESETS.indexOf(it) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (itemTitle.isNotBlank()) {
                        onConfirm(
                            itemTitle.trim(),
                            itemDetails.trim(),
                            itemCategory,
                            customerPhone.trim(),
                            LocationPoint.PRESETS[selectedPickupIndex],
                            LocationPoint.PRESETS[selectedDropoffIndex]
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MatrixEmeraldTertiary, contentColor = Color.Black),
                modifier = Modifier.testTag("submit_inquiry_confirm_btn")
            ) {
                Text("Send to Main Admin", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun LocationPickerRow(
    selectedLocation: LocationPoint,
    onSelect: (LocationPoint) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF091124))
                .border(1.dp, Color(0xFF1E3264), RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedLocation.address,
                fontSize = 11.sp,
                color = Color.White,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            Text(text = "Change", fontSize = 10.sp, color = MatrixCyanPrimary, fontWeight = FontWeight.Bold)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LocationPoint.PRESETS.forEach { loc ->
                DropdownMenuItem(
                    text = { Text(loc.address, fontSize = 11.sp) },
                    onClick = {
                        onSelect(loc)
                        expanded = false
                    }
                )
            }
        }
    }
}
