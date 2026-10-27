package com.assignmenthub.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignmenthub.model.DemoUsers
import com.assignmenthub.model.User
import com.assignmenthub.model.UserRole

@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }

    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var majorOrTitleInput by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // App Branding Icon & Header
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AssignmentHub",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Online Assignment Submission & Grading System",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Auth Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Role Selector Tab
                    TabRow(
                        selectedTabIndex = if (selectedRole == UserRole.STUDENT) 0 else 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedRole == UserRole.STUDENT,
                            onClick = { selectedRole = UserRole.STUDENT },
                            text = { Text("Student Portal", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedRole == UserRole.TEACHER,
                            onClick = { selectedRole = UserRole.TEACHER },
                            text = { Text("Instructor Portal", fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (isSignUpMode) "Create ${if (selectedRole == UserRole.STUDENT) "Student" else "Instructor"} Account" else "Sign In to Your Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (errorMessage != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Registration Name Field
                    AnimatedVisibility(visible = isSignUpMode) {
                        Column {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it; errorMessage = null },
                                label = { Text("Full Name") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                                },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it; errorMessage = null },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it; errorMessage = null },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Registration Department / Major Field
                    AnimatedVisibility(visible = isSignUpMode) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = majorOrTitleInput,
                                onValueChange = { majorOrTitleInput = it; errorMessage = null },
                                label = { Text(if (selectedRole == UserRole.STUDENT) "Major / Department (e.g. B.Tech CSE)" else "Faculty Department / Title") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Work, contentDescription = null)
                                },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Action Button
                    Button(
                        onClick = {
                            if (emailInput.isBlank() || passwordInput.isBlank()) {
                                errorMessage = "Please enter both email and password."
                                return@Button
                            }
                            if (isSignUpMode && nameInput.isBlank()) {
                                errorMessage = "Please enter your full name."
                                return@Button
                            }

                            isLoading = true
                            val initials = if (isSignUpMode && nameInput.isNotBlank()) {
                                nameInput.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
                            } else if (selectedRole == UserRole.STUDENT) {
                                "AJ"
                            } else {
                                "SM"
                            }

                            val titleOrMajor = if (isSignUpMode && majorOrTitleInput.isNotBlank()) {
                                majorOrTitleInput.trim()
                            } else if (selectedRole == UserRole.STUDENT) {
                                "Computer Science & Engineering"
                            } else {
                                "Professor of Computer Science"
                            }

                            val name = if (isSignUpMode && nameInput.isNotBlank()) {
                                nameInput.trim()
                            } else if (emailInput.contains("alex", ignoreCase = true) || selectedRole == UserRole.STUDENT) {
                                "Alex Johnson"
                            } else {
                                "Dr. Sarah Mitchell"
                            }

                            val user = User(
                                id = "usr_${System.currentTimeMillis()}",
                                name = name,
                                email = emailInput.trim(),
                                role = selectedRole,
                                avatarInitials = if (initials.isBlank()) "U" else initials,
                                titleOrMajor = titleOrMajor
                            )

                            onLoginSuccess(user)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = if (isSignUpMode) "Complete Registration & Sign In" else "Sign In to Portal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Toggle Sign In vs Register
                    TextButton(
                        onClick = {
                            isSignUpMode = !isSignUpMode
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isSignUpMode) "Already have an account? Sign In" else "New User? Register New Account",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Demo Fill Options
            Text(
                text = "Demo Credentials Quick Access:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        selectedRole = UserRole.STUDENT
                        emailInput = DemoUsers.sampleStudent.email
                        passwordInput = "password123"
                        isSignUpMode = false
                        errorMessage = null
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Fill Demo Student", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        selectedRole = UserRole.TEACHER
                        emailInput = DemoUsers.sampleTeacher.email
                        passwordInput = "password123"
                        isSignUpMode = false
                        errorMessage = null
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Fill Demo Teacher", fontSize = 12.sp)
                }
            }
        }
    }
}
