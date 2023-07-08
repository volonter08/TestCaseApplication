package com.example.testcaseapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.random.Random

class Game21View @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr:Int = 0): SurfaceView(context,attributeSet,defStyleAttr), SurfaceHolder.Callback {

    private val mPaint = Paint()
    val myCards = Stack<Card>()
    private val cardsOnTable: MutableList<Card> = mutableListOf<Card>().apply {
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                add(Card(suit, rank))
            }
        }
    }
    var count = 0
    val TIME_BETWEEN_DRAWING = 10
    val KOEF_SPEED_TRANSLATING_CARD = 0.0004 // пикселей в секунду
    var mPrevDrawTime  = 0L
    var mStartTime = 0L
    var cardlocationX = width / 2
    var cardlocationY = height / 2
    var flagRunning: Boolean = true
    val tableForGame by lazy {
        Bitmap.createScaledBitmap(
            BitmapFactory.decodeStream(context.assets.open("tablegame.jpg")),width,height,true)
    }
    val cardWidth = 200
    val cardHeight = 300
    val cardBackSide = BitmapFactory.decodeResource(resources, R.drawable.card_back_side).run {
        Bitmap.createScaledBitmap(this, cardWidth, cardHeight, true)
    }
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
    var isTakeCard = false
    var isFlipCard = false
    var rotateFlipping = 0f

    init {
        holder.addCallback(this)
    }
    override fun surfaceCreated(holder: SurfaceHolder) {
        Thread {
            mPrevDrawTime = getTime()
            while (flagRunning) {
                val canvas = holder.lockCanvas()
                val currentTime = getTime()
                try {
                    drawTableForGame(canvas)
                    if (currentTime - mPrevDrawTime < TIME_BETWEEN_DRAWING) {
                        continue
                    }
                    if (isTakeCard) {
                        drawTakingCard(canvas)
                    }
                    if( isFlipCard){
                        rotateFlipping+= ((Math.PI)/1000*15).toFloat()
                        drawFlippingCard(canvas,(rotateFlipping))
                    }
                } catch (e: IllegalMonitorStateException) {
                    e.printStackTrace()
                } finally {
                    holder.unlockCanvasAndPost(canvas)
                }
                mPrevDrawTime = currentTime
            }
        }.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.e("MainActivity", "surfaceDestroyed")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x?.toInt()
        val touchY = event?.y?.toInt()

        if (event?.action == MotionEvent.ACTION_DOWN && touchX in (leftLimitTouchToTakeCard..rightLimitTouchToTakeCard) && touchY in (bottomLimitTouchToTakeCard downTo topLimitTouchToTakeCard)
            && cardsOnTable.isNotEmpty()
        ) {
            takeCard()
            mStartTime = getTime()
            isTakeCard = true
        }
        if (event?.action == MotionEvent.ACTION_DOWN && touchX in (leftLimitTouchToTakeCard..rightLimitTouchToTakeCard) && touchY in (height- cardBackSide.height..height) &&
            myCards.isNotEmpty()
        ) {
            isFlipCard = true
        }
        return super.onTouchEvent(event)
    }
    fun getTime(): Long {
        return System.nanoTime() / 1_000_000
    }

    fun takeCard() {
        if (cardsOnTable.isNotEmpty())
            myCards.push(
                cardsOnTable.run {
                    removeAt(Random.nextInt(size - 1))
                }
            )
    }

    fun drawTakingCard(canvas: Canvas) {
        val curTime = getTime() - mStartTime
        canvas.drawBitmap(cardBackSide, Matrix().apply {
            setTranslate(
                width / 2f - (cardBackSide.width).toFloat() / 2f,
                height / 2f - (cardBackSide.height).toFloat() / 2f
            )
            postTranslate(
                0f, (curTime * height * KOEF_SPEED_TRANSLATING_CARD).toFloat()
            )
        }, mPaint)
        cardlocationY = height / 2 + (curTime * height * KOEF_SPEED_TRANSLATING_CARD).toInt()
        if (cardlocationY >= height - (cardBackSide.height).toFloat() / 2f) {
            isTakeCard = false
            if (myCards.isNotEmpty()) {
                canvas.drawBitmap(cardBackSide, Matrix().apply {
                    setTranslate(
                        width / 2f - (cardBackSide.width).toFloat() / 2f,
                        height - (cardBackSide.height).toFloat()
                    )

                }, mPaint)
            }
        }
    }

    fun drawTableForGame(canvas: Canvas) {
        canvas.drawBitmap(
            tableForGame,
            Matrix(),
            mPaint
        )
        if (cardsOnTable.isNotEmpty()) {
            canvas.drawBitmap(cardBackSide, Matrix().apply {
                setTranslate(
                    width / 2f - (cardBackSide.width).toFloat() / 2f,
                    height / 2f - (cardBackSide.height).toFloat() / 2f
                )
            }, mPaint)
        }
        if (myCards.isNotEmpty()) {
            if (!isTakeCard || myCards.size != 1)
                canvas.drawBitmap(cardBackSide, Matrix().apply {
                    setTranslate(
                        width / 2f - (cardBackSide.width).toFloat() / 2f,
                        height - (cardBackSide.height).toFloat()
                    )

                }, mPaint)
        }
    }

    fun drawFlippingCard(canvas: Canvas, rotate: Float) {
        val cardFrontSide = CardsBitmapHandling.getCardBitmap(context,myCards.peek(),cardWidth,cardHeight)
        if( rotate >= Math.PI.toFloat()){
            isFlipCard = false
            rotateFlipping = 0f
        }
        else {
            if(rotate<= Math.PI/2)
                canvas.drawBitmap(cardBackSide, Matrix().apply {
                    setTranslate(
                        width / 2f - (cardBackSide.width).toFloat() / 2f,
                        height - (cardBackSide.height).toFloat()
                    )
                    postScale(
                        cardBackSide.height / 2f * abs((cos(rotate))) * 2 / cardBackSide.height,
                        1f,
                        width / 2f,
                        height - cardBackSide.height / 2f
                    )
                }, mPaint)
            else
                canvas.drawBitmap(cardFrontSide, Matrix().apply {
                    setTranslate(
                        width / 2f - (cardBackSide.width).toFloat() / 2f,
                        height - (cardBackSide.height).toFloat()
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