package com.example.progettosistemiembedded

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import android.util.Log

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
private data class GridButtonData(
    val char: String,
    val boxColor: Color,
    val textColor: Color
)

@Composable
fun MainScreen(modifier: Modifier = Modifier, onGameEnd: (sequence: List<String>) -> Unit) {

    val mainTAG = "MainScreen:MainScreen"

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var sequence by rememberSaveable { mutableStateOf(listOf<String>()) }

    val buttons = listOf(
        GridButtonData("R", Color.Red, Color.White),
        GridButtonData("G", Color.Green, Color.Black),
        GridButtonData("B", Color.Blue, Color.White),
        GridButtonData("M", Color.Magenta, Color.White),
        GridButtonData("Y", Color.Yellow, Color.Black),
        GridButtonData("C", Color.Cyan, Color.Black)
    )

    Log.d(mainTAG, "Creating MainScreen with buttons $buttons and orientation ${if (isLandscape) "landscape" else "portrait"}")

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        val (titleRef, matrixRef, boxRef, actionsRef) = createRefs()

        Text(
            text = stringResource(R.string.game_title),
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(titleRef) {
                    if (isLandscape) {
                        top.linkTo(parent.top)
                        start.linkTo(matrixRef.end)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    } else {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                }
        )

        ButtonsMatrix(
            buttons = buttons,
            onButtonClick = { char ->
                Log.d(mainTAG, "Button $char pressed, adding to sequence $sequence")
                sequence = sequence + char
                Log.d(mainTAG, "Sequence updated: $sequence")
            },
            modifier = Modifier.constrainAs(matrixRef) {
                if (isLandscape) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(boxRef.start, margin = 16.dp)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                } else {
                    top.linkTo(titleRef.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.percent(0.8f)
                }
            }
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .constrainAs(boxRef) {
                    if (isLandscape) {
                        start.linkTo(matrixRef.end, margin = 16.dp)
                        end.linkTo(parent.end)
                        bottom.linkTo(actionsRef.top, margin = 24.dp)
                        width = Dimension.fillToConstraints
                    } else {
                        top.linkTo(matrixRef.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(actionsRef.top)
                        width = Dimension.fillToConstraints
                    }
                }
                .height(100.dp)
        ) {
            Text(
                text = sequence.joinToString(", "),
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Row(
            modifier = Modifier
                .constrainAs(actionsRef) {
                    if (isLandscape) {
                        start.linkTo(boxRef.start)
                        end.linkTo(boxRef.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    } else {
                        top.linkTo(boxRef.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                },
            horizontalArrangement = Arrangement.spacedBy(
                12.dp,
                Alignment.CenterHorizontally
            )
        ) {
            Button(
                onClick = {
                    Log.d(mainTAG, "Game canceled, resetting sequence $sequence")
                    sequence = emptyList()
                    Log.d(mainTAG, "Sequence cleared: $sequence")
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
                    Log.d(mainTAG, "Game ended with $sequence")
                    onGameEnd(sequence)
                    sequence = emptyList()
                    Log.d(mainTAG, "Sequence cleared: $sequence")
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
private fun ButtonsMatrix(
    buttons: List<GridButtonData>,
    onButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val mtrTAG = "MainScreen:ButtonsMatrix"
    Log.d(mtrTAG, "Creating ButtonsMatrix with buttons $buttons")

    Column(
        modifier = modifier,
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
private fun ColorCell(
    char: String,
    boxColor: Color,
    textColor: Color,
    modifier: Modifier,
    onClick: (String) -> Unit
) {
    val cellTAG = "MainScreen:ColorCell"
    Log.d(cellTAG, "Creating ColorCell with char -> $char, boxColor -> $boxColor, textColor -> $textColor")

    val darkTheme = isSystemInDarkTheme()
    val normalColor = if (darkTheme) darken(boxColor, 0.85f) else boxColor
    val buttonAspectRatio = if (LocalWindowInfo.current.containerDpSize.height <= 740.dp) 1.6f else 1f

    ElevatedButton(
        onClick = {
            Log.d(cellTAG, "Button $char pressed")
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