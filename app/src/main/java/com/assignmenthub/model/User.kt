package com.assignmenthub.model

enum class UserRole {
    STUDENT,
    TEACHER
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarInitials: String,
    val titleOrMajor: String
)

object DemoUsers {
    val sampleStudent = User(
        id = "stu_01",
        name = "Alex Johnson",
        email = "alex.johnson@university.edu",
        role = UserRole.STUDENT,
        avatarInitials = "AJ",
        titleOrMajor = "Computer Science & Engineering"
    )

    val sampleTeacher = User(
        id = "tch_01",
        name = "Dr. Sarah Mitchell",
        email = "s.mitchell@university.edu",
        role = UserRole.TEACHER,
        avatarInitials = "SM",
        titleOrMajor = "Professor of Computer Science"
    )

    val initialUsers = listOf(sampleStudent, sampleTeacher)
}
