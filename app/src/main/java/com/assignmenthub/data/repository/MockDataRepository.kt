package com.assignmenthub.data.repository

import com.assignmenthub.model.Assignment
import com.assignmenthub.model.AssignmentStatus
import com.assignmenthub.model.Course
import com.assignmenthub.model.DemoUsers
import com.assignmenthub.model.RubricItem
import com.assignmenthub.model.SampleCourses
import com.assignmenthub.model.Submission
import com.assignmenthub.model.SubmissionAttachment
import com.assignmenthub.model.SubmissionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MockDataRepository : AssignmentRepository, SubmissionRepository {

    private val _assignments = MutableStateFlow<List<Assignment>>(createInitialAssignments())
    val assignmentsFlow = _assignments.asStateFlow()

    private val _courses = MutableStateFlow<List<Course>>(SampleCourses.allCourses)
    val coursesFlow = _courses.asStateFlow()

    private val _submissions = MutableStateFlow<List<Submission>>(createInitialSubmissions())
    val submissionsFlow = _submissions.asStateFlow()

    override fun getAssignments(): Flow<List<Assignment>> = _assignments

    override fun getAssignmentById(id: String): Flow<Assignment?> {
        return _assignments.map { list -> list.find { it.id == id } }
    }

    override fun getCourses(): Flow<List<Course>> = _courses

    override suspend fun createAssignment(assignment: Assignment): Result<Assignment> {
        val current = _assignments.value.toMutableList()
        current.add(0, assignment)
        _assignments.value = current
        return Result.success(assignment)
    }

    override suspend fun updateAssignmentStatus(assignmentId: String, newStatus: AssignmentStatus) {
        val current = _assignments.value.map { item ->
            if (item.id == assignmentId) item.copy(status = newStatus) else item
        }
        _assignments.value = current
    }

    override fun getSubmissionsForAssignment(assignmentId: String): Flow<List<Submission>> {
        return _submissions.map { list -> list.filter { it.assignmentId == assignmentId } }
    }

    override fun getStudentSubmission(assignmentId: String, studentId: String): Flow<Submission?> {
        return _submissions.map { list ->
            list.find { it.assignmentId == assignmentId && it.studentId == studentId }
        }
    }

    override fun getAllPendingTeacherSubmissions(): Flow<List<Submission>> {
        return _submissions.map { list -> list.filter { !it.isGraded } }
    }

    override suspend fun submitAssignment(
        assignmentId: String,
        studentId: String,
        studentName: String,
        textContent: String,
        links: List<String>,
        attachments: List<SubmissionAttachment>
    ): Result<Submission> {
        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        val formattedDate = sdf.format(Date())

        val assignment = _assignments.value.find { it.id == assignmentId }
        val maxPoints = assignment?.points ?: 100

        val newSubmission = Submission(
            id = "sub_${System.currentTimeMillis()}",
            assignmentId = assignmentId,
            studentId = studentId,
            studentName = studentName,
            submittedAtFormatted = formattedDate,
            submittedAtMillis = System.currentTimeMillis(),
            textContent = textContent,
            links = links,
            attachments = attachments,
            maxScore = maxPoints,
            isGraded = false
        )

        // Add or replace existing submission
        val currentSubmissions = _submissions.value.toMutableList()
        currentSubmissions.removeAll { it.assignmentId == assignmentId && it.studentId == studentId }
        currentSubmissions.add(0, newSubmission)
        _submissions.value = currentSubmissions

        // Update Assignment status to SUBMITTED
        updateAssignmentStatus(assignmentId, AssignmentStatus.SUBMITTED)

        return Result.success(newSubmission)
    }

    override suspend fun gradeSubmission(
        submissionId: String,
        score: Int,
        feedback: String
    ): Result<Submission> {
        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        val formattedDate = sdf.format(Date())

        var targetAssignmentId = ""
        val updatedSubmissions = _submissions.value.map { sub ->
            if (sub.id == submissionId) {
                targetAssignmentId = sub.assignmentId
                sub.copy(
                    score = score,
                    teacherFeedback = feedback,
                    isGraded = true,
                    gradedAtFormatted = formattedDate
                )
            } else {
                sub
            }
        }
        _submissions.value = updatedSubmissions

        // Update Assignment status to GRADED
        if (targetAssignmentId.isNotEmpty()) {
            updateAssignmentStatus(targetAssignmentId, AssignmentStatus.GRADED)
        }

        val updatedSub = updatedSubmissions.find { it.id == submissionId }
            ?: return Result.failure(Exception("Submission not found"))
        return Result.success(updatedSub)
    }

    override suspend fun resetDemoData() {
        _assignments.value = createInitialAssignments()
        _submissions.value = createInitialSubmissions()
    }

    companion object {
        private fun createInitialAssignments(): List<Assignment> {
            return listOf(
                Assignment(
                    id = "asg_01",
                    courseId = SampleCourses.CS402.id,
                    courseCode = SampleCourses.CS402.code,
                    courseName = SampleCourses.CS402.name,
                    title = "Responsive UI & Adaptive Navigation in Jetpack Compose",
                    description = "Design and build a fully adaptive mobile screen layout that supports compact phones, 20:9 tall aspect ratios, foldable devices, and widescreen tablets. Implement NavigationBar, NavigationRail, and dynamic WindowSizeClass responsiveness. Submit your code archive and architecture writeup.",
                    dueDateFormatted = "Tomorrow at 11:59 PM",
                    dueDateMillis = System.currentTimeMillis() + 86400000L,
                    points = 100,
                    status = AssignmentStatus.PENDING,
                    allowedSubmissionTypes = listOf(
                        SubmissionType.DOCUMENT,
                        SubmissionType.TEXT_ENTRY,
                        SubmissionType.URL_LINK
                    ),
                    attachedResources = listOf(
                        "Material3_Adaptive_Guidelines.pdf",
                        "Design_Spec_Figma_Tokens.json"
                    ),
                    rubricCriteria = listOf(
                        RubricItem("Layout Responsiveness", "Seamless adaptation to phone, foldable, and tablet aspect ratios.", 40),
                        RubricItem("Code Quality & Architecture", "Clean separation of ViewModel, UI, and Repository layers.", 35),
                        RubricItem("Material 3 & Theming", "Consistent colors, dark mode support, and accessibility.", 25)
                    ),
                    submissionCount = 28,
                    totalStudents = 45
                ),
                Assignment(
                    id = "asg_02",
                    courseId = SampleCourses.CS301.id,
                    courseCode = SampleCourses.CS301.code,
                    courseName = SampleCourses.CS301.name,
                    title = "Mini-Project: Dijkstra & A* Graph Pathfinding Visualizer",
                    description = "Implement an interactive graph pathfinding tool. Benchmark performance on grid graphs of varying density (100x100 to 1000x1000 nodes). Provide complexity analysis and profiling metrics.",
                    dueDateFormatted = "In 4 Days (Friday, 5:00 PM)",
                    dueDateMillis = System.currentTimeMillis() + (4 * 86400000L),
                    points = 80,
                    status = AssignmentStatus.PENDING,
                    allowedSubmissionTypes = listOf(
                        SubmissionType.DOCUMENT,
                        SubmissionType.URL_LINK,
                        SubmissionType.CODE_ARCHIVE
                    ),
                    attachedResources = listOf("Graph_Benchmark_Dataset.csv"),
                    rubricCriteria = listOf(
                        RubricItem("Algorithm Correctness", "Correct shortest paths on directed and weighted graphs.", 30),
                        RubricItem("Runtime Benchmark & Profiling", "Accurate asymptotic timing data vs matrix sizes.", 30),
                        RubricItem("Report & Visualization", "Clear charts and explanation of edge cases.", 20)
                    ),
                    submissionCount = 14,
                    totalStudents = 45
                ),
                Assignment(
                    id = "asg_03",
                    courseId = SampleCourses.SE410.id,
                    courseCode = SampleCourses.SE410.code,
                    courseName = SampleCourses.SE410.name,
                    title = "Security Audit & Threat Modeling Case Study",
                    description = "Perform a STRIDE threat model on an OAuth 2.0 and JWT token authentication pipeline. Identify vulnerabilities (CSRF, token hijacking, replay attacks) and propose mitigation controls.",
                    dueDateFormatted = "Submitted Yesterday",
                    dueDateMillis = System.currentTimeMillis() - 86400000L,
                    points = 50,
                    status = AssignmentStatus.SUBMITTED,
                    allowedSubmissionTypes = listOf(
                        SubmissionType.DOCUMENT,
                        SubmissionType.TEXT_ENTRY
                    ),
                    attachedResources = listOf("OAuth2_System_Architecture.png"),
                    rubricCriteria = listOf(
                        RubricItem("STRIDE Threat Identification", "Thorough breakdown of all 6 STRIDE categories.", 25),
                        RubricItem("Remediation Strategy", "Actionable, industry-standard mitigation steps.", 25)
                    ),
                    submissionCount = 42,
                    totalStudents = 45
                ),
                Assignment(
                    id = "asg_04",
                    courseId = SampleCourses.UX204.id,
                    courseCode = SampleCourses.UX204.code,
                    courseName = SampleCourses.UX204.name,
                    title = "Design System Components: Tokens, Cards & Navigation",
                    description = "Construct a reusable token-based design system in Figma and translate into interactive Compose components. Ensure WCAG AAA contrast ratio and dynamic text scaling.",
                    dueDateFormatted = "Graded Oct 22",
                    dueDateMillis = System.currentTimeMillis() - (5 * 86400000L),
                    points = 100,
                    status = AssignmentStatus.GRADED,
                    allowedSubmissionTypes = listOf(
                        SubmissionType.DOCUMENT,
                        SubmissionType.URL_LINK
                    ),
                    attachedResources = listOf("WCAG_2.2_Checklist.pdf"),
                    rubricCriteria = listOf(
                        RubricItem("Accessibility & Contrast", "Meets 4.5:1 text and 3:1 graphical component contrast.", 40),
                        RubricItem("Design Tokens & Consistency", "Clear hierarchy of typography, spacing, and elevation.", 35),
                        RubricItem("Component Ergonomics", "Intuitive touch targets and responsive behavior.", 25)
                    ),
                    submissionCount = 45,
                    totalStudents = 45
                ),
                Assignment(
                    id = "asg_05",
                    courseId = SampleCourses.CS402.id,
                    courseCode = SampleCourses.CS402.code,
                    courseName = SampleCourses.CS402.name,
                    title = "Lab 4: Android Studio Memory Profiler & Battery Historian",
                    description = "Trace memory allocations and identify bitmap retention leaks using Android Studio LeakCanary and Profiler. Submit annotated screenshots and corrected code.",
                    dueDateFormatted = "Graded Oct 18",
                    dueDateMillis = System.currentTimeMillis() - (8 * 86400000L),
                    points = 40,
                    status = AssignmentStatus.GRADED,
                    allowedSubmissionTypes = listOf(
                        SubmissionType.DOCUMENT,
                        SubmissionType.CODE_ARCHIVE
                    ),
                    submissionCount = 44,
                    totalStudents = 45
                ),
                Assignment(
                    id = "asg_06",
                    courseId = SampleCourses.CS301.id,
                    courseCode = SampleCourses.CS301.code,
                    courseName = SampleCourses.CS301.name,
                    title = "Problem Set 1: Amortized Analysis & Fibonacci Heaps",
                    description = "Prove bounds for Fibonacci heap decrease-key operations using the potential method. Submit mathematical derivations in LaTeX or scanned PDF.",
                    dueDateFormatted = "Overdue (Due Oct 10)",
                    dueDateMillis = System.currentTimeMillis() - (16 * 86400000L),
                    points = 50,
                    status = AssignmentStatus.OVERDUE,
                    allowedSubmissionTypes = listOf(SubmissionType.DOCUMENT),
                    submissionCount = 39,
                    totalStudents = 45
                )
            )
        }

        private fun createInitialSubmissions(): List<Submission> {
            return listOf(
                Submission(
                    id = "sub_init_01",
                    assignmentId = "asg_03",
                    studentId = DemoUsers.sampleStudent.id,
                    studentName = DemoUsers.sampleStudent.name,
                    studentAvatar = "AJ",
                    submittedAtFormatted = "Yesterday at 4:15 PM",
                    submittedAtMillis = System.currentTimeMillis() - 86400000L,
                    textContent = "Completed the full STRIDE threat analysis. For token replay attacks, implemented cryptographically bound DPoP (Demonstrating Proof-of-Possession) headers and short-lived JWT lifetimes with refresh token rotation.",
                    links = listOf("https://github.com/alex-johnson/security-stride-audit"),
                    attachments = listOf(
                        SubmissionAttachment(
                            id = "att_01",
                            fileName = "STRIDE_Security_Audit_Report.pdf",
                            fileSizeFormatted = "2.4 MB",
                            fileExtension = "pdf",
                            uploadedAt = "Yesterday at 4:12 PM"
                        ),
                        SubmissionAttachment(
                            id = "att_02",
                            fileName = "token_validator_test.zip",
                            fileSizeFormatted = "540 KB",
                            fileExtension = "zip",
                            uploadedAt = "Yesterday at 4:14 PM"
                        )
                    ),
                    maxScore = 50,
                    isGraded = false
                ),
                Submission(
                    id = "sub_init_02",
                    assignmentId = "asg_04",
                    studentId = DemoUsers.sampleStudent.id,
                    studentName = DemoUsers.sampleStudent.name,
                    studentAvatar = "AJ",
                    submittedAtFormatted = "Oct 21 at 9:30 PM",
                    submittedAtMillis = System.currentTimeMillis() - (5 * 86400000L),
                    textContent = "Implemented Material 3 Design Tokens covering light, dark, and high-contrast modes. Tested in Android Studio multi-screen previews across phone, foldable, and tablet aspect ratios.",
                    links = listOf("https://figma.com/@alexj/design-system-tokens"),
                    attachments = listOf(
                        SubmissionAttachment(
                            id = "att_03",
                            fileName = "UX_Design_System_Spec.pdf",
                            fileSizeFormatted = "4.8 MB",
                            fileExtension = "pdf",
                            uploadedAt = "Oct 21 at 9:28 PM"
                        )
                    ),
                    score = 96,
                    maxScore = 100,
                    isGraded = true,
                    teacherFeedback = "Superb typography and responsive layout choices, Alex! The contrast ratio verification in your report was exceptionally well-documented. Outstanding work.",
                    gradedAtFormatted = "Oct 22 at 11:15 AM"
                ),
                Submission(
                    id = "sub_init_03",
                    assignmentId = "asg_05",
                    studentId = DemoUsers.sampleStudent.id,
                    studentName = DemoUsers.sampleStudent.name,
                    studentAvatar = "AJ",
                    submittedAtFormatted = "Oct 17 at 8:10 PM",
                    submittedAtMillis = System.currentTimeMillis() - (9 * 86400000L),
                    textContent = "Analyzed bitmap heap allocations in Android Studio Memory Profiler. Resolved context leak inside the ImageLoader singleton by using ApplicationContext.",
                    links = emptyList(),
                    attachments = listOf(
                        SubmissionAttachment(
                            id = "att_04",
                            fileName = "Memory_Profiler_Dump_Analysis.pdf",
                            fileSizeFormatted = "3.1 MB",
                            fileExtension = "pdf",
                            uploadedAt = "Oct 17 at 8:08 PM"
                        )
                    ),
                    score = 38,
                    maxScore = 40,
                    isGraded = true,
                    teacherFeedback = "Great diagnostic walkthrough. Make sure to also check listener unregistration inside onCleared() in ViewModels.",
                    gradedAtFormatted = "Oct 18 at 2:40 PM"
                ),
                // Another student submission awaiting grading for Dr. Mitchell's grading queue
                Submission(
                    id = "sub_init_04",
                    assignmentId = "asg_01",
                    studentId = "stu_02",
                    studentName = "Chloe Bennett",
                    studentAvatar = "CB",
                    submittedAtFormatted = "Today at 10:20 AM",
                    submittedAtMillis = System.currentTimeMillis() - 10800000L,
                    textContent = "Submitted my Compose adaptive navigation project with phone NavigationBar and tablet Two-Pane layout.",
                    links = listOf("https://github.com/chloeb/compose-adaptive-hub"),
                    attachments = listOf(
                        SubmissionAttachment(
                            id = "att_05",
                            fileName = "Bennett_Adaptive_Compose.zip",
                            fileSizeFormatted = "1.8 MB",
                            fileExtension = "zip",
                            uploadedAt = "Today at 10:18 AM"
                        )
                    ),
                    maxScore = 100,
                    isGraded = false
                )
            )
        }
    }
}
