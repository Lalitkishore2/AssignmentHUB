package com.assignmenthub.model

data class SubmissionAttachment(
    val id: String,
    val fileName: String,
    val fileSizeFormatted: String,
    val fileExtension: String, // pdf, zip, docx, etc.
    val uploadedAt: String
)

data class Submission(
    val id: String,
    val assignmentId: String,
    val studentId: String,
    val studentName: String,
    val studentAvatar: String = "AJ",
    val submittedAtFormatted: String,
    val submittedAtMillis: Long,
    val textContent: String = "",
    val links: List<String> = emptyList(),
    val attachments: List<SubmissionAttachment> = emptyList(),
    val score: Int? = null,
    val maxScore: Int = 100,
    val isGraded: Boolean = false,
    val teacherFeedback: String? = null,
    val gradedAtFormatted: String? = null
)
