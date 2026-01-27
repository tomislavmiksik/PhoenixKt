package dev.tomislavmiksik.peak.ui.base.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import dev.tomislavmiksik.peak.R
import dev.tomislavmiksik.peak.ui.theme.PeakTheme

@Composable
fun PeakBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val cornerRadius = dimensionResource(R.dimen.corner_radius_lg)
    val elevation = dimensionResource(R.dimen.elevation_md)
    val borderWidth = dimensionResource(R.dimen.border_width_sm)

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius)
            )
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(R.dimen.padding_card))

    ) {
        content()
    }
}

//region Previews
@Preview(showBackground = true)
@Composable
private fun PeakBox_preview() {
    PeakTheme {
        PeakBox {
            Text(text = "Content inside PeakBox")
        }
    }
}
//endregion