package com.assignmenthub.ui.screens.teacher

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignmenthub.model.Submission
import com.assignmenthub.ui.components.CourseBadge
import com.assignmenthub.ui.components.StatMetricCard
import com.assignmenthub.ui.theme.CourseIndigo
import com.assignmenthub.ui.theme.StatusGraded
import com.assignmenthub.ui.theme.StatusGradedContainer
import com.assignmenthub.ui.theme.StatusPending
import com.assignmenthub.ui.viewmodel.AssignmentViewModel
import com.assignmenthub.ui.viewmodel.GradingViewModel

@Composable
fun TeacherGradingQueueScreen(
    assignmentViewModel: AssignmentViewModel,
    gradingViewModel: GradingViewModel,
    onSelectSubmissionToGrade: (Submission) -> Unit,
    modifier: Modifier = Modifier
) {
    val assignments by assignmentViewModel.allAssignments.collectAsState()
    val courses by assignmentViewModel.courses.collectAsState()
    val pendingSubmissions by gradingViewModel.pendingSubmissions.collectAsState()
    val allSubmissions by gradingViewModel.allSubmissions.collectAsState()

    var selectedCourseFilter by remember { mutableStateOf<String?>(null) }

    val gradedSubmissions = allSubmissions.filter { it.isGraded }

    val filteredPending = if (selectedCourseFilter == null) {
        pendingSubmissions
    } else {
        pendingSubmissions.filter { sub ->
            val asg = assignments.find { it.id == sub.assignmentId }
            asg?.courseId == selectedCourseFilter
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Overview Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatMetricCard(
                    title = "Pending Queue",
                    value = "${pendingSubmissions.size}",
                    icon = Icons.Default.HourglassEmpty,
                    accentColor = StatusPending,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Graded Total",
                    value = "${gradedSubmissions.size}",
                    icon = Icons.Default.CheckCircle,
                    accentColor = StatusGraded,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section 2: Course Filter
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCourseFilter == null,
                    onClick = { selectedCourseFilter = null },
                    label = { Text("All Courses") },
                    shape = RoundedCornerShape(20.dp)
                )
                courses.forEach { course ->
                    FilterChip(
                        selected = selectedCourseFilter == course.id,
                        onClick = {
                            selectedCourseFilter = if (selectedCourseFilter == course.id) null else course.id
                        },
                        label = { Text(course.code) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Section 3: Submissions Awaiting Evaluation
        item {
            Text(
                text = "Awaiting Evaluation (${filteredPending.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (filteredPending.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentTurnedIn,
                            contentDescription = null,
                            tint = StatusGraded,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Grading Queue Clear!",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "All submitted assignments for this filter have been evaluated.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredPending, key = { it.id }) { submission ->
                val assignment = assignments.find { it.id == submission.assignmentId }
                val course = courses.find { it.id == assignment?.courseId }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = submission.studentAvatar,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = submission.studentName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Submitted: ${submission.submittedAtFormatted}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (course != null) {
                                CourseBadge(code = course.code, color = course.color)
                            }
                        }

                        if (assignment != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = assignment.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onSelectSubmissionToGrade(submission) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Grade,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Evaluate & Assign Score", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 4: Recently Graded Submissions
        if (gradedSubmissions.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recently Graded Submissions (${gradedSubmissions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(gradedSubmissions, key = { it.id }) { submission ->
                val assignment = assignments.find { it.id == submission.assignmentId }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = submission.studentName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = assignment?.title ?: "Assignment",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Surface(
                                color = StatusGradedContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${submission.score} / ${submission.maxScore} Pts",
                                    color = StatusGraded,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (!submission.teacherFeedback.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Feedback: \"${submission.teacherFeedback}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
