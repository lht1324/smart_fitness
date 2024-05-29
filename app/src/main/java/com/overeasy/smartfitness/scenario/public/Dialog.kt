package com.overeasy.smartfitness.scenario.public

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.overeasy.smartfitness.dpToSp
import com.overeasy.smartfitness.ui.theme.fontFamily

@Composable
fun Dialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    customDescription: (@Composable () -> Unit)? = null,
    confirmText: String,
    dismissText: String? = null,
    onClickConfirm: () -> Unit,
    onClickDismiss: (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            DialogButton(
                text = confirmText,
                onClick = onClickConfirm
            )
        },
        modifier = modifier,
        dismissButton = if (dismissText != null && onClickDismiss != null) {
            @Composable {
                DialogButton(
                    text = dismissText,
                    onClick = onClickDismiss
                )
            }
        } else {
            null
        },
        title = if (title != null) {
            @Composable {
                Text(
                    text = title,
                    color = Color.Black,
                    fontSize = 20.dpToSp(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
            }
        } else {
            null
        },
        text = if (customDescription != null) {
            @Composable {
                customDescription()
            }
        } else {
            if (description != null) {
                @Composable {
                    Text(
                        text = description,
                        color = Color.Black,
                        fontSize = 18.dpToSp(),
                        fontWeight = FontWeight.Normal,
                        fontFamily = fontFamily
                    )
                }
            } else {
                null
            }
        },
        titleContentColor = Color.Black,
        containerColor = Color.White
    )
}

@Composable
private fun DialogButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(
                color = Color.Transparent,
                shape = AbsoluteRoundedCornerShape(10.dp)
            )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(5.dp),
            color = Color.Black,
            fontSize = 16.dpToSp(),
            fontWeight = FontWeight.Medium,
            fontFamily = fontFamily
        )
    }
}