package com.example.playcoach.ui.components.matches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SegmentedButtonRow(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    options: List<Pair<String, String>>
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (value, label) ->
            val selected = selectedOption == value
            ElevatedButton(
                onClick = { onOptionSelected(value) },
                colors = if (selected) ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00205B),
                    contentColor = Color.White
                )
                else ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(label, fontSize = 14.sp)
            }
        }
    }
}