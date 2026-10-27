package com.assignmenthub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmenthub.data.repository.MockDataRepository
import com.assignmenthub.model.Assignment
import com.assignmenthub.model.AssignmentStatus
import com.assignmenthub.model.Submission
import com.assignmenthub.model.SubmissionType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GradingViewModel(
    private val repository: MockDataRepository
) : ViewModel() {

    val allSubmissions: StateFlow<List<Submission>> = repository.submissionsFlow

    val pendingSubmissions: StateFlow<List<Submission>> = repository.getAllPendingTeacherSubmissions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSubmissionId = MutableStateFlow<String?>(null)
    val selectedSubmissionId: StateFlow<String?> = _selectedSubmissionId.asStateFlow()

    private val _scoreInput = MutableStateFlow<String>("")
    val scoreInput: StateFlow<String> = _scoreInput.asStateFlow()

    private val _feedbackText = MutableStateFlow<String>("")
    val feedbackText: StateFlow<String> = _feedbackText.asStateFlow()

    private val _isPublishing = MutableStateFlow(false)
    val isPublishing: StateFlow<Boolean> = _isPublishing.asStateFlow()

    private val _gradePublishedEvent = MutableSharedFlow<String>()
    val gradePublishedEvent: SharedFlow<String> = _gradePublishedEvent.asSharedFlow()

    val quickFeedbackTemplates = listOf(
        "Exceptional work! Well-structured solution and clear documentation.",
        "Solid attempt! Consider adding more test cases for boundary conditions.",
        "Good design and clean code layout. Meets all core criteria.",
        "Well-documented report with great visual architecture diagrams."
    )

    fun selectSubmission(submission: Submission) {
        _selectedSubmissionId.value = submission.id
        _scoreInput.value = submission.score?.toString() ?: ""
        _feedbackText.value = submission.teacherFeedback ?: ""
    }

    fun updateScore(score: String) {
        if (score.isEmpty() || (score.all { it.isDigit() } && score.length <= 4)) {
            _scoreInput.value = score
        }
    }

    fun updateFeedback(feedback: String) {
        _feedbackText.value = feedback
    }

    fun appendQuickFeedback(phrase: String) {
        if (_feedbackText.value.isBlank()) {
            _feedbackText.value = phrase
        } else {
            _feedbackText.value = "${_feedbackText.value} $phrase"
        }
    }

    fun publishGrade(maxScore: Int, onSuccess: () -> Unit) {
        val subId = _selectedSubmissionId.value ?: return
        val score = _scoreInput.value.toIntOrNull() ?: 0
        val clampedScore = score.coerceIn(0, maxScore)

        viewModelScope.launch {
            _isPublishing.value = true
            repository.gradeSubmission(
                submissionId = subId,
                score = clampedScore,
                feedback = _feedbackText.value.trim()
            )
            _isPublishing.value = false
            _gradePublishedEvent.emit("Grade published successfully!")
            onSuccess()
        }
    }

    fun createAssignment(
        courseId: String,
        courseCode: String,
        courseName: String,
        title: String,
        description: String,
        dueDateFormatted: String,
        points: Int,
        onSuccess: () -> Unit
    ) {
        val newAssignment = Assignment(
            id = "asg_${System.currentTimeMillis()}",
            courseId = courseId,
            courseCode = courseCode,
            courseName = courseName,
            title = title,
            description = description,
            dueDateFormatted = dueDateFormatted,
            dueDateMillis = System.currentTimeMillis() + (7 * 86400000L),
            points = points,
            status = AssignmentStatus.PENDING,
            allowedSubmissionTypes = listOf(
                SubmissionType.DOCUMENT,
                SubmissionType.TEXT_ENTRY,
                SubmissionType.URL_LINK
            ),
            submissionCount = 0,
            totalStudents = 45
        )

        viewModelScope.launch {
            repository.createAssignment(newAssignment)
            onSuccess()
        }
    }
}
