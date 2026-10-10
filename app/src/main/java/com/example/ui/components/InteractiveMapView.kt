package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MatrixAlarmCrimson
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber

@Composable
fun InteractiveMapView(
    adminLat: Double,
    adminLng: Double,
    adminName: String,
    pickupLat: Double,
    pickupLng: Double,
    pickupAddress: String,
    dropoffLat: Double,
    dropoffLng: Double,
    dropoffAddress: String,
    etaPickupMinutes: Int,
    etaDropoffMinutes: Int,
    isSimulatingMovement: Boolean,
    onToggleSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseSize by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 42f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_size"
    )

    Box(
        modifier = modifier
            .testTag("interactive_map_container")
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .background(Color(0xFF091124))
    ) {
        // Canvas Interactive Vector Map
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.6f, 3.5f)
                        panOffsetX += pan.x
                        panOffsetY += pan.y
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f + panOffsetX
            val centerY = height / 2f + panOffsetY

            // Draw Matrix Grid Network
            val gridSize = 40f * zoomScale
            var x = panOffsetX % gridSize
            while (x < width) {
                drawLine(
                    color = Color(0xFF132247),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += gridSize
            }
            var y = panOffsetY % gridSize
            while (y < height) {
                drawLine(
                    color = Color(0xFF132247),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += gridSize
            }

            // Radar concentric rings around center
            for (r in 1..4) {
                drawCircle(
                    color = Color(0xFF182D5C).copy(alpha = 0.4f),
                    radius = r * 80f * zoomScale,
                    center = Offset(centerX, centerY),
                    style = Stroke(
                        width = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }

            // Map coordinates projection relative to center
            // (lat offset maps to Y, lng offset maps to X)
            val coordScale = 12000f * zoomScale

            val avgLat = (pickupLat + dropoffLat) / 2.0
            val avgLng = (pickupLng + dropoffLng) / 2.0

            fun toScreenOffset(lat: Double, lng: Double): Offset {
                val dx = (lng - avgLng) * coordScale
                val dy = -(lat - avgLat) * coordScale // North is up
                return Offset((centerX + dx).toFloat(), (centerY + dy).toFloat())
            }

            val adminPos = toScreenOffset(adminLat, adminLng)
            val pickupPos = toScreenOffset(pickupLat, pickupLng)
            val dropoffPos = toScreenOffset(dropoffLat, dropoffLng)

            // Draw Route Lines (Admin -> Pickup -> Dropoff)
            drawLine(
                color = MatrixCyanPrimary.copy(alpha = 0.8f),
                start = adminPos,
                end = pickupPos,
                strokeWidth = 4f * zoomScale,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
            )
            drawLine(
                color = MatrixEmeraldTertiary.copy(alpha = 0.8f),
                start = pickupPos,
                end = dropoffPos,
                strokeWidth = 4f * zoomScale,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
            )

            // 1. Dropoff Destination Marker (Blue)
            drawCircle(
                color = Color(0xFF2979FF).copy(alpha = 0.3f),
                radius = 22f * zoomScale,
                center = dropoffPos
            )
            drawCircle(
                color = Color(0xFF2979FF),
                radius = 12f * zoomScale,
                center = dropoffPos
            )
            drawCircle(
                color = Color.White,
                radius = 5f * zoomScale,
                center = dropoffPos
            )

            // 2. Pickup Origin Marker (Green Glowing Pulse)
            drawCircle(
                color = MatrixEmeraldTertiary.copy(alpha = 0.35f),
                radius = pulseSize * zoomScale,
                center = pickupPos
            )
            drawCircle(
                color = MatrixEmeraldTertiary,
                radius = 14f * zoomScale,
                center = pickupPos
            )
            drawCircle(
                color = Color.White,
                radius = 6f * zoomScale,
                center = pickupPos
            )

            // 3. Admin / Courier Live Vehicle Marker (Cyan glowing with bearing)
            drawCircle(
                color = MatrixCyanPrimary.copy(alpha = 0.4f),
                radius = 28f * zoomScale,
                center = adminPos
            )
            drawCircle(
                color = MatrixCyanPrimary,
                radius = 16f * zoomScale,
                center = adminPos
            )
            drawCircle(
                color = Color(0xFF001F2A),
                radius = 8f * zoomScale,
                center = adminPos
            )
        }

        // Overlay: Top Navigation HUD Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopCenter),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0B1630).copy(alpha = 0.92f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LIVE DISPATCH RADAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = MatrixCyanPrimary
                        )
                        Text(
                            text = "Courier: $adminName",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MatrixEmeraldTertiary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ETA Pickup: ${etaPickupMinutes}m",
                                color = MatrixEmeraldTertiary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF2979FF).copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Dropoff: ${etaDropoffMinutes}m",
                                color = Color(0xFF82B1FF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Pickup Location",
                        tint = MatrixEmeraldTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pickupAddress,
                        fontSize = 11.sp,
                        color = Color(0xFFB0BEC5),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Overlay: Map Controls (Zoom & Recenter)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
        ) {
            IconButton(
                onClick = { zoomScale = (zoomScale * 1.25f).coerceAtMost(3.5f) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF14244D))
                    .testTag("zoom_in_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White)
            }
            Spacer(modifier = Modifier.height(6.dp))
            IconButton(
                onClick = { zoomScale = (zoomScale / 1.25f).coerceAtLeast(0.6f) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF14244D))
                    .testTag("zoom_out_btn")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White)
            }
            Spacer(modifier = Modifier.height(6.dp))
            IconButton(
                onClick = {
                    panOffsetX = 0f
                    panOffsetY = 0f
                    zoomScale = 1f
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF14244D))
                    .testTag("recenter_btn")
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Recenter", tint = MatrixCyanPrimary)
            }
        }

        // Overlay: Bottom Action Toolbar (Google Maps launch + Live Simulation)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Live GPS simulation toggle
            Button(
                onClick = onToggleSimulation,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSimulatingMovement) MatrixWarningAmber else Color(0xFF1E356A)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .testTag("simulate_gps_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Simulate Movement",
                    tint = if (isSimulatingMovement) Color.Black else Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isSimulatingMovement) "Simulating GPS" else "Simulate Live GPS",
                    fontSize = 11.sp,
                    color = if (isSimulatingMovement) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Google Maps Launcher
            Button(
                onClick = {
                    launchGoogleMaps(context, pickupLat, pickupLng, pickupAddress)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixEmeraldTertiary,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .testTag("launch_google_maps_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = "Open in Google Maps",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Google Maps",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun launchGoogleMaps(context: Context, lat: Double, lng: Double, label: String) {
    try {
        // Build navigation intent to pickup location
        val uri = Uri.parse("google.navigation:q=$lat,$lng&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback to generic browser / maps viewer
            val geoUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(label)})")
            val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri)
            context.startActivity(fallbackIntent)
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Navigating to coordinates: $lat, $lng", Toast.LENGTH_SHORT).show()
    }
}
