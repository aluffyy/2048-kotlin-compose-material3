package com.alexjlockwood.twentyfortyeight.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import com.alexjlockwood.twentyfortyeight.domain.GridTileMovement
import com.alexjlockwood.twentyfortyeight.viewmodel.GRID_SIZE
import kotlinx.coroutines.launch
import kotlin.math.*

private val GRID_TILE_RADIUS = 4.dp

/**
 * Renders a grid of tiles that animates when game moves are made.
 */
@Composable
fun GameGrid(
    modifier: Modifier = Modifier,
    gridTileMovements: List<GridTileMovement>,
    moveCount: Int,
) {
    BoxWithConstraints(modifier.wrapContentSize().aspectRatio(1f)) {
        val gridSizePx = with(LocalDensity.current) { min(maxWidth.toPx(), maxHeight.toPx()) }
        val tileMarginPx = with(LocalDensity.current) { 4.dp.toPx() }
        val tileSizePx = ((gridSizePx - tileMarginPx * (GRID_SIZE - 1)) / GRID_SIZE)
            .coerceAtLeast(0f)
        val tileOffsetPx = tileSizePx + tileMarginPx
        GameGrid(
            gridTileMovements = gridTileMovements,
            moveCount = moveCount,
            tileSizePx = tileSizePx,
            tileOffsetPx = tileOffsetPx,
            emptyTileColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun GameGrid(
    modifier: Modifier = Modifier,
    gridTileMovements: List<GridTileMovement>,
    moveCount: Int,
    tileSizePx: Float,
    tileOffsetPx: Float,
    emptyTileColor: Color = getEmptyTileColor(isSystemInDarkTheme()),
) {
    Box(
        modifier.drawBehind {
            // Draw the background empty tiles.
            for (row in 0 until GRID_SIZE) {
                for (col in 0 until GRID_SIZE) {
                    drawRoundRect(
                        color = emptyTileColor,
                        topLeft = Offset(col * tileOffsetPx, row * tileOffsetPx),
                        size = Size(tileSizePx, tileSizePx),
                        cornerRadius = CornerRadius(GRID_TILE_RADIUS.toPx()),
                    )
                }
            }
        }
    ) {
        for (gridTileMovement in gridTileMovements) {
            // Each grid tile is laid out at (0,0) in the box. Shifting tiles are then translated
            // to their correct position in the grid, and added tiles are scaled from 0 to 1.
            val (fromGridTile, toGridTile) = gridTileMovement
            val fromScale = if (fromGridTile == null) 0f else 1f
            val toOffset = Offset(
                toGridTile.cell.col * tileOffsetPx,
                toGridTile.cell.row * tileOffsetPx
            )
            val fromOffset = fromGridTile?.let {
                Offset(
                    it.cell.col * tileOffsetPx,
                    it.cell.row * tileOffsetPx
                )
            } ?: toOffset

            // In 2048, tiles are frequently being removed and added to the grid. As a result,
            // the order in which grid tiles are rendered is constantly changing after each
            // recomposition. In order to ensure that each tile animates from its correct
            // starting position, it is critical that we assign each tile a unique ID using
            // the key() function.
            key(toGridTile.tile.id) {
                val animatedScale = remember { Animatable(fromScale) }
                val animatedOffset = remember { Animatable(fromOffset, Offset.VectorConverter) }
                GameTile(
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = animatedScale.value,
                            scaleY = animatedScale.value,
                            translationX = animatedOffset.value.x,
                            translationY = animatedOffset.value.y,
                        ),
                    num = toGridTile.tile.num,
                    tileSize = Dp(tileSizePx / LocalDensity.current.density),
                    tileColor = getTileColor(toGridTile.tile.num, isSystemInDarkTheme()),
                )
                LaunchedEffect(moveCount) {
                    launch { animatedScale.animateTo(1f, tween(200, 50)) }
                    launch { animatedOffset.animateTo(toOffset, tween(100)) }
                }
            }
        }
    }
}

@Composable
private fun GameTile(
    modifier: Modifier = Modifier,
    num: Int,
    tileSize: Dp,
    tileColor: Color = Color.Black,
    textColor: Color = Color.White,
    fontSize: TextUnit = 18.sp,
) {
    Text(
        text = "$num",
        modifier = modifier
            .background(
                color = tileColor,
                shape = RoundedCornerShape(GRID_TILE_RADIUS),
            )
            .size(tileSize)
            .wrapContentSize(),
        color = textColor,
        fontSize = fontSize,
    )
}

private fun getTileColor(num: Int, isDarkTheme: Boolean): Color {
    return when (num) {
        2 -> Color(if (isDarkTheme) 0xff4e6cef else 0xff50c0e9)
        4 -> Color(if (isDarkTheme) 0xff3f51b5 else 0xff1da9da)
        8 -> Color(if (isDarkTheme) 0xff8e24aa else 0xffcb97e5)
        16 -> Color(if (isDarkTheme) 0xff673ab7 else 0xffb368d9)
        32 -> Color(if (isDarkTheme) 0xffc00c23 else 0xffff5f5f)
        64 -> Color(if (isDarkTheme) 0xffa80716 else 0xffe92727)
        128 -> Color(if (isDarkTheme) 0xff0a7e07 else 0xff92c500)
        256 -> Color(if (isDarkTheme) 0xff056f00 else 0xff7caf00)
        512 -> Color(if (isDarkTheme) 0xffe37c00 else 0xffffc641)
        1024 -> Color(if (isDarkTheme) 0xffd66c00 else 0xffffa713)
        2048 -> Color(if (isDarkTheme) 0xffcf5100 else 0xffff8a00)
        4096 -> Color(if (isDarkTheme) 0xff80020a else 0xffcc0000)
        8192 -> Color(if (isDarkTheme) 0xff303f9f else 0xff0099cc)
        16384 -> Color(if (isDarkTheme) 0xff512da8 else 0xff9933cc)
        else -> Color.Black
    }
}

private fun getEmptyTileColor(isDarkTheme: Boolean): Color {
    return Color(if (isDarkTheme) 0xff444444 else 0xffdddddd)
}
