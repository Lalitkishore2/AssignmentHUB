package com.assignmenthub.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.assignmenthub.model.Assignment
import com.assignmenthub.model.AssignmentStatus
import com.assignmenthub.ui.components.CourseBadge
import com.assignmenthub.ui.components.StatMetricCard
import com.assignmenthub.ui.components.StatusBadge
import com.assignmenthub.ui.theme.CourseIndigo
import com.assignmenthub.ui.theme.StatusGraded
import com.assignmenthub.ui.theme.StatusOverdue
import com.assignmenthub.ui.theme.StatusPending
import com.assignmenthub.ui.theme.StatusSubmitted
import com.assignmenthub.ui.viewmodel.AssignmentViewModel

@Composable
fun StudentDashboardScreen(
    viewModel: AssignmentViewModel,
    onSelectAssignment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val assignments by viewModel.filteredAssignments.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val metrics by viewModel.metrics.collectAsState()
    val selectedCourseId by viewModel.selectedCourseId.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedAssignmentId by viewModel.selectedAssignmentId.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Academic Metrics Dashboard Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatMetricCard(
                    title = "Due Soon",
                    value = "${metrics.pendingCount}",
                    icon = Icons.Default.HourglassEmpty,
                    accentColor = StatusPending,
                    modifier = Modifier.width(145.dp)
                )
                StatMetricCard(
                    title = "Submitted",
                    value = "${metrics.submittedCount}",
                    icon = Icons.Default.CheckCircle,
                    accentColor = StatusSubmitted,
                    modifier = Modifier.width(145.dp)
                )
                StatMetricCard(
                    title = "Graded",
                    value = "${metrics.gradedCount}",
                    icon = Icons.Default.AssignmentTurnedIn,
                    accentColor = StatusGraded,
                    modifier = Modifier.width(145.dp)
                )
                StatMetricCard(
                    title = "Overdue",
                    value = "${metrics.overdueCount}",
                    icon = Icons.Default.Error,
                    accentColor = StatusOverdue,
                    modifier = Modifier.width(145.dp)
                )
            }
        }

        // 2. Search Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search assignments, courses, keywords...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 3. Course Filter Chips (Horizontal)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCourseId == null,
                    onClick = { viewModel.setCourseFilter(null) },
                    label = { Text("All Courses") },
                    shape = RoundedCornerShape(20.dp)
                )
                courses.forEach { course ->
                    FilterChip(
                        selected = selectedCourseId == course.id,
                        onClick = {
                            viewModel.setCourseFilter(
                                if (selectedCourseId == course.id) null else course.id
                            )
                        },
                        label = { Text(course.code) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = course.color.copy(alpha = 0.2f),
                            selectedLabelColor = course.color
                        )
                    )
                }
            }
        }

        // 4. Status Filter Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { viewModel.setStatusFilter(null) },
                    label = { Text("All Statuses") },
                    shape = RoundedCornerShape(20.dp)
                )
                AssignmentStatus.values().forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = {
                            viewModel.setStatusFilter(
                                if (selectedStatus == status) null else status
                            )
                        },
                        label = { Text(status.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // 5. Header count
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Assignments (${assignments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 6. Assignment Cards List
        if (assignments.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No assignments found",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Try adjusting your course or status filter",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(assignments, key = { it.id }) { assignment ->
                val isSelected = assignment.id == selectedAssignmentId
                val course = courses.find { it.id == assignment.courseId }
                val courseColor = course?.color ?: CourseIndigo

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            viewModel.selectAssignment(assignment.id)
                            onSelectAssignment(assignment.id)
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CourseBadge(
                                code = assignment.courseCode,
                                color = courseColor
                            )
                            StatusBadge(status = assignment.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = assignment.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = assignment.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Due: ${assignment.dueDateFormatted}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = if (assignment.status == AssignmentStatus.OVERDUE) {
                                    StatusOverdue
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "${assignment.points} pts",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
