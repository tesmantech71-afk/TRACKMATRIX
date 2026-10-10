package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MatrixAlarmCrimson
import com.example.ui.theme.MatrixCyanPrimary
import com.example.ui.theme.MatrixEmeraldTertiary
import com.example.ui.theme.MatrixWarningAmber

@Composable
fun LoginScreen(
    appName: String,
    onLoginAdmin: (username: String, pass: String) -> Unit,
    onLoginCustomer: (customerName: String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Main Admin, 1: Sub Admin, 2: Customer
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin123") }
    var customerName by remember { mutableStateOf("Sarah Jenkins") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070D1E),
                        Color(0xFF0F1B38),
                        Color(0xFF070D1E)
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // High tech Matrix Emblem
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MatrixCyanPrimary.copy(alpha = 0.12f))
                    .border(2.dp, MatrixCyanPrimary, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "Emblem",
                    tint = MatrixCyanPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = appName.ifBlank { "TRACKMATRIX" },
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
            )

            Text(
                text = "GLOBAL DISPATCH & ROUTE MATRIX GATEWAY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MatrixCyanPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Role selection tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF111F42),
                contentColor = MatrixCyanPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF22356B), RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        username = "admin"
                        password = "admin123"
                    },
                    text = { Text("Main Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_main_admin")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        username = "subadmin1"
                        password = "pass123"
                    },
                    text = { Text("Sub-Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_sub_admin")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Customer", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_customer")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0E1A38)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3264))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (selectedTab == 0 || selectedTab == 1) {
                        Text(
                            text = if (selectedTab == 0) "Main Admin Access Portal" else "Dispatched Admin Access Portal",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) MatrixCyanPrimary else MatrixWarningAmber
                        )
                        Text(
                            text = if (selectedTab == 0)
                                "Default credentials: admin / admin123 (permanent change in settings)"
                            else
                                "Login with credentials assigned by Main Admin",
                            fontSize = 11.sp,
                            color = Color(0xFF90A4CE)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (selectedTab == 0) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                    contentDescription = "User Icon",
                                    tint = MatrixCyanPrimary
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_username_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password Icon",
                                    tint = MatrixCyanPrimary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Password Visibility"
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_field")
                        )

                        if (!errorMessage.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage,
                                color = MatrixAlarmCrimson,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onLoginAdmin(username.trim(), password) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == 0) MatrixCyanPrimary else MatrixWarningAmber,
                                contentColor = Color(0xFF001F2A)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_login_btn")
                        ) {
                            Text(
                                text = if (selectedTab == 0) "ENTER COMMAND PANEL" else "ENTER DISPATCH PANEL",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    } else {
                        // Customer Mode
                        Text(
                            text = "Customer Dispatch & Inquiry Portal",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MatrixEmeraldTertiary
                        )
                        Text(
                            text = "Request item pickup, select dropoff address and monitor live courier dispatch",
                            fontSize = 11.sp,
                            color = Color(0xFF90A4CE)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Your Full Name") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MatrixEmeraldTertiary)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("customer_name_field")
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onLoginCustomer(customerName.ifBlank { "Customer" }) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MatrixEmeraldTertiary,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_customer_entry_btn")
                        ) {
                            Text(
                                text = "OPEN CLIENT INQUIRY PORTAL",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Demo Switchers
            Text(
                text = "FAST TESTING ONE-CLICK PRESETS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D719E),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedTab = 0
                        onLoginAdmin("admin", "admin123")
                    },
                    modifier = Modifier.weight(1f).testTag("quick_main_admin_btn")
                ) {
                    Text("Main Admin", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = {
                        selectedTab = 1
                        onLoginAdmin("subadmin1", "pass123")
                    },
                    modifier = Modifier.weight(1f).testTag("quick_sub_admin_btn")
                ) {
                    Text("Sub Admin", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = {
                        selectedTab = 2
                        onLoginCustomer("Sarah Jenkins")
                    },
                    modifier = Modifier.weight(1f).testTag("quick_customer_btn")
                ) {
                    Text("Customer", fontSize = 11.sp)
                }
            }
        }
    }
}
