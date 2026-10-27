package com.assignmenthub

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.assignmenthub.model.Submission
import com.assignmenthub.model.UserRole
import com.assignmenthub.ui.adaptive.AdaptiveNavigationScaffold
import com.assignmenthub.ui.adaptive.AdaptiveTwoPaneLayout
import com.assignmenthub.ui.adaptive.AppNavDestination
import com.assignmenthub.ui.adaptive.WindowWidthClass
import com.assignmenthub.ui.adaptive.calculateWindowWidthClass
import com.assignmenthub.ui.components.TopBarWithRoleSwitch
import com.assignmenthub.ui.screens.auth.LoginScreen
import com.assignmenthub.ui.screens.student.AssignmentDetailScreen
import com.assignmenthub.ui.screens.student.StudentDashboardScreen
import com.assignmenthub.ui.screens.student.StudentGradesScreen
import com.assignmenthub.ui.screens.student.StudentManageSubmitScreen
import com.assignmenthub.ui.screens.student.StudentProfileScreen
import com.assignmenthub.ui.screens.student.SubmissionStudioScreen
import com.assignmenthub.ui.screens.teacher.CreateAssignmentScreen
import com.assignmenthub.ui.screens.teacher.GradingStudioScreen
import com.assignmenthub.ui.screens.teacher.TeacherDashboardScreen
import com.assignmenthub.ui.screens.teacher.TeacherGradingQueueScreen
import com.assignmenthub.ui.screens.teacher.TeacherProfileScreen
import com.assignmenthub.ui.viewmodel.AssignmentViewModel
import com.assignmenthub.ui.viewmodel.GradingViewModel
import com.assignmenthub.ui.viewmodel.SubmissionViewModel

sealed class AppScreen {
    object Dashboard : AppScreen()
    data class AssignmentDetail(val assignmentId: String) : AppScreen()
    data class SubmissionStudio(val assignmentId: String) : AppScreen()
    data class GradingStudio(val submission: Submission) : AppScreen()
}

