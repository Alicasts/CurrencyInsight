package com.alicasts.currencyinsight.presentation.currency_comparison.details_fragment.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyHistoricalData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BidChartComposable(
    currencyComparisonDetails: CurrencyComparisonDetails,
) {
    val chartValues = buildChartValues(currencyComparisonDetails)
    val timestamps = buildTimestamps(currencyComparisonDetails)

    val chartColor = Color.Blue
    val labelFontSize = 12.sp
    val adjustedMinValue = chartValues.min() * 0.5f
    val adjustedMaxValue = chartValues.max() * 1.1f

    var zoomFactor by remember { mutableFloatStateOf(0.8f) }

    val scrollState = rememberScrollState()

    LaunchedEffect(zoomFactor) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column {
        ChartHeader(
            currencyComparisonDetails = currencyComparisonDetails,
            zoomFactor =  zoomFactor,
            onZoomChange = {
            newZoomFactor ->
            zoomFactor = newZoomFactor.coerceIn(0.8f, 1.6f)
        })

        Box(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .fillMaxWidth()
        ) {
            Canvas(
                modifier = Modifier
                    .width((chartValues.size * 80.dp * zoomFactor) )
                    .height(300.dp)
                    .testTag("BidChart")
            ) {
                drawChart(
                    chartValues = chartValues,
                    timestamps = timestamps,
                    chartColor = chartColor,
                    adjustedMinValue = adjustedMinValue,
                    adjustedMaxValue = adjustedMaxValue,
                    labelFontSize = labelFontSize
                )
            }
        }
    }
}

@Composable
fun ChartHeader(
    currencyComparisonDetails: CurrencyComparisonDetails,
    zoomFactor: Float,
    onZoomChange: (Float) ->
    Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.W300,
                        fontStyle = FontStyle.Italic,
                        fontSize = 20.sp
                    )
                ) {
                    append(currencyComparisonDetails.code)
                    append(" x ")
                    append(currencyComparisonDetails.codein)
                }
            },
            modifier = Modifier.padding(start = 12.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ZoomButton(
                label = "-",
                enabled = zoomFactor > 0.8f
            ) {
                onZoomChange(zoomFactor - 0.2f)
            }
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            ZoomButton(
                label = "+",
                enabled = zoomFactor < 1.6f
            ) {
                onZoomChange(zoomFactor + 0.2f)
            }
        }
    }
}

@Composable
fun ZoomButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        modifier = Modifier.size(32.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(label)
    }
}

fun buildChartValues(currencyComparisonDetails: CurrencyComparisonDetails): List<Float> {
    return listOf(currencyComparisonDetails.bid.toFloatOrNull() ?: 0f) +
            currencyComparisonDetails.historicalData.mapNotNull { it.bid.toFloatOrNull() }
}

fun buildTimestamps(currencyComparisonDetails: CurrencyComparisonDetails): List<String> {
    val dateFormatter = SimpleDateFormat("dd/MM", Locale.getDefault())
    val timestamps = currencyComparisonDetails.historicalData.map {
        dateFormatter.format(Date(it.timestamp.toLong() * 1000))
    }.toMutableList()

    timestamps.add(0, dateFormatter.format(Date(currencyComparisonDetails.timestamp.toLong() * 1000)))
    return timestamps
}

fun DrawScope.drawChart(
    chartValues: List<Float>,
    timestamps: List<String>,
    chartColor: Color,
    adjustedMinValue: Float,
    adjustedMaxValue: Float,
    labelFontSize: TextUnit
) {
    val width = size.width + 120
    val height = size.height - 20
    val xOffset = 24.dp.toPx()
    val yOffset = adjustedMinValue / adjustedMaxValue * height

    val reversedChartValues = chartValues.reversed()
    val reversedTimestamps = timestamps.reversed()
    val spacing = (width - xOffset) / reversedChartValues.size

    val path = buildChartPath(reversedChartValues, xOffset, height, yOffset, spacing, adjustedMaxValue)
    val gradientPath = buildGradientPath(reversedChartValues, xOffset, height, yOffset, spacing, adjustedMaxValue)

    drawPath(
        path = gradientPath,
        brush = Brush.verticalGradient(
            colors = listOf(chartColor.copy(alpha = 0.5f), Color.Transparent),
            startY = 0f,
            endY = height)
    )
    drawPath(
        path = path,
        color = chartColor,
        style = Stroke(
            width = 2.dp.toPx()
        )
    )

    drawLabels(
        chartValues = reversedChartValues,
        timestamps = reversedTimestamps,
        xOffset = xOffset,
        height = height,
        yOffset = yOffset,
        spacing = spacing,
        adjustedMaxValue = adjustedMaxValue,
        labelFontSize = labelFontSize
    )
}

