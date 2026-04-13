package com.example.progettosistemiembedded

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Restituisce una versione più scura del colore passato in input,
 * applicando un fattore di riduzione ai componenti RGB.
 *
 * Viene usata per adattare l'aspetto dei tasti al tema scuro e
 * (TODO) per dare un feedback visivo alla pressione di un pulsante
 */
private fun darken(color: Color, factor: Float = 0.85f): Color {
    return color.copy(
        red = color.red * factor,
        green = color.green * factor,
        blue = color.blue * factor
    )
}

/**
 *
 */
data class GridButtonData(
    val char: String,
    val boxColor: Color,
    val textColor: Color
)

@Composable
fun MainScreen(onGameEnd: (sequence: List<String>) -> Unit) {

    var sequence by rememberSaveable { mutableStateOf(listOf<String>()) }

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
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.game_title),
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        /* Matrice di tasti per la selezione della sequenza */
        ButtonsMatrix(
            buttons = buttons,
            onButtonClick = { char ->
                sequence = sequence + char
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        /* Box contenente la sequenza della partita */
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Text(
                text = sequence.joinToString(", "),
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        /* Riga pulsanti cancella e termina il gioco */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                12.dp,
                Alignment.CenterHorizontally
            )
        ) {
            Button(
                onClick = {
                    println("button pressed")
                    sequence = emptyList()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = stringResource(R.string.cancel_game),
                    fontSize = 18.sp
                )
            }
            Button(
                onClick = {
                    println("button pressed")
                    onGameEnd(sequence)
                    sequence = emptyList()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(R.string.end_game),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun ButtonsMatrix(buttons: List<GridButtonData>, onButtonClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(0.8f),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        for (i in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    12.dp,
                    Alignment.CenterHorizontally
                )
            ) {
                ColorCell(
                    char = buttons[i * 2].char,
                    boxColor = buttons[i * 2].boxColor,
                    textColor = buttons[i * 2].textColor,
                    modifier = Modifier.weight(1f),
                    onClick = onButtonClick
                )
                ColorCell(
                    char = buttons[i * 2 + 1].char,
                    boxColor = buttons[i * 2 + 1].boxColor,
                    textColor = buttons[i * 2 + 1].textColor,
                    modifier = Modifier.weight(1f),
                    onClick = onButtonClick
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
    modifier: Modifier,
    onClick: (String) -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    val normalColor = if (darkTheme) darken(boxColor, 0.85f) else boxColor
    val configuration = LocalConfiguration.current
    val buttonAspectRatio = if (configuration.screenHeightDp <= 740) 1.6f else 1f

    ElevatedButton(
        onClick = {
            println("button $char pressed")
            onClick(char)
        },
        modifier = modifier
            .aspectRatio(buttonAspectRatio)
            .sizeIn(minWidth = 72.dp, minHeight = 72.dp),
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