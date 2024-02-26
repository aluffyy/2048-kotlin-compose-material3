package com.alexjlockwood.twentyfortyeight.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alexjlockwood.twentyfortyeight.R
import com.alexjlockwood.twentyfortyeight.domain.Direction
import com.alexjlockwood.twentyfortyeight.domain.GridTileMovement
import kotlin.math.*

/**
 * Renders the 2048 game's home screen UI.
 */
@Composable
fun GameUi(
    modifier: Modifier = Modifier,
    gridTileMovements: List<GridTileMovement>,
    currentScore: Int,
    bestScore: Int,
    moveCount: Int,
    isPortrait: Boolean,
    onSwipeListener: (direction: Direction) -> Unit,
    onNewGameRequested: () -> Unit,
    onRestoreGameRequested: () -> Unit,
) {
    var swipeAngle by remember { mutableStateOf(0f) }
    ConstraintLayout(
        modifier.pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    change.consume()
                    swipeAngle = calcDegree(dragAmount.x, -(dragAmount.y))
                },
                onDragEnd = {
                    onSwipeListener(
                        when {
                            45 <= swipeAngle && swipeAngle < 135 -> Direction.NORTH
                            135 <= swipeAngle && swipeAngle < 225 -> Direction.WEST
                            225 <= swipeAngle && swipeAngle < 315 -> Direction.SOUTH
                            else -> Direction.EAST
                        }
                    )
                }
            )
        }
    ) {
        val (titleRef, actionAddRef, actionBackRef) = createRefs()
        val (gameGridRef, currentScoreRef, bestScoreRef) = createRefs()
        GameGrid(
            modifier = Modifier
                .constrainAs(gameGridRef) {
                    if (isPortrait) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(titleRef.bottom)
                        bottom.linkTo(bestScoreRef.top)
                    } else {
                        start.linkTo(titleRef.end)
                        end.linkTo(bestScoreRef.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .padding(16.dp),
            gridTileMovements = gridTileMovements,
            moveCount = moveCount,
        )
        TitleBox(
            modifier = Modifier
                .constrainAs(titleRef) {
                    if (isPortrait) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    } else {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                }
                .padding(32.dp),
        )
        ActionBox(
            modifier = Modifier
                .constrainAs(actionAddRef) {
                    if (isPortrait) {
                        end.linkTo(parent.end)
                        bottom.linkTo(titleRef.bottom)
                    } else {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(titleRef.end)
                    }
                }
                .padding(16.dp),
            imageVector = Icons.Filled.Add,
        ) { onNewGameRequested() }
        ActionBox(
            modifier = Modifier
                .constrainAs(actionBackRef) {
                    if (isPortrait) {
                        end.linkTo(actionAddRef.start)
                        bottom.linkTo(actionAddRef.bottom)
                    } else {
                        bottom.linkTo(actionAddRef.top)
                        end.linkTo(actionAddRef.end)
                    }
                }
                .padding(16.dp),
            imageVector = Icons.Filled.ArrowBack,
        ) { onRestoreGameRequested() }
        ScoreBox(
            modifier = Modifier
                .constrainAs(currentScoreRef) {
                    if (isPortrait) {
                        end.linkTo(bestScoreRef.start)
                        top.linkTo(bestScoreRef.top)
                    } else {
                        start.linkTo(bestScoreRef.start)
                        bottom.linkTo(bestScoreRef.top)
                    }
                }
                .padding(32.dp),
            text = "$currentScore",
            label = stringResource(R.string.msg_score)
        )
        ScoreBox(
            modifier = Modifier
                .constrainAs(bestScoreRef) {
                    if (isPortrait) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    } else {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                }
                .padding(32.dp),
            text = "$bestScore",
            label = stringResource(R.string.msg_best_score)
        )
    }
}

@Composable
private fun TitleBox(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.app_name),
    FontSize: TextUnit = 32.sp,
) {
    Column(modifier) {
        Text(
            text = text,
            fontSize = FontSize,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun ActionBox(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    onClick: () -> Unit,
) {
    Card(modifier) {
        Icon(
            modifier = Modifier.clickable { onClick() }.padding(16.dp),
            imageVector = imageVector,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = ""
        )
    }
}

@Composable
private fun ScoreBox(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    minFontSize: TextUnit = 16.sp
) {
    Column(modifier) {
        Text(
            text = text,
            fontSize = minFontSize * 2,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = label,
            fontSize = minFontSize,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

private fun calcDegree(x: Float, y: Float): Float = (atan2(y, x) * 180 / PI).toFloat().let { deg ->
        if (deg < 0) { deg + 360 }
        else { deg }
    }
