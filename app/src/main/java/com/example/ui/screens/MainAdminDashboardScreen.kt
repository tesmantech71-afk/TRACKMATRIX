package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.AdminUserEntity
import com.example.data.local.AppConfigEntity
import com.example.data.local.DispatchOrderEntity
import com.example.data.model.OrderStatus
import com.example.ui.components.AdminStatusBadge
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.MatrixAlarmCrimson
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber

@Composable
fun MainAdminDashboardScreen(
    orders: List<DispatchOrderEntity>,
    subAdmins: List<AdminUserEntity>,
    appConfig: AppConfigEntity,
    onDispatchOrder: (orderId: String, adminId: String, adminName: String) -> Unit,
    onToggleAutoDispatch: (Boolean) -> Unit,
    onOpenSubAdminManagement: () -> Unit,
    onOpenSettings: () -> Unit,
    onViewOrderDetails: (orderId: String) -> Unit,
    calculateDistanceKm: (lat1: Double, lon1: Double, lat2: Double, lon2: Double) -> Double,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All Inquiries, 1: Pending Dispatch, 2: Active & En Route

    val filteredOrders = when (selectedTab) {
        1 -> orders.filter { it.status == OrderStatus.PENDING_MAIN_ADMIN.name }
        2 -> orders.filter { it.status != OrderStatus.PENDING_MAIN_ADMIN.name && it.status != OrderStatus.DELIVERED.name }
        else -> orders
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 86.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Auto-Dispatch & Action Bar Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E3F)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D3264))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (appConfig.autoDispatchEnabled) MatrixCyanPrimary else Color(0xFF90A4CE),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AUTO-DISPATCH ENGINE",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = if (appConfig.autoDispatchEnabled) MatrixCyanPrimary else Color(0xFF90A4CE)
                                    )
                                }
                                Text(
                                    text = if (appConfig.autoDispatchEnabled)
                                        "Jobs instantly routed to closest sub-admin on arrival"
                                    else
                                        "Manual dispatch mode active",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB0BEC5)
                                )
                            }

                            Switch(
                                checked = appConfig.autoDispatchEnabled,
                                onCheckedChange = onToggleAutoDispatch,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF001F2A),
                                    checkedTrackColor = MatrixCyanPrimary
                                ),
                                modifier = Modifier.testTag("auto_dispatch_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Navigation buttons for Sub Admins and Settings
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onOpenSubAdminManagement,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("manage_sub_admins_btn")
                            ) {
                                Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Fleet Admins (${subAdmins.size})", fontSize = 11.sp, maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = onOpenSettings,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("main_admin_settings_btn")
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Branding & Pass", fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // Inquiries Tab Filter
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MatrixCyanPrimary,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    val pendingCount = orders.count { it.status == OrderStatus.PENDING_MAIN_ADMIN.name }
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("All Jobs (${orders.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Pending (${pendingCount})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pendingCount > 0) MatrixAlarmCrimson else Color.Unspecified
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Active Fleet En Route", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (filteredOrders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MatrixEmeraldTertiary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No dispatch inquiries in this view",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF90A4CE)
                            )
                        }
                    }
                }
            } else {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderDispatchCard(
                        order = order,
                        subAdmins = subAdmins,
                        onDispatch = { adminId, adminName ->
                            onDispatchOrder(order.id, adminId, adminName)
                        },
                        onViewDetails = { onViewOrderDetails(order.id) },
                        calculateDistanceKm = calculateDistanceKm
                    )
                }
            }
        }
    }
}

@Composable
fun OrderDispatchCard(
    order: DispatchOrderEntity,
    subAdmins: List<AdminUserEntity>,
    onDispatch: (adminId: String, adminName: String) -> Unit,
    onViewDetails: () -> Unit,
    calculateDistanceKm: (lat1: Double, lon1: Double, lat2: Double, lon2: Double) -> Double,
    modifier: Modifier = Modifier
) {
    var isDispatchMenuOpen by remember { mutableStateOf(false) }

    // Rank sub-admins by proximity to pickup location
    val rankedAdmins = remember(subAdmins, order.pickupLat, order.pickupLng) {
        subAdmins.map { admin ->
            val dist = calculateDistanceKm(admin.latitude, admin.longitude, order.pickupLat, order.pickupLng)
            admin to dist
        }.sortedBy { it.second }
    }

    val closestAdmin = rankedAdmins.firstOrNull()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}")
            .clickable { onViewDetails() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D1730)
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (order.status == OrderStatus.PENDING_MAIN_ADMIN.name) MatrixAlarmCrimson.copy(alpha = 0.6f) else Color(0xFF1B2C56)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with tracking & status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.trackingCode,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = MatrixCyanPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${order.itemCategory}",
                        fontSize = 11.sp,
                        color = Color(0xFF90A4CE)
                    )
                }

                OrderStatusBadge(
                    status = OrderStatus.valueOf(order.status)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.itemTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (order.itemDetails.isNotBlank()) {
                Text(
                    text = order.itemDetails,
                    fontSize = 11.sp,
                    color = Color(0xFF90A4CE),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pickup & Dropoff Address Matrix
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF091024))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MatrixEmeraldTertiary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "PICKUP LOCATION (PRIVATE TO MAIN ADMIN)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MatrixEmeraldTertiary)
                        Text(text = order.pickupAddress, fontSize = 11.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2979FF))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "DROPOFF DESTINATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF82B1FF))
                        Text(text = order.dropoffAddress, fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Closest Admin Calculation Display
            if (closestAdmin != null) {
                val (admin, distKm) = closestAdmin
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MatrixCyanPrimary.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsRun,
                            contentDescription = null,
                            tint = MatrixCyanPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Closest Admin: ${admin.fullName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = String.format("%.1f km away", distKm),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MatrixCyanPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions: Dispatch to Admin or Track Live
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Live Map & Chat", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    Button(
                        onClick = { isDispatchMenuOpen = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (order.assignedAdminId == null) MatrixWarningAmber else Color(0xFF1E356A),
                            contentColor = if (order.assignedAdminId == null) Color.Black else Color.White
                        ),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("dispatch_action_btn_${order.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (order.assignedAdminId == null) "Dispatch to Admin" else "Re-assign (${order.assignedAdminName})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Dropdown listing all added admins sorted by proximity
                    DropdownMenu(
                        expanded = isDispatchMenuOpen,
                        onDismissRequest = { isDispatchMenuOpen = false }
                    ) {
                        Text(
                            text = "SELECT ADMIN (SORTED BY PROXIMITY):",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MatrixCyanPrimary
                        )
                        rankedAdmins.forEach { (admin, dist) ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(admin.fullName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            if (admin.isBlocked) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("(BLOCKED)", color = MatrixAlarmCrimson, fontSize = 10.sp)
                                            }
                                        }
                                        Text(
                                            text = String.format("%.1f km from pickup • %s", dist, admin.status),
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                },
                                onClick = {
                                    isDispatchMenuOpen = false
                                    onDispatch(admin.id, admin.fullName)
                                },
                                enabled = !admin.isBlocked
                            )
                        }
                    }
                }
            }
        }
    }
}
