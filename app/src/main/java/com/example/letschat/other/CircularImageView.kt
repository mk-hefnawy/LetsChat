package com.example.letschat.other

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.appcompat.widget.AppCompatImageView
import com.example.letschat.R

class CircularImageView(context: Context): AppCompatImageView(context) {

    private val paint = Paint()
    private val size = context.resources.getDimension(R.dimen.addDeclineFriendRequestSize)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    }
}