fun buildChartPath(
    chartValues: List<Float>,
    xOffset: Float,
    height: Float,
    yOffset: Float,
    spacing: Float,
    adjustedMaxValue: Float
): Path {
    return Path().apply {
        chartValues.forEachIndexed { index, value ->
            val x = xOffset + index * spacing
            val y = height - (value / adjustedMaxValue * (height - yOffset))
            if (index == 0) {
                moveTo(x, y)
            } else {
                lineTo(x, y)
            }
        }
    }
}

fun buildGradientPath(
    chartValues: List<Float>,
    xOffset: Float,
    height: Float,
    yOffset: Float,
    spacing: Float,
    adjustedMaxValue: Float
): Path {
    return Path().apply {
        chartValues.forEachIndexed { index, value ->
            val x = xOffset + index * spacing
            val y = height - (value / adjustedMaxValue * (height - yOffset))
            if (index == 0) {
                moveTo(x, y)
            } else {
                lineTo(x, y)
            }
        }
        lineTo(xOffset + (chartValues.size - 1) * spacing, height)
        lineTo(xOffset, height)
        close()
    }
}

fun DrawScope.drawLabels(
    chartValues: List<Float>,
    timestamps: List<String>,
    xOffset: Float,
    height: Float,
    yOffset: Float,
    spacing: Float,
    adjustedMaxValue: Float,
    labelFontSize: TextUnit
) {
    chartValues.forEachIndexed { index, value ->
        val x = xOffset + index * spacing
        val y = height - (value / adjustedMaxValue * (height - yOffset))
        val yLabel = if (value < 100) "%.4f".format(value) else "%.0f".format(value)

        drawContext.canvas.nativeCanvas.drawText(
            yLabel, x - 24.dp.toPx(), y - 10.dp.toPx(), android.graphics.Paint().apply {
                textSize = labelFontSize.toPx()
                color = android.graphics.Color.BLACK
            }
        )
    }

    timestamps.forEachIndexed { index, timestamp ->
        val x = xOffset + index * spacing
        val y = height

        drawContext.canvas.nativeCanvas.drawText(
            timestamp, x - 20.dp.toPx(), y - 4.dp.toPx(), android.graphics.Paint().apply {
                textSize = labelFontSize.toPx()
                color = android.graphics.Color.BLACK
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBidChartComposable() {
    val exampleDetails = CurrencyComparisonDetails(
        code = "AAA",
        codein = "BBB",
        name = "",
        high = "",
        low = "",
        varBid = "",
        pctChange = "",
        bid = "5.1111",
        ask = "5.0",
        timestamp = "1725839920",
        createDate = "",
        historicalData = listOf(
            CurrencyHistoricalData(
                high = "",
                low = "",
                varBid = "",
                pctChange = "",
                bid = "5",
                ask = "",
                timestamp = "1725839920"),
            CurrencyHistoricalData(
                high = "",
                low = "",
                varBid = "",
                pctChange = "",
                bid = "4.9",
                ask = "",
                timestamp = "1725839920"),
            CurrencyHistoricalData(
                high = "",
                low = "",
                varBid = "",
                pctChange = "",
                bid = "4",
                ask = "",
                timestamp = "1725839920"),
            CurrencyHistoricalData(
                high = "",
                low = "",
                varBid = "",
                pctChange = "",
                bid = "6",
                ask = "",
                timestamp = "1725839920"),
            CurrencyHistoricalData(
                high = "",
                low = "",
                varBid = "",
                pctChange = "",
                bid = "3",
                ask = "",
                timestamp = "1725839920"),

            CurrencyHistoricalData(
                high = "",
                low = "",
                varBid = "0.24",
                pctChange = "",
                bid = "2",
                ask = "",
                timestamp = "1725839920"),)
    )
    BidChartComposable(currencyComparisonDetails = exampleDetails)
}