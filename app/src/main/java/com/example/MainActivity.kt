package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AlarmSoundManager
import com.example.data.local.TrackMatrixDatabase
import com.example.data.model.UserRole
import com.example.data.repository.TrackMatrixRepository
import com.example.ui.components.AlertNotificationBanner
import com.example.ui.components.TrackMatrixTopBar
import com.example.ui.screens.CustomerInquiryScreen
import com.example.ui.screens.LiveChatScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainAdminDashboardScreen
import com.example.ui.screens.OrderTrackingDetailScreen
import com.example.ui.screens.SubAdminDashboardScreen
import com.example.ui.screens.SubAdminManagementScreen
import com.example.ui.screens.SystemSettingsScreen
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.TrackMatrixViewModel
import com.example.viewmodel.TrackMatrixViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy { TrackMatrixDatabase.getDatabase(applicationContext) }
    private val alarmSoundManager by lazy { AlarmSoundManager(applicationContext) }
    private val repository by lazy {
        TrackMatrixRepository(
            adminUserDao = database.adminUserDao(),
            dispatchOrderDao = database.dispatchOrderDao(),
            chatMessageDao = database.chatMessageDao(),
            appConfigDao = database.appConfigDao()
        )
    }

    private val viewModel: TrackMatrixViewModel by viewModels {
        TrackMatrixViewModelFactory(repository, alarmSoundManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TrackMatrixApp(viewModel)
            }
        }
    }
}

