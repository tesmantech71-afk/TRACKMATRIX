package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.theme.MatrixAlarmCrimson
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber

@Composable
fun OrderStatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (status) {
        OrderStatus.PENDING_MAIN_ADMIN -> MatrixAlarmCrimson to "Pending Main Admin"
        OrderStatus.DISPATCHED -> MatrixWarningAmber to "Dispatched to Admin"
        OrderStatus.ACCEPTED -> MatrixCyanPrimary to "Admin Accepted"
        OrderStatus.EN_ROUTE_PICKUP -> MatrixCyanPrimary to "En Route to Pickup"
        OrderStatus.ITEM_COLLECTED -> Color(0xFF9C27B0) to "Item Collected"
        OrderStatus.IN_TRANSIT -> Color(0xFF2979FF) to "In Transit to Dropoff"
        OrderStatus.DELIVERED -> MatrixEmeraldTertiary to "Delivered"
        OrderStatus.CANCELLED -> Color(0xFF757575) to "Cancelled"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .testTag("status_badge_${status.name}")
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (status == OrderStatus.PENDING_MAIN_ADMIN || status == OrderStatus.DISPATCHED || status == OrderStatus.IN_TRANSIT) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(alphaAnim)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AdminStatusBadge(
    status: String,
    isBlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val (color, text) = when {
        isBlocked -> MatrixAlarmCrimson to "BLOCKED"
        status == "AVAILABLE" -> MatrixEmeraldTertiary to "AVAILABLE"
        status == "BUSY" -> MatrixWarningAmber to "ON DISPATCH"
        else -> Color(0xFF8892B0) to "OFFLINE"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
