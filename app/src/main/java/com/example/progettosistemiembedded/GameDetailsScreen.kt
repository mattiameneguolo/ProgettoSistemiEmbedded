package com.example.progettosistemiembedded

import androidx.compose.runtime.Composable
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlin.collections.joinToString

@Composable
fun GameDetailsScreen(modifier: Modifier = Modifier, gameId: Int, game: Game) {

    val resTAG = "ResultsScreen:ResultsScreen"

    val sequenceScrollState = rememberScrollState()

    Log.d(resTAG, "Creating GameDateilsScreen for game: $gameId) $game")

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {

        val (titleRef, subtitleRef, correctSequenceLenRef, sequenceCardRef) = createRefs()

        Text(
            text = stringResource(R.string.history_title),
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = "Result of game $gameId:",
            fontSize = 28.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.constrainAs(subtitleRef) {
                top.linkTo(titleRef.bottom, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = "Correct sequence length: ${game.sequence.size - 1}",
            fontSize = 24.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.constrainAs(correctSequenceLenRef) {
                top.linkTo(subtitleRef.bottom, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .verticalScroll(sequenceScrollState)
                .constrainAs(sequenceCardRef) {
                    top.linkTo(correctSequenceLenRef.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        ) {
                Text(
                    text = game.sequence.joinToString(", "),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
        }
    }
}
