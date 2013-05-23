package net.wanhack.android

import android.content.Context
import android.view.View
import android.util.AttributeSet
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Rect
import net.wanhack.model.GameFacade

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var game: GameFacade? = null
    val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG);

    {
        mTextPaint.setColor(Color.GREEN)
    }

    protected override fun onDraw(canvas: Canvas?) {
        super<View>.onDraw(canvas)
        canvas!!

        val drawingRect = Rect()
        getDrawingRect(drawingRect)

        val rect = RectF(drawingRect);

        // Draw the shadow
        canvas.drawOval(rect, mTextPaint);
    }
}
