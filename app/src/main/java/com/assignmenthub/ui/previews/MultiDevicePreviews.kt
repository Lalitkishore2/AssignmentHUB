package com.assignmenthub.ui.previews

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.assignmenthub.AssignmentApp
import com.assignmenthub.model.AssignmentStatus
import com.assignmenthub.model.DemoUsers
import com.assignmenthub.model.SampleCourses
import com.assignmenthub.ui.components.CourseBadge
import com.assignmenthub.ui.components.StatusBadge
import com.assignmenthub.ui.screens.student.AssignmentDetailScreen
import com.assignmenthub.ui.screens.student.StudentDashboardScreen
import com.assignmenthub.ui.theme.AssignmentHubTheme
import com.assignmenthub.ui.viewmodel.AssignmentViewModel

/**
 * Android Studio Presentation Previews Suite
 *
 * Click "Split" or "Design" at top right of the Android Studio editor
 * to view the AssignmentHub application rendering live across diverse
 * device screen sizes and aspect ratios simultaneously.
 */

@Preview(
    name = "1. Standard Phone Portrait (16:9 / 18:9)",
    device = "spec:width=411dp,height=891dp,dpi=420",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewPhonePortrait() {
    AssignmentHubTheme {
        Surface {
            AssignmentApp()
        }
    }
}

@Preview(
    name = "2. Tall Aspect Ratio Phone (20:9 Modern Flagship)",
    device = "spec:width=412dp,height=915dp,dpi=440",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewTallAspectPhone() {
    AssignmentHubTheme {
        Surface {
            AssignmentApp()
        }
    }
}

@Preview(
    name = "3. Foldable Device (Unfolded - NavRail)",
    device = "spec:width=673dp,height=841dp,dpi=420",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewFoldableDevice() {
    AssignmentHubTheme {
        Surface {
            AssignmentApp()
        }
    }
}

@Preview(
    name = "4. Tablet Landscape (Two-Pane Split View)",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewTabletTwoPane() {
    AssignmentHubTheme {
        Surface {
            AssignmentApp()
        }
    }
}

@Preview(
    name = "5. Dark Mode Presentation",
    device = Devices.PHONE,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewDarkMode() {
    AssignmentHubTheme(darkTheme = true) {
        Surface {
            AssignmentApp()
        }
    }
}

@Preview(name = "6. Component: Status Badges Showcase", showBackground = true)
@Composable
fun PreviewStatusBadges() {
    AssignmentHubTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(status = AssignmentStatus.PENDING)
                StatusBadge(status = AssignmentStatus.SUBMITTED)
                StatusBadge(status = AssignmentStatus.GRADED)
                StatusBadge(status = AssignmentStatus.OVERDUE)
            }
        }
    }
}
