package com.example.testcaseapplication

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import com.example.testcaseapplication.model.Game21
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos

class Game21View @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attributeSet, defStyleAttr), SurfaceHolder.Callback {
    companion object {
        const val TIME_BETWEEN_DRAWING = 10
        const val KOEF_SPEED_TRANSLATING_CARD = 0.0004
        const val TIME_FLIPPING = 1
    }

    lateinit var game21: Game21
    lateinit var thread: Thread
    var canvas: Canvas? = null
    val mPaint = Paint()
    var textViewCounter: TextView? = null
    var mPrevDrawTime = 0L
    var mStartTime = 0L
    val cardWidth = 200
    val cardHeight = 300
    var cardlocationX = width / 2
    var cardlocationY = height / 2
    val leftLimitTouchToTakeCard by lazy {
        width / 2 - cardBackSide.width / 2
    }
    val rightLimitTouchToTakeCard by lazy {
        width / 2 + cardBackSide.width / 2
    }
    val topLimitTouchToTakeCard by lazy {
        height / 2 - cardBackSide.height / 2
    }
    val bottomLimitTouchToTakeCard by lazy {
        height / 2 + cardBackSide.height / 2
    }
    var flagRunning: Boolean = false
    val tableForGame by lazy {
        Bitmap.createScaledBitmap(
            BitmapFactory.decodeStream(context.assets.open("cards/tableforgame.jpg")),
            width,
            height,
            true
        )
    }
    val cardBackSide = BitmapFactory.decodeResource(resources, R.drawable.card_back_side).run {
        Bitmap.createScaledBitmap(this, cardWidth, cardHeight, true)
    }
    var isTakeCard = false
    var isFlipCard = false
    var rotateFlipping = 0f

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        textViewCounter?.text = game21.count.toString()
        flagRunning = true
        thread = Thread {
            mPrevDrawTime = getTime()
            while (flagRunning) {
                canvas = holder.lockCanvas()
                val currentTime = getTime()
                try {
                    canvas?.let {
                        drawTableForGame(it)
                        if (currentTime - mPrevDrawTime < TIME_BETWEEN_DRAWING) {
                            return@let
                        }
                        if (isTakeCard) {
                            drawTakingCard(it)
                        }
                        if (isFlipCard) {

                            rotateFlipping += (2*(Math.PI) / (TIME_FLIPPING*1000) *TIME_BETWEEN_DRAWING).toFloat()
                            drawFlippingCard(it, (rotateFlipping))
                        }
                    }
                } catch (e: IllegalMonitorStateException) {
                    e.printStackTrace()
                } finally {
                    if (canvas != null)
                        holder.unlockCanvasAndPost(canvas)
                }
                mPrevDrawTime = currentTime
            }
        }
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        flagRunning = false
        thread.join()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x?.toInt()
        val touchY = event?.y?.toInt()

