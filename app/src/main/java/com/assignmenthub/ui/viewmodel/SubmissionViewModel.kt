package com.assignmenthub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmenthub.data.repository.MockDataRepository
import com.assignmenthub.model.DemoUsers
import com.assignmenthub.model.Submission
import com.assignmenthub.model.SubmissionAttachment
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubmissionViewModel(
    private val repository: MockDataRepository
) : ViewModel() {

    private val _currentAssignmentId = MutableStateFlow<String>("")
    val currentAssignmentId: StateFlow<String> = _currentAssignmentId.asStateFlow()

    private val _existingSubmission = MutableStateFlow<Submission?>(null)
    val existingSubmission: StateFlow<Submission?> = _existingSubmission.asStateFlow()

    private val _textResponse = MutableStateFlow("")
    val textResponse: StateFlow<String> = _textResponse.asStateFlow()

    private val _linkInput = MutableStateFlow("")
    val linkInput: StateFlow<String> = _linkInput.asStateFlow()

    private val _links = MutableStateFlow<List<String>>(emptyList())
    val links: StateFlow<List<String>> = _links.asStateFlow()

    private val _attachments = MutableStateFlow<List<SubmissionAttachment>>(emptyList())
    val attachments: StateFlow<List<SubmissionAttachment>> = _attachments.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _submissionSuccessEvent = MutableSharedFlow<String>()
    val submissionSuccessEvent: SharedFlow<String> = _submissionSuccessEvent.asSharedFlow()

    fun loadAssignment(assignmentId: String) {
        _currentAssignmentId.value = assignmentId
        viewModelScope.launch {
            repository.getStudentSubmission(assignmentId, DemoUsers.sampleStudent.id).collect { sub ->
                _existingSubmission.value = sub
                if (sub != null) {
                    _textResponse.value = sub.textContent
                    _links.value = sub.links
                    _attachments.value = sub.attachments
                } else {
                    _textResponse.value = ""
                    _linkInput.value = ""
                    _links.value = emptyList()
                    _attachments.value = emptyList()
                }
            }
        }
    }

    fun updateTextResponse(text: String) {
        _textResponse.value = text
    }

    fun updateLinkInput(link: String) {
        _linkInput.value = link
    }

    fun addLink() {
        val link = _linkInput.value.trim()
        if (link.isNotBlank() && !_links.value.contains(link)) {
            _links.value = _links.value + link
            _linkInput.value = ""
        }
    }

    fun removeLink(link: String) {
        _links.value = _links.value.filter { it != link }
    }

    fun addSimulatedAttachment(presetName: String, extension: String, sizeStr: String) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        val newAttachment = SubmissionAttachment(
            id = "att_${System.currentTimeMillis()}",
            fileName = presetName,
            fileSizeFormatted = sizeStr,
            fileExtension = extension,
            uploadedAt = "Today at ${sdf.format(Date())}"
        )
        _attachments.value = _attachments.value + newAttachment
    }

    fun removeAttachment(attachmentId: String) {
        _attachments.value = _attachments.value.filter { it.id != attachmentId }
    }

    fun submit(onSuccess: () -> Unit) {
        if (_currentAssignmentId.value.isBlank()) return
        if (_textResponse.value.isBlank() && _attachments.value.isEmpty() && _links.value.isEmpty()) {
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            repository.submitAssignment(
                assignmentId = _currentAssignmentId.value,
                studentId = DemoUsers.sampleStudent.id,
                studentName = DemoUsers.sampleStudent.name,
                textContent = _textResponse.value,
                links = _links.value,
                attachments = _attachments.value
            )
            _isSubmitting.value = false
            _submissionSuccessEvent.emit("Assignment submitted successfully!")
            onSuccess()
        }
    }
}
