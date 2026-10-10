package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_users")
data class AdminUserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val password: String,
    val fullName: String,
    val role: String, // "MAIN_ADMIN" or "SUB_ADMIN"
    val isBlocked: Boolean = false,
    val latitude: Double = 40.7580,
    val longitude: Double = -73.9855,
    val phone: String = "+1 555-0199",
    val status: String = "AVAILABLE", // "AVAILABLE", "BUSY", "OFFLINE"
    val lastActive: Long = System.currentTimeMillis()
)

@Entity(tableName = "dispatch_orders")
data class DispatchOrderEntity(
    @PrimaryKey
    val id: String,
    val trackingCode: String,
    val customerName: String,
    val customerPhone: String,
    val itemTitle: String,
    val itemDetails: String,
    val itemCategory: String,
    val pickupAddress: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropoffAddress: String,
    val dropoffLat: Double,
    val dropoffLng: Double,
    val status: String,
    val assignedAdminId: String? = null,
    val assignedAdminName: String? = null,
    val isAcknowledgedByMainAdmin: Boolean = false,
    val isAcknowledgedByAssignedAdmin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val currentAdminLat: Double? = null,
    val currentAdminLng: Double? = null,
    val etaPickupMinutes: Int = 12,
    val etaDropoffMinutes: Int = 28
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val orderId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String, // "CUSTOMER", "SUB_ADMIN", "MAIN_ADMIN"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_config")
data class AppConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val appName: String = "TRACKMATRIX",
    val logoPreset: String = "MATRIX_BLUE", // MATRIX_BLUE, CYBER_AMBER, STEALTH_DARK, EMERALD_RADAR
    val autoDispatchEnabled: Boolean = false,
    val alertSoundEnabled: Boolean = true
)
