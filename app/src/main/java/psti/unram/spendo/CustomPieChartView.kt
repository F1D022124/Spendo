package psti.unram.spendo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomPieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var pieData: List<PieChartData> = emptyList()

    fun setData(data: List<PieChartData>) {
        pieData = data
        invalidate() // Memicu onDraw() ulang
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width == 0 || height == 0 || pieData.isEmpty()) return

        val radius = Math.min(width, height) / 2 * 0.8f
        val centerX = width / 2f
        val centerY = height / 2f

        val total = pieData.sumOf { it.value.toDouble() }.toFloat()
        var startAngle = 0f

        for (item in pieData) {
            val sweepAngle = (item.value / total) * 360f
            paint.color = item.color
            canvas.drawArc(
                centerX - radius, centerY - radius,
                centerX + radius, centerY + radius,
                startAngle, sweepAngle, true, paint
            )
            startAngle += sweepAngle
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 300
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }
}