@Composable
fun TrackMatrixApp(viewModel: TrackMatrixViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentUserRole by viewModel.currentUserRole.collectAsState()
    val currentCustomerName by viewModel.currentCustomerName.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val allSubAdmins by viewModel.allSubAdmins.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val isAlarmActive by viewModel.isAlarmActive.collectAsState()
    val alarmMessage by viewModel.activeAlarmMessage.collectAsState()
    val selectedOrderId by viewModel.selectedOrderId.collectAsState()

    var showQuickSwitchDialog by remember { mutableStateOf(false) }

    // Handle system back navigation
    BackHandler(enabled = currentScreen != AppScreen.LOGIN && currentScreen != AppScreen.MAIN_ADMIN_DASHBOARD && currentScreen != AppScreen.CUSTOMER_PORTAL) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (currentScreen != AppScreen.LOGIN) {
                TrackMatrixTopBar(
                    appName = appConfig.appName,
                    logoPreset = appConfig.logoPreset,
                    currentUserRole = currentUserRole,
                    currentUserName = currentUser?.fullName ?: currentCustomerName,
                    isAlarmActive = isAlarmActive,
                    onToggleSound = { viewModel.toggleAlarmSound() },
                    onSwitchUserClick = { showQuickSwitchDialog = true },
                    onLogoutClick = { viewModel.logout() }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Priority Alert Banner with looping alarm controls
            AlertNotificationBanner(
                visible = isAlarmActive,
                message = alarmMessage ?: "DISPATCH ACTION REQUIRED IMMEDIATELY",
                actionButtonText = if (currentUserRole == UserRole.MAIN_ADMIN) "DISPATCH TO ADMIN" else "VIEW & ACCEPT",
                onActionClick = {
                    if (currentUserRole == UserRole.MAIN_ADMIN) {
                        viewModel.navigateTo(AppScreen.MAIN_ADMIN_DASHBOARD)
                    } else if (currentUserRole == UserRole.SUB_ADMIN) {
                        viewModel.navigateTo(AppScreen.SUB_ADMIN_DASHBOARD)
                    }
                    viewModel.silenceCurrentAlarm()
                },
                onSilenceClick = { viewModel.silenceCurrentAlarm() }
            )

            // Content Screen Router
            Box(modifier = Modifier.weight(1f)) {
                when (currentScreen) {
                    AppScreen.LOGIN -> {
                        LoginScreen(
                            appName = appConfig.appName,
                            onLoginAdmin = { user, pass -> viewModel.loginAdmin(user, pass) },
                            onLoginCustomer = { name -> viewModel.loginCustomer(name) },
                            errorMessage = loginError
                        )
                    }

                    AppScreen.MAIN_ADMIN_DASHBOARD -> {
                        MainAdminDashboardScreen(
                            orders = allOrders,
                            subAdmins = allSubAdmins,
                            appConfig = appConfig,
                            onDispatchOrder = { orderId, adminId, adminName ->
                                viewModel.dispatchOrder(orderId, adminId, adminName)
                            },
                            onToggleAutoDispatch = { viewModel.toggleAutoDispatch(it) },
                            onOpenSubAdminManagement = { viewModel.navigateTo(AppScreen.SUB_ADMIN_MANAGEMENT) },
                            onOpenSettings = { viewModel.navigateTo(AppScreen.SETTINGS) },
                            onViewOrderDetails = { viewModel.openOrderDetails(it) },
                            calculateDistanceKm = { lat1, lon1, lat2, lon2 ->
                                viewModel.calculateDistanceKm(lat1, lon1, lat2, lon2)
                            }
                        )
                    }

                    AppScreen.SUB_ADMIN_DASHBOARD -> {
                        val currentSubAdmin = currentUser ?: allSubAdmins.firstOrNull()
                        if (currentSubAdmin != null) {
                            val assignedOrders = allOrders.filter { it.assignedAdminId == currentSubAdmin.id }
                            SubAdminDashboardScreen(
                                currentAdmin = currentSubAdmin,
                                assignedOrders = assignedOrders,
                                onAcceptOrder = { viewModel.acceptDispatchedOrder(it) },
                                onAdvanceOrderStatus = { orderId, status ->
                                    viewModel.advanceOrderStatus(orderId, status)
                                },
                                onViewOrderDetails = { viewModel.openOrderDetails(it) },
                                onOpenChat = { viewModel.openChat(it) }
                            )
                        } else {
                            Text(
                                "No sub-admin selected. Please log in.",
                                color = Color.White,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    AppScreen.CUSTOMER_PORTAL -> {
                        CustomerInquiryScreen(
                            customerName = currentCustomerName,
                            orders = allOrders,
                            onSubmitInquiry = { title, details, category, phone, pickup, dropoff ->
                                viewModel.submitCustomerInquiry(title, details, category, phone, pickup, dropoff)
                            },
                            onViewOrderDetails = { viewModel.openOrderDetails(it) },
                            onOpenChat = { viewModel.openChat(it) }
                        )
                    }

                    AppScreen.SUB_ADMIN_MANAGEMENT -> {
                        SubAdminManagementScreen(
                            subAdmins = allSubAdmins,
                            onAddSubAdmin = { name, username, pass, phone, lat, lng ->
                                viewModel.addSubAdmin(name, username, pass, phone, lat, lng)
                            },
                            onToggleBlockAdmin = { id, blocked -> viewModel.toggleBlockAdmin(id, blocked) },
                            onChangeAdminPassword = { id, newPass -> viewModel.changeAdminPassword(id, newPass) },
                            onDeleteAdmin = { viewModel.deleteAdmin(it) },
                            onBackClick = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.SETTINGS -> {
                        SystemSettingsScreen(
                            currentConfig = appConfig,
                            mainAdminUsername = "admin",
                            onSaveBranding = { name, logo -> viewModel.updateBranding(name, logo) },
                            onChangeMainAdminPassword = { viewModel.changeMainAdminPassword(it) },
                            onTestAlarmTone = { viewModel.toggleAlarmSound() },
                            onBackClick = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.ORDER_DETAILS -> {
                        val order = allOrders.firstOrNull { it.id == selectedOrderId }
                        if (order != null) {
                            OrderTrackingDetailScreen(
                                order = order,
                                currentUserId = currentUser?.id ?: "customer",
                                currentUserRoleName = currentUserRole.name,
                                onOpenChat = { viewModel.openChat(order.id) },
                                onBackClick = { viewModel.navigateBack() }
                            )
                        }
                    }

                    AppScreen.CHAT -> {
                        val order = allOrders.firstOrNull { it.id == selectedOrderId }
                        if (order != null) {
                            val messages by remember(order.id) {
                                viewModel.getChatMessages(order.id)
                            }.collectAsState(initial = emptyList<com.example.data.local.ChatMessageEntity>())

                            LiveChatScreen(
                                order = order,
                                messages = messages,
                                currentUserId = currentUser?.id ?: "customer",
                                currentUserName = currentUser?.fullName ?: currentCustomerName,
                                currentUserRole = currentUserRole.name,
                                onSendMessage = { text -> viewModel.sendChatMessage(order.id, text) },
                                onBackClick = { viewModel.navigateBack() }
                            )
                        }
                    }
                }
            }
        }
    }

    // Role switcher dialog for rapid testing & switching across roles on device
    if (showQuickSwitchDialog) {
        AlertDialog(
            onDismissRequest = { showQuickSwitchDialog = false },
            title = {
                Text(
                    text = "SWITCH ACCOUNT / ROLE",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = MatrixCyanPrimary
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Quickly simulate other users/devices in the dispatch network:",
                        fontSize = 12.sp,
                        color = Color(0xFF90A4CE)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.loginAdmin("admin", "admin123")
                            showQuickSwitchDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixCyanPrimary, contentColor = Color(0xFF001F2A)),
                        modifier = Modifier.fillMaxWidth().testTag("switch_to_main_admin")
                    ) {
                        Text("Switch to Main Admin (Commander)", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    allSubAdmins.forEach { subAdmin ->
                        OutlinedButton(
                            onClick = {
                                viewModel.loginAdmin(subAdmin.username, subAdmin.password)
                                showQuickSwitchDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sub-Admin: ${subAdmin.fullName}", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Button(
                        onClick = {
                            viewModel.loginCustomer("Sarah Jenkins")
                            showQuickSwitchDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixEmeraldTertiary, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth().testTag("switch_to_customer")
                    ) {
                        Text("Switch to Customer Portal", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { showQuickSwitchDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
