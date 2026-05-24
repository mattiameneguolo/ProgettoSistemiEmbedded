package com.example.progettosistemiembedded.routes.game

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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.progettosistemiembedded.R
import com.example.progettosistemiembedded.audio.GameSoundManager
import kotlinx.coroutines.delay

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
 * Restituisce una variante più chiara del colore passato in input,
 * aumentando la luminosità dei componenti RGB.
 *
 * Viene utilizzata per evidenziare il pulsante attivo durante
 * la proposta del computer e durante il feedback del giocatore.
 *
 * @param color colore di partenza da schiarire
 * @param factor fattore di schiarimento applicato ai componenti RGB
 * @return una nuova istanza di [Color] più chiara rispetto all'originale
 */
private fun lighten(color: Color, factor: Float = 0.35f): Color {
    return color.copy(
        red = color.red + (1f - color.red) * factor,
        green = color.green + (1f - color.green) * factor,
        blue = color.blue + (1f - color.blue) * factor
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
 * Enumerazione privata che rappresenta le varie fasi di gioco,
 * utilizzata per gestire il l'annullamento/terminazione della partita,
 * l'attivazione dei vari tasti e la gestione della routine incaricata
 * di fornire la sequenza di tasti da premere
 *
 * @property IDLE = In attesa di avvio della partita
 * @property COMPUTER_PLAYING = La routine mostra la sequenza
 * @property PAUSED = Gioco in pausa
 * @property PLAYER_TURN = Gioco in attesa di input utente
 * @property GAME_OVER = Partita terminata
 * @property WAITING_NEXT_SEQUENCE = Delay fra input utente e COMPUTER_PLAYING
 */
private enum class GamePhase {
    IDLE,
    COMPUTER_PLAYING,
    PAUSED,
    PLAYER_TURN,
    GAME_OVER,
    WAITING_NEXT_SEQUENCE
}

/**
 * TODO: Aggiornare descrizione funzione
 *
 * @param modifier modificatore opzionale applicato al layout principale
 * @param onGameEnd callback invocata per terminare e salvare la partita
 * @param onGameCanceled callback invocata per terminare la partita senza salvarla
 */
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    onGameEnd: (sequence: List<String>, errorIndex: Int) -> Unit,
    onGameCanceled: () -> Unit
) {
    val gameTAG = "GameScreen:GameScreen"

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val context = LocalContext.current

    val soundManager = remember {
        GameSoundManager(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }

    val buttons = listOf(
        GridButtonData("R", Color.Red, Color.White),
        GridButtonData("G", Color.Green, Color.Black),
        GridButtonData("B", Color.Blue, Color.White),
        GridButtonData("M", Color.Magenta, Color.White),
        GridButtonData("Y", Color.Yellow, Color.Black),
        GridButtonData("C", Color.Cyan, Color.Black)
    )

    var targetSequence by rememberSaveable { mutableStateOf(listOf<String>()) }
    var playerSequence by rememberSaveable { mutableStateOf(listOf<String>()) }

    var gamePhase by rememberSaveable { mutableStateOf(GamePhase.IDLE) }
    var playbackIndex by rememberSaveable { mutableStateOf(0) }

    var activeColor by rememberSaveable { mutableStateOf<String?>(null) }
    var errorIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    var playerFeedbackTick by rememberSaveable { mutableStateOf(0) }

    val sequenceScrollState = rememberScrollState()

    val isGameRunning = gamePhase != GamePhase.IDLE && gamePhase != GamePhase.GAME_OVER

    fun resetLocalGame() {
        Log.d(gameTAG, "Resetting local game")

        targetSequence = emptyList()
        playerSequence = emptyList()
        playbackIndex = 0
        activeColor = null
        errorIndex = null
        gamePhase = GamePhase.IDLE
    }

    fun startNewGame() {
        Log.d(gameTAG, "Game started")

        targetSequence = listOf(buttons.random().char)
        playerSequence = emptyList()
        playbackIndex = 0
        activeColor = null
        errorIndex = null
        gamePhase = GamePhase.COMPUTER_PLAYING
    }

    fun finishGameManuallyOrCancel() {
        /*
         * Fine partita.
         * Rimuovo l'ultimo carattere della targetSequence perché è il carattere
         * aggiunto dal computer per il turno successivo/non completato.
         */
        val sequenceToSave = targetSequence.dropLast(1)

        /*
         * Se dopo aver rimosso l'ultimo carattere non rimane nulla,
         * significa che il giocatore non ha completato nessuna sequenza valida.
         * Quindi annullo la partita senza salvarla.
         */
        if (sequenceToSave.isEmpty()) {
            Log.d(gameTAG, "Game canceled, no completed sequence to save")
            resetLocalGame()
            onGameCanceled()
            return
        }

        targetSequence = sequenceToSave
        errorIndex = null
        activeColor = null
        playbackIndex = 0
        gamePhase = GamePhase.GAME_OVER

        Log.d(
            gameTAG,
            "Game ended manually without error. Saved sequence: $sequenceToSave"
        )

        /**
         * Uso -1 come valore convenzionale per dire:
         * partita terminata manualmente, nessun errore.
         */
        onGameEnd(sequenceToSave, -1)
    }

    /**
     * Gestione comportamento tasto back:
     *  - stato partita = GAME_OVER -> Torno alla home salvando la partita
     *  - stato partita != GAME_OVER -> Eseguo finishGameManuallyOrCancel() per verificare se salvare o no la partita
     */
    BackHandler(enabled = gamePhase != GamePhase.IDLE) {
        if (gamePhase == GamePhase.GAME_OVER) {
            Log.d(gameTAG, "Back pressed after game over, saving game $targetSequence")
            onGameEnd(targetSequence, errorIndex ?: -1)
        } else {
            Log.d(gameTAG, "Back pressed during game, ending the game...")
            finishGameManuallyOrCancel()
        }
    }

    /**
     * Coroutine che gestisce lo stato della partita:
     *   - GamePhase.WAITING_NEXT_SEQUENCE -> Delay di 1 secondo prima di mostrare la prossima sequenza
     *   - GamePhase.COMPUTER_PLAYING -> Avvio routine di riproduzione della sequenza
     * Si avvia automaticamente al cambio del valore di gamePhase, playbackIndex o targetSequence
     *
     * @param gamePhase fase di gioco corrente
     * @param playbackIndex indice della cella corrente da riprodurre
     * @param targetSequence sequenza da riprodurre
     */
    LaunchedEffect(gamePhase, playbackIndex, targetSequence) {
        if (gamePhase == GamePhase.WAITING_NEXT_SEQUENCE) {
            delay(1000)

            playbackIndex = 0
            activeColor = null
            gamePhase = GamePhase.COMPUTER_PLAYING
        }

        if (gamePhase == GamePhase.COMPUTER_PLAYING && targetSequence.isNotEmpty()) {
            if (playbackIndex < targetSequence.size) {
                /* Riproduzione sequenza da parte del computer */
                playerSequence = emptyList()
                activeColor = null

                delay(150)

                activeColor = targetSequence[playbackIndex]
                soundManager.play(targetSequence[playbackIndex])

                delay(400)

                activeColor = null

                delay(250)

                playbackIndex += 1
            } else {
                /* Riproduzione sequenza terminata, passa il turno al giocatore */
                playbackIndex = 0
                playerSequence = emptyList()
                activeColor = null
                gamePhase = GamePhase.PLAYER_TURN

                Log.d(gameTAG, "Computer sequence ended, player turn started")
            }
        }
    }

    /**
     * Coroutine che ripulisce l'activeColor 180ms dopo la pressione di
     * un tasto della matrice da parte dell'utente
     */
    LaunchedEffect(playerFeedbackTick, gamePhase) {
        if (playerFeedbackTick > 0 && gamePhase == GamePhase.PLAYER_TURN) {
            delay(180)

            if (gamePhase == GamePhase.PLAYER_TURN) {
                activeColor = null
            }
        }
    }

    /**
     * Coroutine che fa in modo che lo scroll nel box di testo mostri
     * sempre l'ultimo carattere inserito nel giocatore, scrollando sempre verso
     * il basso all'aggiunta di un nuovo carattere nella sequenza
     */
    LaunchedEffect(playerSequence.size, targetSequence.size, gamePhase) {
        sequenceScrollState.animateScrollTo(sequenceScrollState.maxValue)
    }

    val sequenceText = buildAnnotatedString {
        when (gamePhase) {

            /* Pulizia TextBox */
            GamePhase.IDLE,
            GamePhase.COMPUTER_PLAYING,
            GamePhase.PAUSED -> {
                append("")
            }

            /* Scrittura sequenza del giocatore nella TextBox */
            GamePhase.PLAYER_TURN -> {
                append(playerSequence.joinToString(", "))
            }

            /* Scrittura sequenza giocatore con segnalazione errore nella TextBox */
            GamePhase.GAME_OVER -> {
                targetSequence.forEachIndexed { index, color ->
                    if (index > 0) {
                        append(", ")
                    }

                    /* Coloro caratteri errati di rosso */
                    if (errorIndex != null && index >= errorIndex!!) {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.error
                            )
                        ) {
                            append(color)
                        }
                    } else {
                        append(color)
                    }
                }
            }

            /* Scrittura sequenza del giocatore nella TextBox */
            GamePhase.WAITING_NEXT_SEQUENCE -> {
                append(playerSequence.joinToString(", "))
            }
        }
    }

    Log.d(
        gameTAG,
        "Creating GameScreen with phase $gamePhase, target $targetSequence, player $playerSequence"
    )

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
            modifier = Modifier.constrainAs(titleRef) {
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
            inputEnabled = gamePhase == GamePhase.PLAYER_TURN,
            activeColor = activeColor,
            onButtonClick = { char ->
                if (gamePhase != GamePhase.PLAYER_TURN) {
                    /* I pulsanti non devono fare nulla, esco dalla lambda */
                    return@ButtonsMatrix
                }

                Log.d(gameTAG, "Player pressed $char")
                soundManager.play(char)

                activeColor = char
                playerFeedbackTick += 1

                playerSequence = playerSequence + char
                val currentIndex = playerSequence.lastIndex

                if (char != targetSequence[currentIndex]) {
                    /* Input errato, partita persa */
                    errorIndex = currentIndex
                    gamePhase = GamePhase.GAME_OVER
                    soundManager.play("error")

                    Log.d(
                        gameTAG,
                        "Wrong button. Expected ${targetSequence[currentIndex]}, received $char. Game over."
                    )
                } else if (playerSequence.size == targetSequence.size) {
                    /* Sequenza replicata correttamente */
                    Log.d(gameTAG, "Sequence completed correctly")

                    /* Aggiungo nuovo carattere alla targetSequence */
                    targetSequence = targetSequence + buttons.random().char
                    playbackIndex = 0
                    activeColor = null
                    errorIndex = null
                    gamePhase = GamePhase.WAITING_NEXT_SEQUENCE
                }
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
                .verticalScroll(sequenceScrollState)
        ) {
            Text(
                text = sequenceText,
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
                8.dp,
                Alignment.CenterHorizontally
            )
        ) {
            Button(
                onClick = {
                    Log.d(gameTAG, "Start Game pressed")
                    startNewGame()
                },
                enabled = gamePhase == GamePhase.IDLE,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(R.string.start_game),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    if (gamePhase == GamePhase.COMPUTER_PLAYING) {
                        Log.d(gameTAG, "Game paused")
                        gamePhase = GamePhase.PAUSED
                        activeColor = null
                    } else if (gamePhase == GamePhase.PAUSED) {
                        Log.d(gameTAG, "Game resumed")
                        gamePhase = GamePhase.COMPUTER_PLAYING
                    }
                },
                enabled = gamePhase == GamePhase.COMPUTER_PLAYING ||
                        gamePhase == GamePhase.PAUSED,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = if (gamePhase == GamePhase.PAUSED)
                        stringResource(R.string.resume_game)
                        else stringResource(R.string.pause_game),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    Log.d(gameTAG, "End game pressed")

                    if (gamePhase == GamePhase.GAME_OVER) {
                        onGameEnd(targetSequence, errorIndex ?: -1)
                    } else {
                        finishGameManuallyOrCancel()
                    }
                },
                enabled = isGameRunning || gamePhase == GamePhase.GAME_OVER,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(
                    text = stringResource(R.string.end_game),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
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
    inputEnabled: Boolean,
    activeColor: String?,
    onButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val mtrTAG = "GameScreen:ButtonsMatrix"
    Log.d(mtrTAG, "Creating ButtonsMatrix with buttons $buttons")

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (i in 0 until 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(
                    12.dp,
                    Alignment.CenterHorizontally
                )
            ) {
                ColorCell(
                    buttonData = buttons[i * 2],
                    inputEnabled = inputEnabled,
                    isActive = activeColor == buttons[i * 2].char,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    onClick = onButtonClick
                )

                ColorCell(
                    buttonData = buttons[i * 2 + 1],
                    inputEnabled = inputEnabled,
                    isActive = activeColor == buttons[i * 2 + 1].char,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
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
    inputEnabled: Boolean,
    isActive: Boolean,
    modifier: Modifier,
    onClick: (String) -> Unit
) {
    val cellTAG = "GameScreen:ColorCell"

    val darkTheme = isSystemInDarkTheme()
    val normalColor = if (darkTheme) {
        darken(buttonData.boxColor, 0.85f)
    } else {
        buttonData.boxColor
    }

    val buttonContainerColor = if (isActive) {
        lighten(normalColor)
    } else {
        darken(normalColor)
    }

    ElevatedButton(
        onClick = {
            Log.d(cellTAG, "Button ${buttonData.char} pressed")
            onClick(buttonData.char)
        },
        enabled = inputEnabled,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonContainerColor,
            contentColor = buttonData.textColor,
            disabledContainerColor = buttonContainerColor,
            disabledContentColor = buttonData.textColor
        )
    ) {
        Text(
            text = buttonData.char,
            fontSize = 32.sp
        )
    }
}