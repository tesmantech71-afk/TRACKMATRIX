package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.AlarmSoundManager
import com.example.data.local.AdminUserEntity
import com.example.data.local.AppConfigEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DispatchOrderEntity
import com.example.data.model.LocationPoint
import com.example.data.model.OrderStatus
import com.example.data.model.UserRole
import com.example.data.repository.TrackMatrixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    LOGIN,
    MAIN_ADMIN_DASHBOARD,
    SUB_ADMIN_DASHBOARD,
    CUSTOMER_PORTAL,
    SUB_ADMIN_MANAGEMENT,
    SETTINGS,
    ORDER_DETAILS,
    CHAT
}

class TrackMatrixViewModel(
    private val repository: TrackMatrixRepository,
    private val alarmSoundManager: AlarmSoundManager
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _previousScreen = MutableStateFlow<AppScreen?>(null)

    private val _currentUser = MutableStateFlow<AdminUserEntity?>(null)
    val currentUser: StateFlow<AdminUserEntity?> = _currentUser.asStateFlow()

    private val _currentUserRole = MutableStateFlow(UserRole.MAIN_ADMIN)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    private val _currentCustomerName = MutableStateFlow("Sarah Jenkins")
    val currentCustomerName: StateFlow<String> = _currentCustomerName.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _selectedOrderId = MutableStateFlow<String?>(null)
    val selectedOrderId: StateFlow<String?> = _selectedOrderId.asStateFlow()

    val isAlarmActive = alarmSoundManager.isAlarmActive
    val activeAlarmMessage = alarmSoundManager.activeAlarmMessage

    val allOrders: StateFlow<List<DispatchOrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubAdmins: StateFlow<List<AdminUserEntity>> = repository.allSubAdmins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appConfig: StateFlow<AppConfigEntity> = repository.appConfig
        .combine(MutableStateFlow(Unit)) { config, _ ->
            config ?: AppConfigEntity()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppConfigEntity())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }

        // Monitor orders to trigger repeated alarm sound when action is required
        viewModelScope.launch {
            combine(allOrders, _currentUser, _currentUserRole) { orders, user, role ->
                Triple(orders, user, role)
            }.collectLatest { (orders, user, role) ->
                when (role) {
                    UserRole.MAIN_ADMIN -> {
                        val unhandled = orders.firstOrNull {
                            it.status == OrderStatus.PENDING_MAIN_ADMIN.name && !it.isAcknowledgedByMainAdmin
                        }
                        if (unhandled != null) {
                            alarmSoundManager.startRepeatedAlarm(
                                "🚨 New inquiry '${unhandled.itemTitle}' arrived from ${unhandled.customerName} - Main Admin action required!"
                            )
                        } else if (isAlarmActive.value && activeAlarmMessage.value?.contains("Main Admin") == true) {
                            alarmSoundManager.stopAlarm()
                        }
                    }
                    UserRole.SUB_ADMIN -> {
                        val unhandled = orders.firstOrNull {
                            it.assignedAdminId == user?.id &&
                                    it.status == OrderStatus.DISPATCHED.name &&
                                    !it.isAcknowledgedByAssignedAdmin
                        }
                        if (unhandled != null) {
                            alarmSoundManager.startRepeatedAlarm(
                                "🚨 Urgent Job Dispatched to You: '${unhandled.itemTitle}' - Acceptance Required!"
                            )
                        } else if (isAlarmActive.value && activeAlarmMessage.value?.contains("Dispatched") == true) {
                            alarmSoundManager.stopAlarm()
                        }
                    }
                    UserRole.CUSTOMER -> {
                        // Customer does not receive emergency tones
                    }
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _previousScreen.value = _currentScreen.value
        _currentScreen.value = screen
    }

    fun navigateBack() {
        _currentScreen.value = _previousScreen.value ?: when (_currentUserRole.value) {
            UserRole.MAIN_ADMIN -> AppScreen.MAIN_ADMIN_DASHBOARD
            UserRole.SUB_ADMIN -> AppScreen.SUB_ADMIN_DASHBOARD
            UserRole.CUSTOMER -> AppScreen.CUSTOMER_PORTAL
        }
    }

    fun loginAdmin(username: String, pass: String) {
        viewModelScope.launch {
            _loginError.value = null
            val admin = repository.getAdminByUsername(username)
            if (admin == null) {
                _loginError.value = "Invalid username or password"
                return@launch
            }
            if (admin.password != pass) {
                _loginError.value = "Incorrect password"
                return@launch
            }
            if (admin.isBlocked) {
                _loginError.value = "Access Denied: Your account has been blocked by Main Admin."
                return@launch
            }

            _currentUser.value = admin
            if (admin.role == "MAIN_ADMIN") {
                _currentUserRole.value = UserRole.MAIN_ADMIN
                _currentScreen.value = AppScreen.MAIN_ADMIN_DASHBOARD
            } else {
                _currentUserRole.value = UserRole.SUB_ADMIN
                _currentScreen.value = AppScreen.SUB_ADMIN_DASHBOARD
            }
        }
    }

    fun loginCustomer(name: String) {
        _currentUser.value = null
        _currentCustomerName.value = name
        _currentUserRole.value = UserRole.CUSTOMER
        _currentScreen.value = AppScreen.CUSTOMER_PORTAL
    }

    fun logout() {
        alarmSoundManager.stopAlarm()
        _currentUser.value = null
        _currentUserRole.value = UserRole.MAIN_ADMIN
        _currentScreen.value = AppScreen.LOGIN
    }

    fun toggleAlarmSound() {
        if (alarmSoundManager.isAlarmActive.value) {
            alarmSoundManager.stopAlarm()
        } else {
            alarmSoundManager.startRepeatedAlarm("Test Alarm: Dispatch Alert Repeating Tone")
        }
    }

    fun silenceCurrentAlarm() {
        alarmSoundManager.stopAlarm()
    }

    fun submitCustomerInquiry(
        itemTitle: String,
        itemDetails: String,
        itemCategory: String,
        customerPhone: String,
        pickupPoint: LocationPoint,
        dropoffPoint: LocationPoint
    ) {
        viewModelScope.launch {
            val order = repository.createCustomerInquiry(
                customerName = _currentCustomerName.value,
                customerPhone = customerPhone,
                itemTitle = itemTitle,
                itemDetails = itemDetails,
                itemCategory = itemCategory,
                pickupAddress = pickupPoint.address,
                pickupLat = pickupPoint.latitude,
                pickupLng = pickupPoint.longitude,
                dropoffAddress = dropoffPoint.address,
                dropoffLat = dropoffPoint.latitude,
                dropoffLng = dropoffPoint.longitude
            )

            // Auto-dispatch check
            if (appConfig.value.autoDispatchEnabled) {
                val subAdmins = allSubAdmins.value.filter { !it.isBlocked }
                if (subAdmins.isNotEmpty()) {
                    val closest = subAdmins.minByOrNull { admin ->
                        repository.calculateDistanceKm(admin.latitude, admin.longitude, pickupPoint.latitude, pickupPoint.longitude)
                    }
                    if (closest != null) {
                        repository.dispatchOrderToAdmin(order.id, closest.id, closest.fullName)
                    }
                }
            }
        }
    }

    fun dispatchOrder(orderId: String, adminId: String, adminName: String) {
        viewModelScope.launch {
            repository.dispatchOrderToAdmin(orderId, adminId, adminName)
            repository.acknowledgeByMainAdmin(orderId)
            alarmSoundManager.stopAlarm()
        }
    }

    fun acceptDispatchedOrder(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.ACCEPTED)
            repository.acknowledgeByAssignedAdmin(orderId)
            alarmSoundManager.stopAlarm()
        }
    }

    fun advanceOrderStatus(orderId: String, currentStatus: OrderStatus) {
        viewModelScope.launch {
            val nextStatus = when (currentStatus) {
                OrderStatus.DISPATCHED -> OrderStatus.ACCEPTED
                OrderStatus.ACCEPTED -> OrderStatus.EN_ROUTE_PICKUP
                OrderStatus.EN_ROUTE_PICKUP -> OrderStatus.ITEM_COLLECTED
                OrderStatus.ITEM_COLLECTED -> OrderStatus.IN_TRANSIT
                OrderStatus.IN_TRANSIT -> OrderStatus.DELIVERED
                else -> currentStatus
            }
            repository.updateOrderStatus(orderId, nextStatus)
        }
    }

    fun addSubAdmin(fullName: String, username: String, pass: String, phone: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            val newAdmin = AdminUserEntity(
                id = UUID.randomUUID().toString(),
                username = username,
                password = pass,
                fullName = fullName,
                role = "SUB_ADMIN",
                isBlocked = false,
                latitude = lat,
                longitude = lng,
                phone = phone,
                status = "AVAILABLE"
            )
            repository.insertAdmin(newAdmin)
        }
    }

    fun toggleBlockAdmin(adminId: String, isBlocked: Boolean) {
        viewModelScope.launch {
            repository.setAdminBlocked(adminId, isBlocked)
        }
    }

    fun changeAdminPassword(adminId: String, newPass: String) {
        viewModelScope.launch {
            repository.updateAdminPassword(adminId, newPass)
        }
    }

    fun deleteAdmin(admin: AdminUserEntity) {
        viewModelScope.launch {
            repository.deleteAdmin(admin)
        }
    }

    fun updateBranding(appName: String, logoPreset: String) {
        viewModelScope.launch {
            val updated = appConfig.value.copy(appName = appName, logoPreset = logoPreset)
            repository.updateAppConfig(updated)
        }
    }

    fun toggleAutoDispatch(enabled: Boolean) {
        viewModelScope.launch {
            val updated = appConfig.value.copy(autoDispatchEnabled = enabled)
            repository.updateAppConfig(updated)
        }
    }

    fun changeMainAdminPassword(newPass: String) {
        viewModelScope.launch {
            val main = repository.getMainAdmin()
            if (main != null) {
                repository.updateAdminPassword(main.id, newPass)
            }
        }
    }

    fun openOrderDetails(orderId: String) {
        _selectedOrderId.value = orderId
        navigateTo(AppScreen.ORDER_DETAILS)
    }

    fun openChat(orderId: String) {
        _selectedOrderId.value = orderId
        navigateTo(AppScreen.CHAT)
    }

    fun getChatMessages(orderId: String) = repository.getChatMessages(orderId)

    fun sendChatMessage(orderId: String, text: String) {
        viewModelScope.launch {
            val senderId = _currentUser.value?.id ?: "customer_client"
            val senderName = _currentUser.value?.fullName ?: _currentCustomerName.value
            val senderRole = when (_currentUserRole.value) {
                UserRole.MAIN_ADMIN -> "MAIN_ADMIN"
                UserRole.SUB_ADMIN -> "SUB_ADMIN"
                UserRole.CUSTOMER -> "CUSTOMER"
            }
            repository.sendChatMessage(orderId, senderId, senderName, senderRole, text)
        }
    }

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        return repository.calculateDistanceKm(lat1, lon1, lat2, lon2)
    }

    override fun onCleared() {
        super.onCleared()
        alarmSoundManager.release()
    }
}

class TrackMatrixViewModelFactory(
    private val repository: TrackMatrixRepository,
    private val alarmSoundManager: AlarmSoundManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackMatrixViewModel::class.java)) {
            return TrackMatrixViewModel(repository, alarmSoundManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
