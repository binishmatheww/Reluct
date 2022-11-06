package work.racka.reluct.android.screens.goals.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import work.racka.reluct.android.compose.components.images.LottieAnimationWithDescription
import work.racka.reluct.android.screens.R

@Composable
internal fun EmptyGoalsIndicator(
    showAnimationProvider: () -> Boolean,
    modifier: Modifier = Modifier,
) {
    val showAnimation = remember { derivedStateOf(showAnimationProvider) }
    if (showAnimation.value) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimationWithDescription(
                lottieResId = R.raw.no_task_animation,
                imageSize = 200.dp,
                description = stringResource(R.string.no_goals_text)
            )
        }
    }
}
