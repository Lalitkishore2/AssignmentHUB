package com.assignmenthub.model

import androidx.compose.ui.graphics.Color
import com.assignmenthub.ui.theme.CourseBlue
import com.assignmenthub.ui.theme.CourseIndigo
import com.assignmenthub.ui.theme.CourseTeal
import com.assignmenthub.ui.theme.CoursePurple

data class Course(
    val id: String,
    val code: String,
    val name: String,
    val instructorName: String,
    val color: Color,
    val enrolledStudents: Int = 45,
    val semester: String = "Fall 2026"
)

object SampleCourses {
    val CS402 = Course(
        id = "course_cs402",
        code = "CS-402",
        name = "Mobile Application Engineering",
        instructorName = "Dr. Sarah Mitchell",
        color = CourseIndigo
    )

    val CS301 = Course(
        id = "course_cs301",
        code = "CS-301",
        name = "Advanced Algorithms & Data Structures",
        instructorName = "Prof. David Miller",
        color = CourseTeal
    )

    val UX204 = Course(
        id = "course_ux204",
        code = "UX-204",
        name = "Interactive UI/UX Design Systems",
        instructorName = "Elena Vance",
        color = CoursePurple
    )

    val SE410 = Course(
        id = "course_se410",
        code = "SE-410",
        name = "Software Quality & Security",
        instructorName = "Dr. Marcus Brody",
        color = CourseBlue
    )

    val allCourses = listOf(CS402, CS301, UX204, SE410)
}
