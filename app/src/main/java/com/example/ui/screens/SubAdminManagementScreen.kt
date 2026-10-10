package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.data.local.AdminUserEntity
import com.example.data.model.LocationPoint
import com.example.ui.components.AdminStatusBadge
import com.example.ui.theme.MatrixAlarmCrimson
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubAdminManagementScreen(
    subAdmins: List<AdminUserEntity>,
    onAddSubAdmin: (fullName: String, username: String, pass: String, phone: String, lat: Double, lng: Double) -> Unit,
    onToggleBlockAdmin: (adminId: String, isBlocked: Boolean) -> Unit,
    onChangeAdminPassword: (adminId: String, newPass: String) -> Unit,
    onDeleteAdmin: (AdminUserEntity) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAdminPasswordFor by remember { mutableStateOf<AdminUserEntity?>(null) }
    var adminToDelete by remember { mutableStateOf<AdminUserEntity?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DISPATCH FLEET MANAGEMENT",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Add unlimited sub-admins with limited privileges",
                            fontSize = 11.sp,
                            color = MatrixCyanPrimary
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF070D1E)
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "REGISTERED SUB-ADMINS (${subAdmins.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF90A4CE),
                        letterSpacing = 1.sp
                    )
                }

                items(subAdmins, key = { it.id }) { admin ->
                    SubAdminItemCard(
                        admin = admin,
                        onToggleBlock = { onToggleBlockAdmin(admin.id, !admin.isBlocked) },
                        onChangePassword = { editingAdminPasswordFor = admin },
                        onDelete = { adminToDelete = admin }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MatrixCyanPrimary,
            contentColor = Color(0xFF001F2A),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_sub_admin_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Admin")
                Spacer(modifier = Modifier.width(6.dp))
                Text("ADD ADMIN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Dialog: Add Sub-Admin
        if (showAddDialog) {
            AddSubAdminDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, username, pass, phone, lat, lng ->
                    onAddSubAdmin(name, username, pass, phone, lat, lng)
                    showAddDialog = false
                }
            )
        }

        // Dialog: Change Password
        editingAdminPasswordFor?.let { admin ->
            ChangeAdminPasswordDialog(
                adminName = admin.fullName,
                onDismiss = { editingAdminPasswordFor = null },
                onConfirm = { newPass ->
                    onChangeAdminPassword(admin.id, newPass)
                    editingAdminPasswordFor = null
                }
            )
        }

        // Dialog: Confirm Delete
        adminToDelete?.let { admin ->
            AlertDialog(
                onDismissRequest = { adminToDelete = null },
                title = { Text("Delete Sub-Admin?", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to permanently remove '${admin.fullName}' (${admin.username}) from TRACKMATRIX dispatch fleet?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteAdmin(admin)
                            adminToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixAlarmCrimson)
                    ) {
                        Text("Delete Admin")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { adminToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SubAdminItemCard(
    admin: AdminUserEntity,
    onToggleBlock: () -> Unit,
    onChangePassword: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_card_${admin.username}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1730)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (admin.isBlocked) MatrixAlarmCrimson.copy(alpha = 0.5f) else Color(0xFF1B2C56)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (admin.isBlocked) MatrixAlarmCrimson.copy(alpha = 0.2f) else MatrixCyanPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (admin.isBlocked) MatrixAlarmCrimson else MatrixCyanPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = admin.fullName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Login: ${admin.username}",
                            fontSize = 11.sp,
                            color = MatrixCyanPrimary
                        )
                    }
                }

                AdminStatusBadge(status = admin.status, isBlocked = admin.isBlocked)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = admin.phone, fontSize = 11.sp, color = Color(0xFF90A4CE))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MatrixEmeraldTertiary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.4f, %.4f", admin.latitude, admin.longitude),
                        fontSize = 10.sp,
                        color = Color(0xFF90A4CE)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Change Password
                OutlinedButton(
                    onClick = onChangePassword,
                    modifier = Modifier.height(34.dp).testTag("change_pass_btn_${admin.username}")
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Password", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Block / Unblock
                Button(
                    onClick = onToggleBlock,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (admin.isBlocked) MatrixEmeraldTertiary else MatrixWarningAmber,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.height(34.dp).testTag("block_toggle_btn_${admin.username}")
                ) {
                    Icon(
                        imageVector = if (admin.isBlocked) Icons.Default.Check else Icons.Default.Block,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (admin.isBlocked) "Unblock" else "Block", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Delete
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(34.dp).testTag("delete_admin_btn_${admin.username}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MatrixAlarmCrimson, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun AddSubAdminDialog(
    onDismiss: () -> Unit,
    onConfirm: (fullName: String, username: String, pass: String, phone: String, lat: Double, lng: Double) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("pass" + (100..999).random()) }
    var phone by remember { mutableStateOf("+1 555-0" + (100..999).random()) }
    var selectedLocationPreset by remember { mutableStateOf(LocationPoint.PRESETS.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Dispatched Sub-Admin", fontWeight = FontWeight.Bold, color = MatrixCyanPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name (e.g. Leo Cruz)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_admin_fullname")
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_admin_username")
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Generated Initial Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_admin_password")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Initial Location: ${selectedLocationPreset.address}",
                    fontSize = 11.sp,
                    color = MatrixEmeraldTertiary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && username.isNotBlank() && password.isNotBlank()) {
                        onConfirm(
                            fullName.trim(),
                            username.trim(),
                            password.trim(),
                            phone.trim(),
                            selectedLocationPreset.latitude,
                            selectedLocationPreset.longitude
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MatrixCyanPrimary, contentColor = Color(0xFF001F2A)),
                modifier = Modifier.testTag("confirm_add_admin_btn")
            ) {
                Text("Create Admin Credentials", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ChangeAdminPasswordDialog(
    adminName: String,
    onDismiss: () -> Unit,
    onConfirm: (newPass: String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password for $adminName", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Enter the new password for this sub-admin:", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_password_field")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.isNotBlank()) {
                        onConfirm(newPassword.trim())
                    }
                },
                modifier = Modifier.testTag("save_new_password_btn")
            ) {
                Text("Update Password")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
