package com.assignmenthub.data.repository

import com.assignmenthub.model.Assignment
import com.assignmenthub.model.AssignmentStatus
import com.assignmenthub.model.Course
import kotlinx.coroutines.flow.Flow

interface AssignmentRepository {
    fun getAssignments(): Flow<List<Assignment>>
    fun getAssignmentById(id: String): Flow<Assignment?>
    fun getCourses(): Flow<List<Course>>
    suspend fun createAssignment(assignment: Assignment): Result<Assignment>
    suspend fun updateAssignmentStatus(assignmentId: String, newStatus: AssignmentStatus)
}
