package com.assignmenthub.ui.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignmenthub.model.User
import com.assignmenthub.model.UserRole

enum class AppNavDestination {
    ASSIGNMENTS,
    CREATE_OR_SUBMIT,
    GRADES,
    PROFILE;

    fun getNavLabel(role: UserRole): String {
        return when (this) {
            ASSIGNMENTS -> "Assignments"
            CREATE_OR_SUBMIT -> if (role == UserRole.STUDENT) "Submit" else "Create"
            GRADES -> if (role == UserRole.STUDENT) "Grades" else "Grading"
            PROFILE -> "Profile"
        }
    }

    fun getIcon(role: UserRole): ImageVector {
        return when (this) {
            ASSIGNMENTS -> Icons.Default.Assignment
            CREATE_OR_SUBMIT -> if (role == UserRole.STUDENT) Icons.Default.UploadFile else Icons.Default.AddBox
            GRADES -> if (role == UserRole.STUDENT) Icons.Default.Grade else Icons.Default.AssignmentTurnedIn
            PROFILE -> Icons.Default.Person
        }
    }
}

enum class WindowWidthClass {
    COMPACT,   // Standard and tall phones (< 600dp)
    MEDIUM,    // Foldables unfolded, landscape phones (600dp - 840dp)
    EXPANDED   // Tablets, desktop (> 840dp)
}

fun calculateWindowWidthClass(windowWidthDp: Dp): WindowWidthClass {
    return when {
        windowWidthDp < 600.dp -> WindowWidthClass.COMPACT
        windowWidthDp < 840.dp -> WindowWidthClass.MEDIUM
        else -> WindowWidthClass.EXPANDED
    }
}

@Composable
fun AdaptiveNavigationScaffold(
    windowWidthClass: WindowWidthClass,
    selectedDestination: AppNavDestination,
    onDestinationSelected: (AppNavDestination) -> Unit,
    currentUser: User,
    content: @Composable () -> Unit
) {
    val destinations = AppNavDestination.values()

    when (windowWidthClass) {
        WindowWidthClass.COMPACT -> {
            // Compact Phone: Bottom Navigation Bar
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    destinations.forEach { destination ->
                        val label = destination.getNavLabel(currentUser.role)
                        val icon = destination.getIcon(currentUser.role)
                        val isSelected = selectedDestination == destination

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { onDestinationSelected(destination) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        WindowWidthClass.MEDIUM -> {
            // Foldable / Landscape: Navigation Rail on Left
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    destinations.forEach { destination ->
                        val label = destination.getNavLabel(currentUser.role)
                        val icon = destination.getIcon(currentUser.role)

                        NavigationRailItem(
                            selected = selectedDestination == destination,
                            onClick = { onDestinationSelected(destination) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label
                                )
                            },
                            label = { Text(label, maxLines = 1) }
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }

        WindowWidthClass.EXPANDED -> {
            // Tablet: Permanent Navigation Drawer on Left
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier.width(260.dp)
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.avatarInitials,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentUser.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (currentUser.role == UserRole.STUDENT) "Student" else "Faculty",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        destinations.forEach { destination ->
                            val label = destination.getNavLabel(currentUser.role)
                            val icon = destination.getIcon(currentUser.role)

                            NavigationDrawerItem(
                                label = { Text(label) },
                                selected = selectedDestination == destination,
                                onClick = { onDestinationSelected(destination) },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    content()
                }
            }
        }
    }
}
