package com.assignmenthub.model

enum class AssignmentStatus {
    PENDING,
    SUBMITTED,
    GRADED,
    OVERDUE
}

enum class SubmissionType {
    DOCUMENT,
    TEXT_ENTRY,
    URL_LINK,
    CODE_ARCHIVE
}

data class RubricItem(
    val title: String,
    val description: String,
    val maxPoints: Int
)

data class Assignment(
    val id: String,
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val title: String,
    val description: String,
    val dueDateFormatted: String,
    val dueDateMillis: Long,
    val points: Int,
    val status: AssignmentStatus,
    val allowedSubmissionTypes: List<SubmissionType> = listOf(
        SubmissionType.DOCUMENT,
        SubmissionType.TEXT_ENTRY,
        SubmissionType.URL_LINK
    ),
    val attachedResources: List<String> = emptyList(),
    val rubricCriteria: List<RubricItem> = emptyList(),
    val submissionCount: Int = 0,
    val totalStudents: Int = 45
)