        if (event?.action == MotionEvent.ACTION_DOWN && touchX in (leftLimitTouchToTakeCard..rightLimitTouchToTakeCard) && touchY in (bottomLimitTouchToTakeCard downTo topLimitTouchToTakeCard)
            && game21.cardsOnTable.isNotEmpty() && !isTakeCard && !isFlipCard
        ) {
            game21.takeCard()
            isTakeCard = true
            mStartTime = getTime()
        }
        return super.onTouchEvent(event)
    }

    private fun getTime(): Long {
        return System.nanoTime() / 1_000_000
    }

    fun onFinishGame(isWin: Boolean) {
        if (!isTakeCard && !isFlipCard)
            context.startActivity(Intent(context, ResultGameActivity::class.java).apply {
                putExtra("is_win", isWin)
                putExtra("my_count", game21.count)
                putExtra("count_opponent", game21.countOpponent)
            })
        flagRunning = false
        thread.join()
        game21 = Game21()
    }

    private fun drawTakingCard(canvas: Canvas) {
        val timeFromStart = getTime() - mStartTime
        canvas.drawBitmap(cardBackSide, Matrix().apply {
            setTranslate(
                width / 2f - (cardBackSide.width).toFloat() / 2f,
                height / 2f - (cardBackSide.height).toFloat() / 2f
            )
            postTranslate(
                0f, (timeFromStart * height * KOEF_SPEED_TRANSLATING_CARD).toFloat()
            )
        }, mPaint)
        cardlocationY =
            height / 2 + (timeFromStart * height * KOEF_SPEED_TRANSLATING_CARD).toInt()
        if (cardlocationY >= height - (cardBackSide.height).toFloat() / 2f - 400) {
            isTakeCard = false
            isFlipCard = true
        }
    }

    private fun drawTableForGame(canvas: Canvas) {
        canvas.drawBitmap(
            tableForGame,
            Matrix(),
            mPaint
        )
        if (game21.cardsOnTable.isNotEmpty()) {
            canvas.drawBitmap(cardBackSide, Matrix().apply {
                setTranslate(
                    width / 2f - (cardBackSide.width).toFloat() / 2f,
                    height / 2f - (cardBackSide.height).toFloat() / 2f
                )
            }, mPaint)
        }
        if (game21.myCards.isNotEmpty()) {
            if ((!isTakeCard && !isFlipCard) || game21.myCards.size != 1)
                canvas.drawBitmap(cardBackSide, Matrix().apply {
                    setTranslate(
                        width / 2f - (cardBackSide.width).toFloat() / 2f,
                        height - (cardBackSide.height).toFloat() - 400
                    )

                }, mPaint)
        }
    }

    private fun drawFlippingCard(canvas: Canvas, rotate: Float) {
        val cardFrontSide =
            CardsBitmapHandling.getCardBitmap(
                context,
                game21.myCards.peek(),
                cardWidth,
                cardHeight
            )
        if (rotate >= 2 * Math.PI.toFloat()) {
            isFlipCard = false
            rotateFlipping = 0f
            canvas.drawBitmap(cardBackSide, Matrix().apply {
                setTranslate(
                    width / 2f - (cardBackSide.width).toFloat() / 2f,
                    height - (cardBackSide.height).toFloat() - 400
                )

            }, mPaint)
            CoroutineScope(Dispatchers.Main).launch {
                textViewCounter?.text = game21.count.toString()
                if (game21.count == 21) {
                    onFinishGame(isWin = true)
                }
            }
        } else {
            when {
                (rotate in (0f..Math.PI.toFloat() / 2)) ->
                    canvas.drawBitmap(cardBackSide, Matrix().apply {
                        setTranslate(
                            width / 2f - (cardBackSide.width).toFloat() / 2f,
                            height - (cardBackSide.height).toFloat() - 400
                        )
                        postScale(
                            cardBackSide.height / 2f * abs((cos(rotate))) * 2 / cardBackSide.height,
                            1f,
                            width / 2f,
                            height - cardBackSide.height / 2f
                        )
                    }, mPaint)
                (rotate in (Math.PI.toFloat() / 2..1.5f * Math.PI.toFloat())) ->
                    canvas
                        .drawBitmap(cardFrontSide, Matrix().apply {
                            setTranslate(
                                width / 2f - (cardBackSide.width).toFloat() / 2f,
                                height - (cardBackSide.height).toFloat() - 400
                            )
                            postScale(
                                cardBackSide.height / 2f * abs((cos(rotate))) * 2 / cardBackSide.height,
                                1f,
                                width / 2f,
                                height - cardBackSide.height / 2f
                            )
                        }, mPaint)
                (rotate in (1.5f * Math.PI.toFloat())..2 * Math.PI.toFloat()) ->
                    canvas
                        .drawBitmap(cardBackSide, Matrix().apply {
                            setTranslate(
                                width / 2f - (cardBackSide.width).toFloat() / 2f,
                                height - (cardBackSide.height).toFloat() - 400
                            )
                            postScale(
                                cardBackSide.height / 2f * abs((cos(rotate))) * 2 / cardBackSide.height,
                                1f,
                                width / 2f,
                                height - cardBackSide.height / 2f
                            )
                        }, mPaint)
            }
        }
    }
}
