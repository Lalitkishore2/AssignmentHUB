package com.assignmenthub.ui.screens.teacher

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignmenthub.model.SampleCourses
import com.assignmenthub.ui.components.CourseBadge
import com.assignmenthub.ui.viewmodel.GradingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAssignmentScreen(
    viewModel: GradingViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val courses = SampleCourses.allCourses
    var selectedCourse by remember { mutableStateOf(courses.first()) }
    var courseDropdownExpanded by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("Next Friday, 11:59 PM") }
    var points by remember { mutableStateOf("100") }

    var showCreatedDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Create New Assignment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Assignment Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Course Selector
                        ExposedDropdownMenuBox(
                            expanded = courseDropdownExpanded,
                            onExpandedChange = { courseDropdownExpanded = !courseDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = "${selectedCourse.code} — ${selectedCourse.name}",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Course") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseDropdownExpanded)
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = courseDropdownExpanded,
                                onDismissRequest = { courseDropdownExpanded = false }
                            ) {
                                courses.forEach { course ->
                                    DropdownMenuItem(
                                        text = {
                                            Row {
                                                CourseBadge(code = course.code, color = course.color)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(course.name)
                                            }
                                        },
                                        onClick = {
                                            selectedCourse = course
                                            courseDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Title
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Assignment Title") },
                            placeholder = { Text("e.g. Lab 5: Dynamic Programming") },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Description
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Instructions & Requirements") },
                            placeholder = { Text("Detailed prompt, required deliverables, submission rules...") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Due Date & Points Row
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = dueDate,
                                onValueChange = { dueDate = it },
                                label = { Text("Due Date & Time") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.5f)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            OutlinedTextField(
                                value = points,
                                onValueChange = { if (it.all { ch -> ch.isDigit() }) points = it },
                                label = { Text("Points") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Publish Button
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                val isValid = title.isNotBlank() && description.isNotBlank()

                Button(
                    onClick = {
                        viewModel.createAssignment(
                            courseId = selectedCourse.id,
                            courseCode = selectedCourse.code,
                            courseName = selectedCourse.name,
                            title = title.trim(),
                            description = description.trim(),
                            dueDateFormatted = dueDate.trim(),
                            points = points.toIntOrNull() ?: 100
                        ) {
                            showCreatedDialog = true
                        }
                    },
                    enabled = isValid,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publish Assignment to Class",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    if (showCreatedDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreatedDialog = false
                onNavigateBack()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Assignment Published!") },
            text = { Text("\"$title\" has been published for ${selectedCourse.code}. All enrolled students can now view and submit.") },
            confirmButton = {
                Button(
                    onClick = {
                        showCreatedDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }
}
