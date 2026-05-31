package com.example.progettosistemiembedded.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

fun buildSequenceString(
    sequence: List<String>,
    errorIndex: Int,
    errorColor: Color,
    maxSequenceLength: Int? = null
): AnnotatedString {

    val visibleSequence = if (maxSequenceLength != null) {
        sequence.subList(
            0,
            sequence.size.coerceAtMost(maxSequenceLength)
        )
    } else {
        sequence
    }

    return buildAnnotatedString {
        visibleSequence.forEachIndexed { i, c ->

            if (i > 0) {
                append(", ")
            }

            if (errorIndex in 0..i) {
                withStyle(
                    style = SpanStyle(color = errorColor)
                ) {
                    append(c)
                }
            } else {
                append(c)
            }
        }

        if ( maxSequenceLength != null && sequence.size > maxSequenceLength ) {
            append(" ...")
        }
    }
}