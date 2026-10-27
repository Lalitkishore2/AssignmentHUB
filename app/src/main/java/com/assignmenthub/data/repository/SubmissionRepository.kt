package com.assignmenthub.data.repository

import com.assignmenthub.model.Submission
import com.assignmenthub.model.SubmissionAttachment
import kotlinx.coroutines.flow.Flow

interface SubmissionRepository {
    fun getSubmissionsForAssignment(assignmentId: String): Flow<List<Submission>>
    fun getStudentSubmission(assignmentId: String, studentId: String): Flow<Submission?>
    fun getAllPendingTeacherSubmissions(): Flow<List<Submission>>
    suspend fun submitAssignment(
        assignmentId: String,
        studentId: String,
        studentName: String,
        textContent: String,
        links: List<String>,
        attachments: List<SubmissionAttachment>
    ): Result<Submission>
    suspend fun gradeSubmission(
        submissionId: String,
        score: Int,
        feedback: String
    ): Result<Submission>
    suspend fun resetDemoData()
}
