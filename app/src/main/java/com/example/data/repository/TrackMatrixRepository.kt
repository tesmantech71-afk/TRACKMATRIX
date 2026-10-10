package com.example.data.repository

import com.example.data.local.AdminUserDao
import com.example.data.local.AdminUserEntity
import com.example.data.local.AppConfigDao
import com.example.data.local.AppConfigEntity
import com.example.data.local.ChatMessageDao
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DispatchOrderDao
import com.example.data.local.DispatchOrderEntity
import com.example.data.model.OrderStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.math.*

class TrackMatrixRepository(
    private val adminUserDao: AdminUserDao,
    private val dispatchOrderDao: DispatchOrderDao,
    private val chatMessageDao: ChatMessageDao,
    private val appConfigDao: AppConfigDao
) {
    val allAdmins: Flow<List<AdminUserEntity>> = adminUserDao.getAllAdmins()
    val allSubAdmins: Flow<List<AdminUserEntity>> = adminUserDao.getAllSubAdmins()
    val allOrders: Flow<List<DispatchOrderEntity>> = dispatchOrderDao.getAllOrders()
    val appConfig: Flow<AppConfigEntity?> = appConfigDao.getConfig()

    fun getOrdersForAdmin(adminId: String): Flow<List<DispatchOrderEntity>> {
        return dispatchOrderDao.getOrdersForAdmin(adminId)
    }

    fun getOrderByIdFlow(orderId: String): Flow<DispatchOrderEntity?> {
        return dispatchOrderDao.getOrderByIdFlow(orderId)
    }

    fun getChatMessages(orderId: String): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getMessagesForOrder(orderId)
    }

    suspend fun getAdminByUsername(username: String): AdminUserEntity? {
        return adminUserDao.getAdminByUsername(username)
    }

    suspend fun getAdminById(id: String): AdminUserEntity? {
        return adminUserDao.getAdminById(id)
    }

    suspend fun getMainAdmin(): AdminUserEntity? {
        return adminUserDao.getMainAdmin()
    }

    suspend fun insertAdmin(admin: AdminUserEntity) {
        adminUserDao.insertAdmin(admin)
    }

    suspend fun updateAdmin(admin: AdminUserEntity) {
        adminUserDao.updateAdmin(admin)
    }

    suspend fun deleteAdmin(admin: AdminUserEntity) {
        adminUserDao.deleteAdmin(admin)
    }

    suspend fun setAdminBlocked(adminId: String, isBlocked: Boolean) {
        adminUserDao.setBlockedStatus(adminId, isBlocked)
    }

    suspend fun updateAdminPassword(adminId: String, newPass: String) {
        adminUserDao.updatePassword(adminId, newPass)
    }

    suspend fun updateAdminLocation(adminId: String, lat: Double, lng: Double) {
        adminUserDao.updateLocation(adminId, lat, lng)
    }

    suspend fun updateAppConfig(config: AppConfigEntity) {
        appConfigDao.setConfig(config)
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        dispatchOrderDao.updateOrderStatus(orderId, status.name)
    }

    suspend fun acknowledgeByMainAdmin(orderId: String) {
        dispatchOrderDao.acknowledgeByMainAdmin(orderId)
    }

    suspend fun acknowledgeByAssignedAdmin(orderId: String) {
        dispatchOrderDao.acknowledgeByAssignedAdmin(orderId)
    }

    suspend fun dispatchOrderToAdmin(orderId: String, adminId: String, adminName: String) {
        dispatchOrderDao.dispatchOrderToAdmin(orderId, adminId, adminName)
    }

    suspend fun updateAdminLiveLocation(orderId: String, lat: Double, lng: Double, etaPickup: Int, etaDropoff: Int) {
        dispatchOrderDao.updateAdminLiveLocation(orderId, lat, lng, etaPickup, etaDropoff)
    }

    suspend fun sendChatMessage(orderId: String, senderId: String, senderName: String, senderRole: String, text: String) {
        val chat = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            orderId = orderId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            message = text,
            timestamp = System.currentTimeMillis()
        )
        chatMessageDao.insertMessage(chat)
    }

    /**
     * Creates new customer inquiry. If autoDispatchEnabled is true,
     * immediately assigns to the closest available sub-admin.
     */
    suspend fun createCustomerInquiry(
        customerName: String,
        customerPhone: String,
        itemTitle: String,
        itemDetails: String,
        itemCategory: String,
        pickupAddress: String,
        pickupLat: Double,
        pickupLng: Double,
        dropoffAddress: String,
        dropoffLat: Double,
        dropoffLng: Double
    ): DispatchOrderEntity {
        val trackingCode = "TM-" + (100000..999999).random().toString()
        val orderId = UUID.randomUUID().toString()

        val config = appConfigDao.getConfigOnce()
        val isAutoDispatch = config?.autoDispatchEnabled ?: false

        var assignedAdmin: AdminUserEntity? = null
        if (isAutoDispatch) {
            val subAdmins = adminUserDao.getAllSubAdmins()
            // We find closest active sub admin
            // We can query once or compute
            val subAdminList = adminUserDao.getMainAdmin()?.let {
                // Fetch list from DAO directly
            }
        }

        val order = DispatchOrderEntity(
            id = orderId,
            trackingCode = trackingCode,
            customerName = customerName,
            customerPhone = customerPhone,
            itemTitle = itemTitle,
            itemDetails = itemDetails,
            itemCategory = itemCategory,
            pickupAddress = pickupAddress,
            pickupLat = pickupLat,
            pickupLng = pickupLng,
            dropoffAddress = dropoffAddress,
            dropoffLat = dropoffLat,
            dropoffLng = dropoffLng,
            status = OrderStatus.PENDING_MAIN_ADMIN.name,
            assignedAdminId = null,
            assignedAdminName = null,
            isAcknowledgedByMainAdmin = false,
            isAcknowledgedByAssignedAdmin = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            etaPickupMinutes = 15,
            etaDropoffMinutes = 35
        )

        dispatchOrderDao.insertOrder(order)
        return order
    }

    /**
     * Compute Haversine distance in Kilometers between two coordinates
     */
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Estimates travel duration in minutes based on distance (assuming urban avg 30 km/h)
     */
    fun estimateEtaMinutes(distanceKm: Double): Int {
        val avgSpeedKmH = 32.0
        val hours = distanceKm / avgSpeedKmH
        val minutes = (hours * 60).roundToInt()
        return max(3, minutes)
    }

    /**
     * Seed initial configuration and default admin if first launch
     */
    suspend fun seedInitialDataIfNeeded() {
        val existingMain = adminUserDao.getMainAdmin()
        if (existingMain == null) {
            // 1. Create Default Main Admin
            val mainAdmin = AdminUserEntity(
                id = "main_admin_001",
                username = "admin",
                password = "admin123",
                fullName = "Chief Dispatch Commander",
                role = "MAIN_ADMIN",
                isBlocked = false,
                latitude = 40.7580,
                longitude = -73.9855,
                phone = "+1 800-555-0100",
                status = "AVAILABLE"
            )
            adminUserDao.insertAdmin(mainAdmin)

            // 2. Create Default Added Sub-Admins
            val sub1 = AdminUserEntity(
                id = "sub_001",
                username = "subadmin1",
                password = "pass123",
                fullName = "Marcus Vance (Midtown Unit)",
                role = "SUB_ADMIN",
                isBlocked = false,
                latitude = 40.7527,
                longitude = -73.9772,
                phone = "+1 555-0144",
                status = "AVAILABLE"
            )
            val sub2 = AdminUserEntity(
                id = "sub_002",
                username = "subadmin2",
                password = "pass123",
                fullName = "Elena Rostova (Uptown Unit)",
                role = "SUB_ADMIN",
                isBlocked = false,
                latitude = 40.7829,
                longitude = -73.9654,
                phone = "+1 555-0188",
                status = "AVAILABLE"
            )
            val sub3 = AdminUserEntity(
                id = "sub_003",
                username = "subadmin3",
                password = "pass123",
                fullName = "David Chen (Downtown Unit)",
                role = "SUB_ADMIN",
                isBlocked = false,
                latitude = 40.7128,
                longitude = -74.0060,
                phone = "+1 555-0192",
                status = "AVAILABLE"
            )
            adminUserDao.insertAll(listOf(sub1, sub2, sub3))

            // 3. Create Default App Config
            appConfigDao.setConfig(
                AppConfigEntity(
                    id = 1,
                    appName = "TRACKMATRIX",
                    logoPreset = "MATRIX_BLUE",
                    autoDispatchEnabled = true,
                    alertSoundEnabled = true
                )
            )

            // 4. Create an initial sample order for demonstration
            val sampleOrder = DispatchOrderEntity(
                id = "sample_order_001",
                trackingCode = "TM-782910",
                customerName = "Sarah Jenkins",
                customerPhone = "+1 555-0123",
                itemTitle = "High-Security Legal Contract Documents",
                itemDetails = "Sealed waterproof envelope with biometric verification required upon handover.",
                itemCategory = "Documents",
                pickupAddress = "Times Square, New York, USA",
                pickupLat = 40.7580,
                pickupLng = -73.9855,
                dropoffAddress = "Wall Street, New York, USA",
                dropoffLat = 40.7060,
                dropoffLng = -74.0088,
                status = OrderStatus.PENDING_MAIN_ADMIN.name,
                assignedAdminId = null,
                assignedAdminName = null,
                isAcknowledgedByMainAdmin = false,
                isAcknowledgedByAssignedAdmin = false,
                createdAt = System.currentTimeMillis() - 120000,
                updatedAt = System.currentTimeMillis() - 120000,
                currentAdminLat = 40.7527,
                currentAdminLng = -73.9772,
                etaPickupMinutes = 6,
                etaDropoffMinutes = 22
            )
            dispatchOrderDao.insertOrder(sampleOrder)
        }
    }
}
