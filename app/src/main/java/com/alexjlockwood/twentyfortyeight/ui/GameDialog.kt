package com.alexjlockwood.twentyfortyeight.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alexjlockwood.twentyfortyeight.R

@Composable
fun GameDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    onConfirmListener: () -> Unit,
    onDismissListener: () -> Unit,
) {
    val ok = stringResource(R.string.msg_ok)
    val cancel = stringResource(R.string.msg_cancel)
    AlertDialog(
        modifier = modifier,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = { TextButton(onClick = { onConfirmListener() }) { Text(ok) } },
        dismissButton = { TextButton(onClick = { onDismissListener() }) { Text(cancel) } },
        onDismissRequest = { onDismissListener() },
    )
}
