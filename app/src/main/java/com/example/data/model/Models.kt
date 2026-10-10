package com.example.data.model

enum class UserRole {
    MAIN_ADMIN,
    SUB_ADMIN,
    CUSTOMER
}

enum class OrderStatus(val label: String) {
    PENDING_MAIN_ADMIN("Pending Dispatch"),
    DISPATCHED("Dispatched to Admin"),
    ACCEPTED("Accepted by Admin"),
    EN_ROUTE_PICKUP("En Route to Pickup"),
    ITEM_COLLECTED("Item Collected"),
    IN_TRANSIT("In Transit to Drop-off"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}

data class LocationPoint(
    val address: String,
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        // Presets worldwide for instant selection & testing
        val PRESETS = listOf(
            LocationPoint("Times Square, New York, USA", 40.7580, -73.9855),
            LocationPoint("Central Park West, New York, USA", 40.785091, -73.968285),
            LocationPoint("Wall Street, New York, USA", 40.706036, -74.008827),
            LocationPoint("Oxford Street, London, UK", 51.5151, -0.1419),
            LocationPoint("Tower Bridge, London, UK", 51.5055, -0.0754),
            LocationPoint("Shinjuku Station, Tokyo, Japan", 35.6895, 139.7005),
            LocationPoint("Brandenburg Gate, Berlin, Germany", 52.5163, 13.3777),
            LocationPoint("Sydney Harbour, Sydney, Australia", -33.8568, 151.2153),
            LocationPoint("Burj Khalifa, Dubai, UAE", 25.1972, 55.2744),
            LocationPoint("Champs-Élysées, Paris, France", 48.8698, 2.3075)
        )
    }
}
