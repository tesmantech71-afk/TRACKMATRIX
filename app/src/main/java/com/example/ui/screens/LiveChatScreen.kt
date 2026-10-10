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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DispatchOrderEntity
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveChatScreen(
    order: DispatchOrderEntity,
    messages: List<ChatMessageEntity>,
    currentUserId: String,
    currentUserName: String,
    currentUserRole: String,
    onSendMessage: (text: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val quickReplies = listOf(
        "I'm at the pickup point",
        "Item securely collected",
        "En route now, 5 mins away",
        "Heavy traffic update",
        "Arrived at drop-off location"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070D1E))
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "LIVE ORDER CHAT • ${order.trackingCode}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = MatrixCyanPrimary
                    )
                    Text(
                        text = "Client: ${order.customerName} ↔ Courier: ${order.assignedAdminName ?: "Sub-Admin"}",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070D1E))
        )

        // Chat messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Direct channel established. Send a message to coordinate pickup or delivery details.",
                            fontSize = 12.sp,
                            color = Color(0xFF90A4CE),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                val isMe = msg.senderId == currentUserId || msg.senderRole == currentUserRole
                ChatBubble(message = msg, isMe = isMe)
            }
        }

        // Quick replies chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickReplies) { reply ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF132247))
                        .border(1.dp, Color(0xFF233B73), RoundedCornerShape(16.dp))
                        .clickable { onSendMessage(reply) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = reply, fontSize = 11.sp, color = Color.White)
                }
            }
        }

        // Input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D1730))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Type coordinate message...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(20.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(messageText.trim())
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MatrixCyanPrimary)
                    .testTag("send_chat_message_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color(0xFF001F2A),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessageEntity,
    isMe: Boolean,
    modifier: Modifier = Modifier
) {
    val timeFormatted = remember(message.timestamp) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    val bubbleColor = if (isMe) MatrixCyanPrimary else Color(0xFF16254A)
    val textColor = if (isMe) Color(0xFF001F2A) else Color.White

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = "${message.senderName} (${message.senderRole})",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isMe) MatrixCyanPrimary else Color(0xFF90A4CE),
                modifier = Modifier.padding(bottom = 2.dp)
            )

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isMe) 14.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 14.dp
                        )
                    )
                    .background(bubbleColor)
                    .padding(horizontal = 12.dp, vertical = 9.dp)
            ) {
                Text(
                    text = message.message,
                    fontSize = 13.sp,
                    color = textColor,
                    lineHeight = 18.sp
                )
            }

            Text(
                text = timeFormatted,
                fontSize = 9.sp,
                color = Color(0xFF5D719E),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun Modifier.widthIn(max: androidx.compose.ui.unit.Dp): Modifier = this.then(
    Modifier.width(androidx.compose.ui.unit.min(max, 320.dp))
)
