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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import android.util.Log

/**
 * Restituisce una variante più scura del colore passato in input,
 * abbassandone la luminosità.
 *
 * Viene utilizzata per adattare l'aspetto grafico dei pulsanti
 * quando il sistema è impostato in tema scuro.
 *
 * @param color colore di partenza da scurire
 * @param factor fattore moltiplicativo applicato ai componenti RGB
 * @return una nuova istanza di [Color] più scura rispetto all'originale
 */
private fun darken(color: Color, factor: Float = 0.85f): Color {
    return color.copy(
        red = color.red * factor,
        green = color.green * factor,
        blue = color.blue * factor
    )
}

/**
 * Classe privata che rappresenta un pulsante della griglia di gioco.
 *
 * Ogni elemento contiene il carattere da visualizzare all'interno
 * del pulsante, il colore di sfondo della cella e il colore del testo.
 *
 * @property char carattere mostrato sul pulsante
 * @property boxColor colore di sfondo del pulsante
 * @property textColor colore del testo del pulsante
 */
private data class GridButtonData(
    val char: String,
    val boxColor: Color,
    val textColor: Color
)

/**
 * Composable principale che costruisce la schermata di gioco.
 *
 * La funzione gestisce lo stato della sequenza selezionata
 * dall'utente e organizza i componenti principali dell'interfaccia:
 * titolo, matrice di pulsanti colorati, area di riepilogo della
 * sequenza inserita e pulsanti finali di azione.
 *
 * Il layout si adatta automaticamente all'orientamento del dispositivo.
 * In modalità portrait gli elementi vengono disposti verticalmente,
 * mentre in landscape la schermata viene riorganizzata per sfruttare
 * meglio lo spazio orizzontale disponibile, posizionando la matrice
 * di pulsanti a sinistra e titolo, box e pulsanti di conferma a destra.
 *
 * Quando l'utente preme il pulsante di conferma, la sequenza corrente
 * viene inviata tramite la callback [onGameEnd] e successivamente
 * azzerata, mentra quando preme il pulsante di cancellazione, la
 * sequenza viene direttamente azzerata.
 *
 * @param modifier modificatore opzionale applicato al layout principale
 * @param onGameEnd callback invocata al termine della partita con la sequenza selezionata
 */
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
                    height = Dimension.fillToConstraints
                } else {
                    top.linkTo(titleRef.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(boxRef.top, margin = 24.dp)
                    width = Dimension.percent(0.8f)
                    height = Dimension.fillToConstraints
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
                        top.linkTo(matrixRef.bottom)
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

/**
 * Costruisce la matrice dei pulsanti colorati mostrati nella schermata principale.
 *
 * La funzione riceve una lista di elementi di tipo [GridButtonData]
 * e li dispone in una griglia composta da tre righe e due colonne.
 * Ogni cella della matrice viene rappresentata attraverso la composable
 * [ColorCell], che gestisce l'aspetto e il comportamento del singolo pulsante.
 *
 * La callback [onButtonClick] viene inoltrata a ciascuna cella per
 * permettere alla schermata principale di aggiornare la sequenza
 * selezionata dall'utente.
 *
 * @param buttons lista dei pulsanti da mostrare nella griglia
 * @param onButtonClick funzione invocata quando viene premuto un pulsante
 * @param modifier modificatore opzionale applicato al contenitore della matrice
 */
@Composable
private fun ButtonsMatrix(
    buttons: List<GridButtonData>,
    onButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val mtrTAG = "MainScreen:ButtonsMatrix"
    Log.d(mtrTAG, "Creating ButtonsMatrix with buttons $buttons")

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (i in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(
                    12.dp,
                    Alignment.CenterHorizontally
                )
            ) {
                ColorCell(
                    buttonData = buttons[i * 2],
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    onClick = onButtonClick
                )
                ColorCell(
                    buttonData = buttons[i * 2 + 1],
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    onClick = onButtonClick
                )
            }
        }
    }
}

/**
 * Rappresenta una singola cella interattiva della matrice di gioco.
 *
 * Questa composable visualizza un pulsante colorato contenente un carattere.
 * Al clic, il carattere associato viene inviato alla callback [onClick],
 * consentendo alla schermata principale di aggiornare la sequenza selezionata.
 *
 * La funzione adatta automaticamente il colore del pulsante al tema di sistema:
 * se il dispositivo è in modalità scura, il colore di sfondo viene reso
 * leggermente più scuro tramite la funzione [darken].
 *
 * Inoltre, il rapporto d'aspetto del pulsante viene modificato in base
 * all'altezza disponibile della finestra, così da migliorare la resa
 * dell'interfaccia su schermi piccoli o particolarmente compatti.
 *
 * @param buttonData contiene informazioni sy carattere associato, colore di box e testo
 * @param modifier modificatore applicato al pulsante
 * @param onClick callback eseguita alla pressione del pulsante
 */
@Composable
private fun ColorCell(
    buttonData: GridButtonData,
    modifier: Modifier,
    onClick: (String) -> Unit
) {
    val cellTAG = "MainScreen:ColorCell"
    Log.d(cellTAG, "Creating ColorCell with char -> $buttonData.char, boxColor -> $buttonData.boxColor, textColor -> $buttonData.textColor")

    val darkTheme = isSystemInDarkTheme()
    val normalColor = if (darkTheme) darken(buttonData.boxColor, 0.85f) else buttonData.boxColor

    ElevatedButton(
        onClick = {
            Log.d(cellTAG, "Button $buttonData.char pressed")
            onClick(buttonData.char)
        },
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = normalColor,
            contentColor = buttonData.textColor
        )
    ) {
        Text(
            text = buttonData.char,
            fontSize = 32.sp
        )
    }
}