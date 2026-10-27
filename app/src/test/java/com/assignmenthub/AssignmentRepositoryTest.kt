package com.assignmenthub

import com.assignmenthub.data.repository.MockDataRepository
import com.assignmenthub.model.AssignmentStatus
import com.assignmenthub.model.DemoUsers
import com.assignmenthub.model.SubmissionAttachment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AssignmentRepositoryTest {

    private lateinit var repository: MockDataRepository

    @Before
    fun setUp() {
        repository = MockDataRepository()
    }

    @Test
    fun getAssignments_initialList_isPopulatedWithRealisticData() = runBlocking {
        val assignments = repository.getAssignments().first()
        assertTrue("Initial assignments list should not be empty", assignments.isNotEmpty())
        assertTrue("Should contain multiple assignments", assignments.size >= 5)

        val pendingAssignments = assignments.filter { it.status == AssignmentStatus.PENDING }
        assertTrue("Should have pending assignments", pendingAssignments.isNotEmpty())
    }

    @Test
    fun submitAssignment_updatesAssignmentStatusToSubmitted() = runBlocking {
        val targetAssignmentId = "asg_01" // Initially PENDING

        val attachment = SubmissionAttachment(
            id = "test_att_01",
            fileName = "Unit_Test_Report.pdf",
            fileSizeFormatted = "1.2 MB",
            fileExtension = "pdf",
            uploadedAt = "Just now"
        )

        val result = repository.submitAssignment(
            assignmentId = targetAssignmentId,
            studentId = DemoUsers.sampleStudent.id,
            studentName = DemoUsers.sampleStudent.name,
            textContent = "Unit test submission content with methodology notes.",
            links = listOf("https://github.com/test/repo"),
            attachments = listOf(attachment)
        )

        assertTrue("Submit operation should succeed", result.isSuccess)

        // Verify the assignment status is now SUBMITTED
        val updatedAssignment = repository.getAssignmentById(targetAssignmentId).first()
        assertNotNull(updatedAssignment)
        assertEquals(AssignmentStatus.SUBMITTED, updatedAssignment?.status)

        // Verify submission is retrieved for the student
        val submission = repository.getStudentSubmission(targetAssignmentId, DemoUsers.sampleStudent.id).first()
        assertNotNull(submission)
        assertEquals(1, submission?.attachments?.size)
        assertEquals("Unit_Test_Report.pdf", submission?.attachments?.first()?.fileName)
        assertFalse(submission!!.isGraded)
    }

    @Test
    fun gradeSubmission_updatesScoreAndMarksAssignmentGraded() = runBlocking {
        // Find an existing ungraded submission or submit one
        val assignmentId = "asg_01"
        repository.submitAssignment(
            assignmentId = assignmentId,
            studentId = DemoUsers.sampleStudent.id,
            studentName = DemoUsers.sampleStudent.name,
            textContent = "Work ready for grading",
            links = emptyList(),
            attachments = emptyList()
        )

        val submission = repository.getStudentSubmission(assignmentId, DemoUsers.sampleStudent.id).first()
        assertNotNull(submission)

        // Grade the submission
        val gradeResult = repository.gradeSubmission(
            submissionId = submission!!.id,
            score = 98,
            feedback = "Superb execution and clean architecture."
        )

        assertTrue(gradeResult.isSuccess)
        val gradedSub = gradeResult.getOrNull()
        assertNotNull(gradedSub)
        assertEquals(98, gradedSub?.score)
        assertTrue(gradedSub!!.isGraded)
        assertEquals("Superb execution and clean architecture.", gradedSub.teacherFeedback)

        // Verify assignment status became GRADED
        val gradedAssignment = repository.getAssignmentById(assignmentId).first()
        assertEquals(AssignmentStatus.GRADED, gradedAssignment?.status)
    }

    @Test
    fun resetDemoData_restoresInitialState() = runBlocking {
        // Change state by submitting
        repository.submitAssignment(
            assignmentId = "asg_01",
            studentId = DemoUsers.sampleStudent.id,
            studentName = DemoUsers.sampleStudent.name,
            textContent = "Temporary",
            links = emptyList(),
            attachments = emptyList()
        )

        // Reset
        repository.resetDemoData()

        // Check asg_01 is back to PENDING
        val asg = repository.getAssignmentById("asg_01").first()
        assertEquals(AssignmentStatus.PENDING, asg?.status)
    }
}
