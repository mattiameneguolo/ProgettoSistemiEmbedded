package com.example.progettosistemiembedded

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.isSystemInDarkTheme

private fun darken(color: Color, factor: Float = 0.85f): Color {
    return color.copy(
        red = color.red * factor,
        green = color.green * factor,
        blue = color.blue * factor
    )
}

data class GridButtonData(
    val char: String,
    val boxColor: Color,
    val textColor: Color
)

@Composable
fun MainScreen() {

    val buttons = listOf(
        GridButtonData("R", Color.Red, Color.White),
        GridButtonData("G", Color.Green, Color.Black),
        GridButtonData("B", Color.Blue, Color.White),
        GridButtonData("M", Color.Magenta, Color.White),
        GridButtonData("Y", Color.Yellow, Color.Black),
        GridButtonData("C", Color.Cyan, Color.Black)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.game_title),
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        ButtonsMatrix(buttons)

        Spacer(modifier = Modifier.height(32.dp))

        Button( onClick = { println("button pressed") } ) {
            Text(text = stringResource(R.string.start))
        }
    }
}

@Composable
fun ButtonsMatrix(buttons: List<GridButtonData>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (i in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ColorCell(
                    char = buttons[i * 2].char,
                    boxColor = buttons[i * 2].boxColor,
                    textColor = buttons[i * 2].textColor,
                    modifier = Modifier.weight(1f)
                )
                ColorCell(
                    char = buttons[i * 2 + 1].char,
                    boxColor = buttons[i * 2 + 1].boxColor,
                    textColor = buttons[i * 2 + 1].textColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ColorCell(
    char: String,
    boxColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val darkTheme = isSystemInDarkTheme()
    val normalColor = if (darkTheme) darken(boxColor, 0.85f) else boxColor

    ElevatedButton(
        onClick = { println("button $char pressed") },
        modifier = modifier.aspectRatio(1.6f),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = normalColor,
            contentColor = textColor
        )
    ) {
        Text(
            text = char,
            fontSize = 32.sp
        )
    }
}