@Composable
fun AssignmentApp(
    assignmentViewModel: AssignmentViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val repository = assignmentViewModel.repositoryInstance
    val submissionViewModel = remember(repository) { SubmissionViewModel(repository) }
    val gradingViewModel = remember(repository) { GradingViewModel(repository) }

    val isLoggedIn by assignmentViewModel.isLoggedIn.collectAsState()
    val currentUser by assignmentViewModel.currentUser.collectAsState()
    val selectedAssignment by assignmentViewModel.selectedAssignment.collectAsState()

    var selectedDestination by remember { mutableStateOf(AppNavDestination.ASSIGNMENTS) }
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Dashboard) }

    val configuration = LocalConfiguration.current
    val windowWidthDp = configuration.screenWidthDp.dp
    val windowWidthClass = calculateWindowWidthClass(windowWidthDp)

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { user ->
                assignmentViewModel.login(user)
                selectedDestination = AppNavDestination.ASSIGNMENTS
                currentScreen = AppScreen.Dashboard
            },
            modifier = modifier
        )
        return
    }

    // Handle back button
    if (currentScreen !is AppScreen.Dashboard) {
        BackHandler {
            currentScreen = AppScreen.Dashboard
        }
    }

    Scaffold(
        topBar = {
            TopBarWithRoleSwitch(
                currentUser = currentUser,
                onToggleRole = {
                    assignmentViewModel.toggleUserRole()
                    selectedDestination = AppNavDestination.ASSIGNMENTS
                    currentScreen = AppScreen.Dashboard
                },
                onResetDemo = {
                    assignmentViewModel.resetDemo()
                    selectedDestination = AppNavDestination.ASSIGNMENTS
                    currentScreen = AppScreen.Dashboard
                },
                onLogout = {
                    assignmentViewModel.logout()
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        AdaptiveNavigationScaffold(
            windowWidthClass = windowWidthClass,
            selectedDestination = selectedDestination,
            onDestinationSelected = { dest ->
                selectedDestination = dest
                currentScreen = AppScreen.Dashboard
            },
            currentUser = currentUser
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (currentUser.role == UserRole.STUDENT) {
                    // STUDENT ROLE FLOW
                    if (windowWidthClass == WindowWidthClass.COMPACT) {
                        // Compact Single-Pane navigation
                        when (val screen = currentScreen) {
                            is AppScreen.Dashboard -> {
                                when (selectedDestination) {
                                    AppNavDestination.ASSIGNMENTS -> {
                                        StudentDashboardScreen(
                                            viewModel = assignmentViewModel,
                                            onSelectAssignment = { id ->
                                                currentScreen = AppScreen.AssignmentDetail(id)
                                            }
                                        )
                                    }
                                    AppNavDestination.CREATE_OR_SUBMIT -> {
                                        StudentManageSubmitScreen(
                                            viewModel = assignmentViewModel,
                                            onOpenSubmissionStudio = { id ->
                                                currentScreen = AppScreen.SubmissionStudio(id)
                                            },
                                            onViewAssignmentDetail = { id ->
                                                currentScreen = AppScreen.AssignmentDetail(id)
                                            }
                                        )
                                    }
                                    AppNavDestination.GRADES -> {
                                        StudentGradesScreen(
                                            viewModel = assignmentViewModel,
                                            onViewAssignmentDetail = { id ->
                                                currentScreen = AppScreen.AssignmentDetail(id)
                                            }
                                        )
                                    }
                                    AppNavDestination.PROFILE -> {
                                        StudentProfileScreen(
                                            viewModel = assignmentViewModel,
                                            onToggleRole = {
                                                assignmentViewModel.toggleUserRole()
                                                selectedDestination = AppNavDestination.ASSIGNMENTS
                                                currentScreen = AppScreen.Dashboard
                                            },
                                            onResetDemo = {
                                                assignmentViewModel.resetDemo()
                                                selectedDestination = AppNavDestination.ASSIGNMENTS
                                                currentScreen = AppScreen.Dashboard
                                            }
                                        )
                                    }
                                }
                            }
                            is AppScreen.AssignmentDetail -> {
                                val targetAssignment = assignmentViewModel.allAssignments.collectAsState().value
                                    .find { it.id == screen.assignmentId } ?: selectedAssignment

                                val studentSubmission = repository.submissionsFlow.collectAsState().value
                                    .find { it.assignmentId == screen.assignmentId && it.studentId == currentUser.id }

                                AssignmentDetailScreen(
                                    assignment = targetAssignment,
                                    studentSubmission = studentSubmission,
                                    onNavigateBack = { currentScreen = AppScreen.Dashboard },
                                    onOpenSubmissionStudio = { id ->
                                        currentScreen = AppScreen.SubmissionStudio(id)
                                    }
                                )
                            }
                            is AppScreen.SubmissionStudio -> {
                                val targetAssignment = assignmentViewModel.allAssignments.collectAsState().value
                                    .find { it.id == screen.assignmentId } ?: selectedAssignment

                                if (targetAssignment != null) {
                                    SubmissionStudioScreen(
                                        assignment = targetAssignment,
                                        viewModel = submissionViewModel,
                                        onNavigateBack = {
                                            currentScreen = AppScreen.AssignmentDetail(screen.assignmentId)
                                        }
                                    )
                                }
                            }
                            else -> {
                                StudentDashboardScreen(
                                    viewModel = assignmentViewModel,
                                    onSelectAssignment = { id ->
                                        currentScreen = AppScreen.AssignmentDetail(id)
                                    }
                                )
                            }
                        }
                    } else {
                        // Tablet / Foldable Two-Pane Layout
                        AdaptiveTwoPaneLayout(
                            windowWidthClass = windowWidthClass,
                            listPane = {
                                when (selectedDestination) {
                                    AppNavDestination.ASSIGNMENTS -> {
                                        StudentDashboardScreen(
                                            viewModel = assignmentViewModel,
                                            onSelectAssignment = { id ->
                                                assignmentViewModel.selectAssignment(id)
                                                currentScreen = AppScreen.Dashboard
                                            }
                                        )
                                    }
                                    AppNavDestination.CREATE_OR_SUBMIT -> {
                                        StudentManageSubmitScreen(
                                            viewModel = assignmentViewModel,
                                            onOpenSubmissionStudio = { id ->
                                                currentScreen = AppScreen.SubmissionStudio(id)
                                            },
                                            onViewAssignmentDetail = { id ->
                                                assignmentViewModel.selectAssignment(id)
                                                currentScreen = AppScreen.Dashboard
                                            }
                                        )
                                    }
                                    AppNavDestination.GRADES -> {
                                        StudentGradesScreen(
                                            viewModel = assignmentViewModel,
                                            onViewAssignmentDetail = { id ->
                                                assignmentViewModel.selectAssignment(id)
                                                currentScreen = AppScreen.Dashboard
                                            }
                                        )
                                    }
                                    AppNavDestination.PROFILE -> {
                                        StudentProfileScreen(
                                            viewModel = assignmentViewModel,
                                            onToggleRole = {
                                                assignmentViewModel.toggleUserRole()
                                                selectedDestination = AppNavDestination.ASSIGNMENTS
                                                currentScreen = AppScreen.Dashboard
                                            },
                                            onResetDemo = {
                                                assignmentViewModel.resetDemo()
                                                selectedDestination = AppNavDestination.ASSIGNMENTS
                                                currentScreen = AppScreen.Dashboard
                                            }
                                        )
                                    }
                                }
                            },
                            detailPane = {
                                when (val screen = currentScreen) {
                                    is AppScreen.SubmissionStudio -> {
                                        val targetAssignment = assignmentViewModel.allAssignments.collectAsState().value
                                            .find { it.id == screen.assignmentId } ?: selectedAssignment

                                        if (targetAssignment != null) {
                                            SubmissionStudioScreen(
                                                assignment = targetAssignment,
                                                viewModel = submissionViewModel,
                                                onNavigateBack = { currentScreen = AppScreen.Dashboard }
                                            )
                                        }
                                    }
                                    else -> {
                                        val studentSubmission = repository.submissionsFlow.collectAsState().value
                                            .find { it.assignmentId == selectedAssignment?.id && it.studentId == currentUser.id }

                                        AssignmentDetailScreen(
                                            assignment = selectedAssignment,
                                            studentSubmission = studentSubmission,
                                            onNavigateBack = null,
                                            onOpenSubmissionStudio = { id ->
                                                currentScreen = AppScreen.SubmissionStudio(id)
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
                } else {
                    // TEACHER ROLE FLOW
                    when (val screen = currentScreen) {
                        is AppScreen.Dashboard -> {
                            when (selectedDestination) {
                                AppNavDestination.ASSIGNMENTS -> {
                                    TeacherDashboardScreen(
                                        assignmentViewModel = assignmentViewModel,
                                        gradingViewModel = gradingViewModel,
                                        onSelectSubmissionToGrade = { submission ->
                                            currentScreen = AppScreen.GradingStudio(submission)
                                        },
                                        onCreateAssignment = {
                                            selectedDestination = AppNavDestination.CREATE_OR_SUBMIT
                                        }
                                    )
                                }
                                AppNavDestination.CREATE_OR_SUBMIT -> {
                                    CreateAssignmentScreen(
                                        viewModel = gradingViewModel,
                                        onNavigateBack = {
                                            selectedDestination = AppNavDestination.ASSIGNMENTS
                                        }
                                    )
                                }
                                AppNavDestination.GRADES -> {
                                    TeacherGradingQueueScreen(
                                        assignmentViewModel = assignmentViewModel,
                                        gradingViewModel = gradingViewModel,
                                        onSelectSubmissionToGrade = { submission ->
                                            currentScreen = AppScreen.GradingStudio(submission)
                                        }
                                    )
                                }
                                AppNavDestination.PROFILE -> {
                                    TeacherProfileScreen(
                                        assignmentViewModel = assignmentViewModel,
                                        gradingViewModel = gradingViewModel,
                                        onToggleRole = {
                                            assignmentViewModel.toggleUserRole()
                                            selectedDestination = AppNavDestination.ASSIGNMENTS
                                            currentScreen = AppScreen.Dashboard
                                        },
                                        onResetDemo = {
                                            assignmentViewModel.resetDemo()
                                            selectedDestination = AppNavDestination.ASSIGNMENTS
                                            currentScreen = AppScreen.Dashboard
                                        }
                                    )
                                }
                            }
                        }
                        is AppScreen.GradingStudio -> {
                            val assignment = assignmentViewModel.allAssignments.collectAsState().value
                                .find { it.id == screen.submission.assignmentId }

                            GradingStudioScreen(
                                submission = screen.submission,
                                assignment = assignment,
                                viewModel = gradingViewModel,
                                onNavigateBack = { currentScreen = AppScreen.Dashboard }
                            )
                        }
                        else -> {
                            TeacherDashboardScreen(
                                assignmentViewModel = assignmentViewModel,
                                gradingViewModel = gradingViewModel,
                                onSelectSubmissionToGrade = { submission ->
                                    currentScreen = AppScreen.GradingStudio(submission)
                                },
                                onCreateAssignment = {
                                    selectedDestination = AppNavDestination.CREATE_OR_SUBMIT
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
