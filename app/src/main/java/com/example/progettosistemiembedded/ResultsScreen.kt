package com.example.progettosistemiembedded

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlin.collections.joinToString

const val mTAG = "ResultsScreen"

@Composable
fun ResultsScreen(modifier: Modifier = Modifier, games: List<List<String>>) {

    Log.d(mTAG, "Creating ResultsScreen with list of games: $games")

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val maxSequenceLength: Int = (if (isLandscape) 12 else 8)

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        val (titleRef, resultsRef) = createRefs()

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

        Column(
            modifier = Modifier
                .constrainAs(resultsRef) {
                    top.linkTo(titleRef.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .verticalScroll(rememberScrollState())
        ) {
            for (game in games.reversed()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(100.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = game.size.toString(),
                            fontSize = 24.sp
                        )

                        Text(
                            text = game.subList(0, Math.min(game.size, maxSequenceLength))
                                .joinToString(", ") + (if (game.size > maxSequenceLength) " ..." else ""),
                            fontSize = 24.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }

}