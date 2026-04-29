package com.ganaljigi.kubf.feature.helper.component.information

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun InformationTitle() {
    Text(
        text = "정보",
        modifier = Modifier.padding(16.dp),
    )
}

@Composable
actual fun InfoBox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text("장애학생지원센터")
        Text("서울특별시 광진구 능동로 120")
        Text("02-450-3968")
    }
}
