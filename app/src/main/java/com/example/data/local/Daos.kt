package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminUserDao {
    @Query("SELECT * FROM admin_users ORDER BY role ASC, fullName ASC")
    fun getAllAdmins(): Flow<List<AdminUserEntity>>

    @Query("SELECT * FROM admin_users WHERE role = 'SUB_ADMIN' ORDER BY fullName ASC")
    fun getAllSubAdmins(): Flow<List<AdminUserEntity>>

    @Query("SELECT * FROM admin_users WHERE id = :id LIMIT 1")
    suspend fun getAdminById(id: String): AdminUserEntity?

    @Query("SELECT * FROM admin_users WHERE username = :username LIMIT 1")
    suspend fun getAdminByUsername(username: String): AdminUserEntity?

    @Query("SELECT * FROM admin_users WHERE role = 'MAIN_ADMIN' LIMIT 1")
    suspend fun getMainAdmin(): AdminUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: AdminUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(admins: List<AdminUserEntity>)

    @Update
    suspend fun updateAdmin(admin: AdminUserEntity)

    @Delete
    suspend fun deleteAdmin(admin: AdminUserEntity)

    @Query("UPDATE admin_users SET isBlocked = :isBlocked WHERE id = :adminId")
    suspend fun setBlockedStatus(adminId: String, isBlocked: Boolean)

    @Query("UPDATE admin_users SET password = :newPassword WHERE id = :adminId")
    suspend fun updatePassword(adminId: String, newPassword: String)

    @Query("UPDATE admin_users SET latitude = :lat, longitude = :lng, lastActive = :time WHERE id = :adminId")
    suspend fun updateLocation(adminId: String, lat: Double, lng: Double, time: Long = System.currentTimeMillis())
}

@Dao
interface DispatchOrderDao {
    @Query("SELECT * FROM dispatch_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<DispatchOrderEntity>>

    @Query("SELECT * FROM dispatch_orders WHERE assignedAdminId = :adminId ORDER BY createdAt DESC")
    fun getOrdersForAdmin(adminId: String): Flow<List<DispatchOrderEntity>>

    @Query("SELECT * FROM dispatch_orders WHERE id = :id LIMIT 1")
    fun getOrderByIdFlow(id: String): Flow<DispatchOrderEntity?>

    @Query("SELECT * FROM dispatch_orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: String): DispatchOrderEntity?

    @Query("SELECT * FROM dispatch_orders WHERE trackingCode = :trackingCode LIMIT 1")
    suspend fun getOrderByTrackingCode(trackingCode: String): DispatchOrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: DispatchOrderEntity)

    @Update
    suspend fun updateOrder(order: DispatchOrderEntity)

    @Query("UPDATE dispatch_orders SET isAcknowledgedByMainAdmin = 1 WHERE id = :orderId")
    suspend fun acknowledgeByMainAdmin(orderId: String)

    @Query("UPDATE dispatch_orders SET isAcknowledgedByAssignedAdmin = 1 WHERE id = :orderId")
    suspend fun acknowledgeByAssignedAdmin(orderId: String)

    @Query("UPDATE dispatch_orders SET status = :status, updatedAt = :time WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, time: Long = System.currentTimeMillis())

    @Query("UPDATE dispatch_orders SET assignedAdminId = :adminId, assignedAdminName = :adminName, status = 'DISPATCHED', isAcknowledgedByAssignedAdmin = 0, updatedAt = :time WHERE id = :orderId")
    suspend fun dispatchOrderToAdmin(orderId: String, adminId: String, adminName: String, time: Long = System.currentTimeMillis())

    @Query("UPDATE dispatch_orders SET currentAdminLat = :lat, currentAdminLng = :lng, etaPickupMinutes = :etaPickup, etaDropoffMinutes = :etaDropoff, updatedAt = :time WHERE id = :orderId")
    suspend fun updateAdminLiveLocation(orderId: String, lat: Double, lng: Double, etaPickup: Int, etaDropoff: Int, time: Long = System.currentTimeMillis())
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE orderId = :orderId ORDER BY timestamp ASC")
    fun getMessagesForOrder(orderId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)
}

@Dao
interface AppConfigDao {
    @Query("SELECT * FROM app_config WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<AppConfigEntity?>

    @Query("SELECT * FROM app_config WHERE id = 1 LIMIT 1")
    suspend fun getConfigOnce(): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setConfig(config: AppConfigEntity)
}
