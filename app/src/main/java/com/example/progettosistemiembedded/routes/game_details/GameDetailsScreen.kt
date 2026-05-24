package com.example.progettosistemiembedded.routes.game_details

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.progettosistemiembedded.database.game.Game
import com.example.progettosistemiembedded.R

@Composable
fun GameDetailsScreen(modifier: Modifier = Modifier, game: Game) {

    val resTAG = "ResultsScreen:ResultsScreen"

    val sequenceScrollState = rememberScrollState()
    
    val gameSequence = game.sequence.split(",")

    Log.d(resTAG, "Creating GameDateilsScreen for game: $game.id) $game")

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {

        val (titleRef, subtitleRef, sequenceLenRef, errorIndexRef, sequenceCardRef) = createRefs()

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
            text = stringResource(R.string.result_of_game, game.id),
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
            text = stringResource(R.string.sequence_length, gameSequence.size),
            fontSize = 24.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.constrainAs(sequenceLenRef) {
                top.linkTo(subtitleRef.bottom, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        if (game.errorIndex >= 0) {
            Text(
                text = stringResource(R.string.failed_at, game.errorIndex),
                fontSize = 24.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.constrainAs(errorIndexRef) {
                    top.linkTo(sequenceLenRef.bottom, margin = 18.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .verticalScroll(sequenceScrollState)
                .constrainAs(sequenceCardRef) {
                    top.linkTo(if (game.errorIndex >= 0) errorIndexRef.bottom else sequenceLenRef.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        ) {
                Text(
                    text = buildAnnotatedString {
                        gameSequence.forEachIndexed { index, char ->
                            if (index > 0) {
                                append(", ")
                            }

                            if (game.errorIndex in 0..index) {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    append(char)
                                }
                            } else {
                                append(char)
                            }
                        }
                    },
                    fontSize = 24.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
        }
    }
}
