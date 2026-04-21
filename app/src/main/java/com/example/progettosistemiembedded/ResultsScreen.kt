package com.example.progettosistemiembedded

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

/**
 * Composable che mostra la schermata dei risultati delle partite giocate.
 *
 * La funzione costruisce un layout composto da un titolo nella parte
 * superiore e da una lista contenente la cronologia delle
 * partite salvate. Ogni elemento della lista rappresenta una singola
 * partita e viene visualizzato tramite la composable [ResultRow].
 *
 * Le partite vengono mostrate in ordine inverso rispetto a quello
 * ricevuto in input, così da visualizzare per prime le più recenti..
 *
 * @param modifier modificatore opzionale applicato al layout principale
 * @param games lista delle partite concluse, dove ogni partita è rappresentata come lista di stringhe
 */
@Composable
fun ResultsScreen(modifier: Modifier = Modifier, games: List<Game>) {

    val resTAG = "ResultsScreen:ResultsScreen"

    Log.d(resTAG, "Creating ResultsScreen with list of games: $games")

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

        LazyColumn(
            modifier = Modifier
                .constrainAs(resultsRef) {
                    top.linkTo(titleRef.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        ) {
            items(items=games.reversed(), key={ it.id }) { game ->
                Log.d(resTAG, "Generating ROW with game: $game")
                ResultRow(game.sequence)
            }
        }
    }
}

/**
 * Composable che rappresenta una singola riga della schermata dei risultati.
 *
 * Ogni riga mostra due informazioni principali relative a una partita:
 * il numero totale di elementi presenti nella sequenza e una
 * rappresentazione testuale della sequenza stessa.
 *
 * La lunghezza massima della sequenza mostrata viene adattata in base
 * all'orientamento del dispositivo: in modalità landscape viene
 * consentita una visualizzazione più estesa, mentre in portrait la
 * sequenza viene abbreviata prima. Se la partita contiene più elementi
 * del limite previsto, il testo viene troncato e completato con "...".
 *
 * @param game lista di stringhe che rappresenta la sequenza registrata per una partita
 */
@Composable
private fun ResultRow(
    game: List<String>
) {
    val rowTAG = "ResultsScreen:ResultRow"

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val maxSequenceLength: Int = (if (isLandscape) 12 else 8)

    Log.d(rowTAG, "Creating ResultRow with game: $game, sequence length: ${game.size}")

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