package com.ganaljigi.kubf.feature.home.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kubfandroid.composeapp.generated.resources.Res
import kubfandroid.composeapp.generated.resources.ic_my_location
import com.ganaljigi.kubf.core.designsystem.theme.MainGreen
import com.ganaljigi.kubf.core.ui.util.noRippleClickable

@Composable
fun MyLocationButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
) {
    Surface(
        modifier = modifier
            .size(48.dp)
            .noRippleClickable(onClick),
        shape = CircleShape,
        color = MainGreen,
        shadowElevation = 2.dp,
    ) {
        Icon(
            modifier = Modifier
                .padding(12.dp),
            painter = painterResource(Res.drawable.ic_my_location),
            contentDescription = "현재 위치로 이동",
            tint = Color.White,
        )
    }
}

@Preview
@Composable
private fun MyLocationButtonPreview() {
    MyLocationButton()
}
