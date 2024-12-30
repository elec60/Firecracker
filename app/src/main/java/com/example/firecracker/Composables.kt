package com.example.firecracker

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularFirework(modifier: Modifier) {
    // Create an infinite transition controller for managing multiple animations
    val infiniteTransition = rememberInfiniteTransition()

    /**
     * Animation for scaling the circles in and out
     * - Starts at 0 (collapsed) and expands to 1 (full size)
     * - Duration: 2000ms (2 seconds)
     * - Repeats indefinitely
     */
    val outerCircleScale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        )
    )

    /**
     * Animation for rotating the entire firework pattern
     * - Rotates from 0 to 360 degrees
     * - Duration: 3000ms (3 seconds)
     * - Repeats indefinitely
     */
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        )
    )

    // Create the canvas for drawing the firework
    Canvas(
        modifier = modifier
    ) {
        // Calculate the center point and base radius for the firework
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 4f

        /**
         * Draw the outer circle of dots
         * - 16 evenly spaced green dots
         * - Rotates clockwise
         * - Scales with the outerCircleScale animation
         */
        val numDots = 16
        for (i in 0 until numDots) {
            // Calculate the position of each dot using polar coordinates
            val angle = (i * 360f / numDots + rotation) * (Math.PI / 180f).toFloat()
            val x = center.x + cos(angle) * radius * outerCircleScale
            val y = center.y + sin(angle) * radius * outerCircleScale

            drawCircle(
                color = Color(0xFF90EE90), // Light green
                radius = 8f,
                center = Offset(x, y)
            )
        }

        /**
         * Draw the inner circle of dots
         * - 16 evenly spaced yellow dots
         * - Rotates counter-clockwise (note the negative rotation)
         * - Radius is 70% of the outer circle
         */
        val innerRadius = radius * 0.7f
        for (i in 0 until numDots) {
            val angle = (i * 360f / numDots - rotation) * (Math.PI / 180f).toFloat()
            val x = center.x + cos(angle) * innerRadius * outerCircleScale
            val y = center.y + sin(angle) * innerRadius * outerCircleScale

            drawCircle(
                color = Color(0xFFFFFF00), // Yellow
                radius = 8f,
                center = Offset(x, y)
            )
        }

        /**
         * Draw the radiating lines (rays)
         * - 8 evenly spaced lavender lines
         * - Extends 20% beyond the outer circle radius
         * - Rotates with the animation
         */
        val rayLength = radius * 1.2f
        for (i in 0 until 8) {
            val angle = (i * 45f + rotation) * (Math.PI / 180f).toFloat()
            val startX = center.x + cos(angle) * radius
            val startY = center.y + sin(angle) * radius
            val endX = center.x + cos(angle) * rayLength
            val endY = center.y + sin(angle) * rayLength

            drawLine(
                color = Color(0xFFE6E6FA), // Lavender
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 8f,
                cap = StrokeCap.Round // Rounded ends for smoother appearance
            )
        }
    }
}

@Composable
fun OvalBurstAnimation(modifier: Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    /**
     * Controls the animation progress through different phases:
     * - 0.0: Initial state/delay
     * - 0.0 to 1.0: Expansion phase
     * - 1.0 to 2.0: Contraction phase
     */
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 700,  // Animation duration
                delayMillis = 500,     // Delay between cycles
                easing = LinearEasing  // Linear movement
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        // Calculate dimensions and positions
        val center = Offset(size.width / 2f, size.height / 2f)
        val baseRadius = size.minDimension / 4f        // Base circle radius
        val maxOvalLength = baseRadius * 0.8f          // Maximum oval length
        val ovalWidth = 10.dp.toPx()                   // Oval thickness

        // Create 8 evenly-spaced ovals
        for (i in 0..7) {
            val angle = i * 45f * (Math.PI / 180f).toFloat()

            // Calculate fixed positions
            val finalEndX = center.x + cos(angle) * (baseRadius + maxOvalLength)
            val finalEndY = center.y + sin(angle) * (baseRadius + maxOvalLength)
            val initialStartX = center.x + cos(angle) * baseRadius
            val initialStartY = center.y + sin(angle) * baseRadius

            /**
             * Calculate current positions based on animation phase:
             * 1. Initial state: Start and end points are the same
             * 2. Expansion: Fixed start, moving end point
             * 3. Contraction: Moving start, fixed end point
             */
            val (startX, startY, endX, endY) = when {
                // Initial state
                animationProgress == 0f -> {
                    Quadruple(initialStartX, initialStartY, initialStartX, initialStartY)
                }
                // Expansion phase
                animationProgress <= 1f -> {
                    val currentEndX =
                        initialStartX + cos(angle) * (maxOvalLength * animationProgress)
                    val currentEndY =
                        initialStartY + sin(angle) * (maxOvalLength * animationProgress)
                    Quadruple(initialStartX, initialStartY, currentEndX, currentEndY)
                }
                // Contraction phase
                else -> {
                    val progress = animationProgress - 1f
                    val currentStartX = initialStartX + cos(angle) * (maxOvalLength * progress)
                    val currentStartY = initialStartY + sin(angle) * (maxOvalLength * progress)
                    Quadruple(currentStartX, currentStartY, finalEndX, finalEndY)
                }
            }

            // Draw oval only if it has a non-zero length
            if (startX != endX || startY != endY) {
                drawLine(
                    color = Color(0xFFFFFF00),         // Bright yellow
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = ovalWidth,
                    cap = StrokeCap.Round              // Rounded ends for oval appearance
                )
            }
        }
    }
}

/**
 * Utility class for holding four float values representing start and end coordinates
 *
 * @property first X coordinate of start point
 * @property second Y coordinate of start point
 * @property third X coordinate of end point
 * @property fourth Y coordinate of end point
 */
private data class Quadruple(
    val first: Float,
    val second: Float,
    val third: Float,
    val fourth: Float
)
