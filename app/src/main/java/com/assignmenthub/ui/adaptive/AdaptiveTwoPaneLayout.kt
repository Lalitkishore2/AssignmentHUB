package com.assignmenthub.ui.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdaptiveTwoPaneLayout(
    windowWidthClass: WindowWidthClass,
    listPane: @Composable () -> Unit,
    detailPane: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    if (windowWidthClass == WindowWidthClass.COMPACT) {
        // Single pane for compact phones
        Box(modifier = modifier.fillMaxSize()) {
            listPane()
        }
    } else {
        // Two-pane split view for tablets & unfolded foldables
        Row(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                listPane()
            }
            VerticalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                detailPane()
            }
        }
    }
}
