@file:OptIn(ExperimentalFoundationApi::class)

package dev.tomislavmiksik.peak.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.tomislavmiksik.peak.R
import dev.tomislavmiksik.peak.ui.base.EventsEffect
import kotlinx.coroutines.launch

private data class OnboardingPageData(
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
)

private val onboardingPages = listOf(
    OnboardingPageData(
        imageRes = R.drawable.onboarding_one,
        titleRes = R.string.onboarding_page1_title,
        descriptionRes = R.string.onboarding_page1_description
    ),
    OnboardingPageData(
        imageRes = R.drawable.onboarding_two,
        titleRes = R.string.onboarding_page2_title,
        descriptionRes = R.string.onboarding_page2_description
    ),
    OnboardingPageData(
        imageRes = R.drawable.onboarding_three,
        titleRes = R.string.onboarding_page3_title,
        descriptionRes = R.string.onboarding_page3_description
    )
)

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
    onNavigateToDashboard: () -> Unit,
    onLaunchPermissionRequest: (Set<String>) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is OnboardingEvent.NavigateToDashboard -> onNavigateToDashboard()
            is OnboardingEvent.LaunchPermissionRequest -> onLaunchPermissionRequest(event.permissions)
        }
    }

    OnboardingContent(
        state = state,
        onRequestPermissions = { viewModel.trySendAction(OnboardingAction.RequestPermissions) },
        modifier = modifier
    )
}

@Composable
private fun OnboardingContent(
    state: OnboardingState,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        !state.isHealthConnectAvailable -> {
            HealthConnectUnavailable(modifier = modifier)
        }

        else -> {
            OnboardingPager(
                state = state,
                onRequestPermissions = onRequestPermissions,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun OnboardingPager(
    state: OnboardingState,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.spacing_lg)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (pagerState.currentPage < onboardingPages.size - 1) {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(onboardingPages.size - 1)
                        }
                    }
                ) {
                    Text(stringResource(R.string.onboarding_skip))
                }
            } else {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxl)))
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            OnboardingPage(
                pageData = onboardingPages[page],
                isLastPage = page == onboardingPages.size - 1,
                errorMessage = state.errorMessage,
                onRequestPermissions = onRequestPermissions,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
        PageIndicator(
            pagerState = pagerState,
            pageCount = onboardingPages.size
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))
        if (pagerState.currentPage < onboardingPages.size - 1) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.onboarding_next))
            }
        } else {
            Button(
                onClick = onRequestPermissions,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.onboarding_get_started))
            }
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
    }
}

@Composable
private fun OnboardingPage(
    pageData: OnboardingPageData,
    isLastPage: Boolean,
    errorMessage: String?,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.spacing_lg)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(pageData.imageRes),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.onboarding_image_size))
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxl)))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(pageData.titleRes),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

            Text(
                text = stringResource(pageData.descriptionRes),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isLastPage && errorMessage != null) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PageIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm))
    ) {
        repeat(pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.indicator_size))
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
private fun HealthConnectUnavailable(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.spacing_xxl)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_lg)),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

        Text(
            text = stringResource(R.string.health_connect_required),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

        Text(
            text = stringResource(R.string.health_connect_install_play_store),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
