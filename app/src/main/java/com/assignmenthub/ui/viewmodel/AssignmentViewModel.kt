package com.assignmenthub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignmenthub.data.repository.MockDataRepository
import com.assignmenthub.model.Assignment
import com.assignmenthub.model.AssignmentStatus
import com.assignmenthub.model.Course
import com.assignmenthub.model.DemoUsers
import com.assignmenthub.model.User
import com.assignmenthub.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardMetrics(
    val pendingCount: Int = 0,
    val submittedCount: Int = 0,
    val gradedCount: Int = 0,
    val overdueCount: Int = 0,
    val averageScorePercent: Int = 0,
    val totalAssignments: Int = 0
)

class AssignmentViewModel(
    private val repository: MockDataRepository = MockDataRepository()
) : ViewModel() {

    val repositoryInstance: MockDataRepository = repository

    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<User>(DemoUsers.sampleStudent)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _selectedCourseId = MutableStateFlow<String?>(null)
    val selectedCourseId: StateFlow<String?> = _selectedCourseId.asStateFlow()

    private val _selectedStatus = MutableStateFlow<AssignmentStatus?>(null)
    val selectedStatus: StateFlow<AssignmentStatus?> = _selectedStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedAssignmentId = MutableStateFlow<String?>("asg_01")
    val selectedAssignmentId: StateFlow<String?> = _selectedAssignmentId.asStateFlow()

    val courses: StateFlow<List<Course>> = repository.coursesFlow

    val allAssignments: StateFlow<List<Assignment>> = repository.assignmentsFlow

    val filteredAssignments: StateFlow<List<Assignment>> = combine(
        repository.assignmentsFlow,
        _selectedCourseId,
        _selectedStatus,
        _searchQuery
    ) { assignments, courseId, status, query ->
        assignments.filter { item ->
            val matchesCourse = courseId == null || item.courseId == courseId
            val matchesStatus = status == null || item.status == status
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.courseCode.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)
            matchesCourse && matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedAssignment: StateFlow<Assignment?> = combine(
        repository.assignmentsFlow,
        _selectedAssignmentId
    ) { list, id ->
        list.find { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val metrics: StateFlow<DashboardMetrics> = combine(
        repository.assignmentsFlow,
        repository.submissionsFlow
    ) { assignments, submissions ->
        val pending = assignments.count { it.status == AssignmentStatus.PENDING }
        val submitted = assignments.count { it.status == AssignmentStatus.SUBMITTED }
        val graded = assignments.count { it.status == AssignmentStatus.GRADED }
        val overdue = assignments.count { it.status == AssignmentStatus.OVERDUE }

        val studentGradedSubmissions = submissions.filter { it.isGraded && it.score != null }
        val avgPercent = if (studentGradedSubmissions.isNotEmpty()) {
            studentGradedSubmissions.sumOf { (it.score!! * 100) / it.maxScore } / studentGradedSubmissions.size
        } else {
            0
        }

        DashboardMetrics(
            pendingCount = pending,
            submittedCount = submitted,
            gradedCount = graded,
            overdueCount = overdue,
            averageScorePercent = avgPercent,
            totalAssignments = assignments.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    fun login(user: User) {
        _currentUser.value = user
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun selectAssignment(id: String) {
        _selectedAssignmentId.value = id
    }

    fun setCourseFilter(courseId: String?) {
        _selectedCourseId.value = courseId
    }

    fun setStatusFilter(status: AssignmentStatus?) {
        _selectedStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleUserRole() {
        if (_currentUser.value.role == UserRole.STUDENT) {
            _currentUser.value = DemoUsers.sampleTeacher
        } else {
            _currentUser.value = DemoUsers.sampleStudent
        }
    }

    fun resetDemo() {
        viewModelScope.launch {
            repository.resetDemoData()
            _selectedAssignmentId.value = "asg_01"
            _selectedCourseId.value = null
            _selectedStatus.value = null
            _searchQuery.value = ""
        }
    }
}
