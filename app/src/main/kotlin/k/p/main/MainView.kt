package k.p.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.MotionEvent
import k.p.utils.EnvironmentUtil
import local.kcn.utils.LogUtil
import local.kcn.view.BaseSurfaceView
import java.io.File

open class MainView : BaseSurfaceView {
    private var bgAlpha = 0f
    private var bgBitmap: Bitmap? = null
    private var blinkAlpha = 0f
    private var boardBlinkAlpha = 0f
    private var boardBlinkBitmap: Bitmap? = null
    private var boardBlinkDelay = 0f
    private var buttonBitmap: Bitmap? = null
    private var buttonExitX = 0f
    private var buttonExitY = 0f
    private var buttonHeight = 0f
    private var buttonStartX = 0f
    private var buttonStartY = 0f
    private var buttonWidth = 0f
    private var context0: TouhouPet? = null
    private var mp: MediaPlayer? = null
    private var musicBPM = 0f
    private var musicOffset = 0
    private var playing = false
    private var satoriBlinkAngel = 0f
    private var satoriBlinkBitmap: Bitmap? = null
    private var satoriBlinkHeight = 0
    private var satoriBlinkWidth = 0
    private var satoriBlinkX = 0
    private var satoriBlinkY = 0
    private var stage = 0
    private var startBoardBlinkOffset = 0
    private var textPaint: Paint? = null
    private var titlePetAlpha = 0f
    private var titlePetBitmap: Bitmap? = null
    private var titlePetHeight = 0f
    private var titlePetWidth = 0f
    private var titlePetX = 0f
    private var titlePetY = 0f
    private var titleSatoriAlpha = 0f
    private var titleSatoriBitmap: Bitmap? = null
    private var titleSatoriHeight = 0f
    private var titleSatoriWidth = 0f
    private var titleSatoriX = 0f
    private var titleSatoriY = 0f
    private var titleTargetX = 0f
    private var titleTouhouAlpha = 0f
    private var titleTouhouBitmap: Bitmap? = null
    private var titleTouhouHeight = 0f
    private var titleTouhouWidth = 0f
    private var titleTouhouX = 0f
    private var titleTouhouY = 0f

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        stage = 0
        musicBPM = 151.0f
        startBoardBlinkOffset = 14877
        boardBlinkDelay = 400.0f
        playing = false
    }

    override fun init() {
        bgBitmap = loadBitmap(R.drawable.bg)
        titleTouhouBitmap = loadBitmap(R.drawable.title_touhou)
        titlePetBitmap = loadBitmap(R.drawable.title_pet)
        titleSatoriBitmap = loadBitmap(R.drawable.title_satori)
        buttonBitmap = loadBitmap(R.drawable.title_text_bg)
        boardBlinkBitmap = loadBitmap(R.drawable.boardblink_bg)
        satoriBlinkBitmap = loadBitmap(R.drawable.satori_blink)
        mp = MediaPlayer()
        try {
            val file = File(EnvironmentUtil.getMainPath() + "/system/Rouzensatorin.mp3")
            LogUtil.log("path = " + file.absolutePath)
            LogUtil.log("exist = " + file.exists())
            mp!!.setDataSource(EnvironmentUtil.getMainPath() + "/system/Rouzensatorin.mp3")
            mp!!.prepare()
            mp!!.setOnCompletionListener {
                stage = 7
            }
        } catch (e: Exception) {
            LogUtil.log(e)
        }
        textPaint = Paint()
        textPaint!!.color = -16777216
        textPaint!!.textAlign = Paint.Align.CENTER
        textPaint!!.isAntiAlias = true
        textPaint!!.textSize = 34.0f
        textPaint!!.style = Paint.Style.FILL_AND_STROKE
        titleTargetX = SCREEN_WIDTH / 2.0f
        titleTouhouWidth = 600.0f * X_SCALE
        titleTouhouHeight = Y_SCALE * 200.0f
        titlePetWidth = X_SCALE * 400.0f
        titlePetHeight = Y_SCALE * 200.0f
        titleSatoriWidth = 300.0f * X_SCALE
        titleSatoriHeight = 100.0f * Y_SCALE
        buttonWidth = 240.0f * X_SCALE
        buttonHeight = 60.0f * Y_SCALE
        satoriBlinkWidth = (X_SCALE * 128.0f).toInt()
        satoriBlinkHeight = (Y_SCALE * 128.0f).toInt()
        titleTouhouX = -titleTouhouWidth / 2.0f
        titleTouhouY = 150.0f * Y_SCALE
        titlePetX = SCREEN_WIDTH + titleTouhouWidth / 2.0f
        titlePetY = 350.0f * Y_SCALE
        buttonStartX = 0.0f
        buttonStartY = 800.0f * Y_SCALE
        buttonExitX = SCREEN_WIDTH.toFloat()
        buttonExitY = 950.0f * Y_SCALE
        titleSatoriX = SCREEN_WIDTH / 2.0f + titleSatoriWidth / 2.0f + 50.0f * X_SCALE
        titleSatoriY = 500.0f * Y_SCALE
        satoriBlinkX = (X_SCALE * 400.0f).toInt()
        satoriBlinkY = (480.0f * Y_SCALE).toInt()
        startDraw()
    }

    override fun updateStatus(time: Int): Boolean {
        var t = time
        if (stage == 0 && offset >= 2000) {
            stage = 1
        }
        if (stage == 1 && offset >= 5000) {
            stage = 2
        }
        if (stage == 2 && offset >= 7000) {
            stage = 3
        }
        if (stage == 3 && offset >= 8000) {
            stage = 4
        }
        if (stage == 4 && offset >= 20000) {
            stage = 5
        }
        if (stage == 5 && offset >= 22000) {
            stage = 6
        }
        if (!playing && offset > 6427) {
            LogUtil.log("music start")
            mp!!.start()
            offset = 6427L
            playing = true
        }
        musicOffset = (offset - 6427).toInt()
        if (musicOffset > startBoardBlinkOffset) {
            val cof = musicOffset - startBoardBlinkOffset
            val interval = (60000.0f / musicBPM).toInt()
            val f = cof % interval
            if (f < boardBlinkDelay) {
                boardBlinkAlpha = (boardBlinkDelay - f) / boardBlinkDelay
                boardBlinkAlpha /= 1.8f
            } else {
                boardBlinkAlpha = 0.0f
            }
        }
        satoriBlinkAngel += t / 10.0f
        if (stage == 1) {
            if (t < 10) {
                t = 10
            }
            val rate = 500.0f / t
            val distance = Math.abs(titleTouhouX - titleTargetX)
            if (distance > 5.0f) {
                titleTouhouX = (if (titleTouhouX - titleTargetX > 0.0f) (-distance) / rate - 1.0f else distance / rate + 1.0f) + titleTouhouX
            } else {
                titleTouhouX = titleTargetX
            }
            titleTouhouAlpha = ((titleTargetX - distance) - 100.0f) / titleTargetX
            val distance2 = Math.abs(titlePetX - titleTargetX)
            if (distance2 > 5.0f) {
                titlePetX = (if (titlePetX - titleTargetX > 0.0f) (-distance2) / rate - 1.0f else distance2 / rate + 1.0f) + titlePetX
            } else {
                titlePetX = titleTargetX
            }
            titlePetAlpha = ((titleTargetX - distance2) - 100.0f) / titleTargetX
        } else if (stage == 2) {
            titleSatoriAlpha += t / 2000.0f
            if (titleSatoriAlpha > 1.0f) {
                titleSatoriAlpha = 1.0f
            }
        } else if (stage == 3) {
            blinkAlpha = ((8000 - offset) / 1000.0f) / 1.4f
        } else if (stage == 4) {
            bgAlpha += t / 15000.0f
            if (bgAlpha > 1.0f) {
                bgAlpha = 1.0f
            }
        } else if (stage == 5) {
            if (t < 10) {
                t = 10
            }
            val rate2 = 300.0f / t
            val distance3 = Math.abs(buttonStartX - titleTargetX)
            if (distance3 > 5.0f) {
                buttonStartX = (if (buttonStartX - titleTargetX > 0.0f) (-distance3) / rate2 - 1.0f else distance3 / rate2 + 1.0f) + buttonStartX
            } else {
                buttonStartX = titleTargetX
            }
            val distance4 = Math.abs(buttonExitX - titleTargetX)
            if (distance4 > 5.0f) {
                buttonExitX = (if (buttonExitX - titleTargetX > 0.0f) (-distance4) / rate2 - 1.0f else distance4 / rate2 + 1.0f) + buttonExitX
            } else {
                buttonExitX = titleTargetX
            }
        }
        return true
    }

    override fun update(canvas: Canvas) {
        var f: Float
        canvas.drawColor(-16777216)
        if (stage >= 3) {
            picPaint!!.alpha = (255.0f * (bgAlpha / 2.0f)).toInt()
            canvas.drawBitmap(bgBitmap!!, null, Rect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT), picPaint)
        }
        picPaint!!.alpha = (255.0f * titleTouhouAlpha).toInt()
        canvas.drawBitmap(
            titleTouhouBitmap!!, null,
            Rect(
                (titleTouhouX - titleTouhouWidth / 2.0f).toInt(),
                (titleTouhouY - titleTouhouHeight / 2.0f).toInt(),
                (titleTouhouX + titleTouhouWidth / 2.0f).toInt(),
                (titleTouhouY + titleTouhouHeight / 2.0f).toInt()
            ), picPaint
        )
        picPaint!!.alpha = (255.0f * titlePetAlpha).toInt()
        canvas.drawBitmap(
            titlePetBitmap!!, null,
            Rect(
                (titlePetX - titlePetWidth / 2.0f).toInt(),
                (titlePetY - titlePetHeight / 2.0f).toInt(),
                (titlePetX + titlePetWidth / 2.0f).toInt(),
                (titlePetY + titlePetHeight / 2.0f).toInt()
            ), picPaint
        )
        picPaint!!.alpha = (255.0f * titleSatoriAlpha).toInt()
        canvas.drawBitmap(
            titleSatoriBitmap!!, null,
            Rect(
                (titleSatoriX - titleSatoriWidth / 2.0f).toInt(),
                (titleSatoriY - titleSatoriHeight / 2.0f).toInt(),
                (titleSatoriX + titleSatoriWidth / 2.0f).toInt(),
                (titleSatoriY + titleSatoriHeight / 2.0f).toInt()
            ), picPaint
        )
        if (stage >= 5) {
            picPaint!!.alpha = 255
            canvas.drawBitmap(
                buttonBitmap!!, null,
                Rect(
                    (buttonStartX - buttonWidth / 2.0f).toInt(),
                    (buttonStartY - buttonHeight / 2.0f).toInt(),
                    (buttonStartX + buttonWidth / 2.0f).toInt(),
                    (buttonStartY + buttonHeight / 2.0f).toInt()
                ), picPaint
            )
            canvas.drawBitmap(
                buttonBitmap!!, null,
                Rect(
                    (buttonExitX - buttonWidth / 2.0f).toInt(),
                    (buttonExitY - buttonHeight / 2.0f).toInt(),
                    (buttonExitX + buttonWidth / 2.0f).toInt(),
                    (buttonExitY + buttonHeight / 2.0f).toInt()
                ), picPaint
            )
            canvas.drawText("开始", buttonStartX, buttonStartY + buttonHeight / 4.0f, textPaint!!)
            canvas.drawText("退出", buttonExitX, buttonExitY + buttonHeight / 4.0f, textPaint!!)
        }
        if (stage == 3 || stage == 4) {
            canvas.drawColor(Color.argb((blinkAlpha * 255.0f).toInt(), 255, 255, 255))
        }
        if (stage == 5 || stage == 6) {
            picPaint!!.alpha = (255.0f * boardBlinkAlpha).toInt()
            canvas.drawBitmap(boardBlinkBitmap!!, null, Rect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT), picPaint)
        }
        if ((stage == 5 || stage == 6) && musicOffset > startBoardBlinkOffset) {
            val cof = musicOffset - startBoardBlinkOffset
            val interval = (60000.0f / musicBPM).toInt()
            val f2 = cof % interval
            f = if (f2 < (interval / 9) * 10) {
                (((((interval / 9) * 10) - f2) / ((interval / 9) * 10)) / 5.0f) + 0.8f
            } else {
                1.0f
            }
            picPaint!!.alpha = 255
            val matrix = canvas.matrix
            matrix.postTranslate(satoriBlinkX.toFloat(), satoriBlinkY.toFloat())
            matrix.postRotate(satoriBlinkAngel, satoriBlinkX + satoriBlinkWidth.toFloat(), satoriBlinkY + satoriBlinkHeight.toFloat())
            matrix.postScale(f / 1.4f, f / 1.4f, satoriBlinkX + satoriBlinkWidth.toFloat(), satoriBlinkY + satoriBlinkHeight.toFloat())
            canvas.drawBitmap(satoriBlinkBitmap!!, matrix, picPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        if (event.action != 0 || stage != 6) {
            return true
        }
        if (x > buttonStartX - buttonWidth / 2.0f && x < buttonStartX + buttonWidth / 2.0f &&
            y > buttonStartY - buttonHeight / 2.0f && y < buttonStartY + buttonHeight / 2.0f
        ) {
            (getContext() as TouhouPet).start()
            return true
        }
        if (x > buttonExitX - buttonWidth / 2.0f && x < buttonExitX + buttonWidth / 2.0f &&
            y > buttonExitY - buttonHeight / 2.0f && y < buttonExitY + buttonHeight / 2.0f
        ) {
            (getContext() as TouhouPet).exit()
            return true
        }
        return true
    }

    fun getStage(): Int = stage

    override fun release() {
        if (mp != null) {
            if (mp!!.isPlaying) {
                mp!!.stop()
            }
            mp!!.release()
            mp = null
            LogUtil.log("release")
        }
    }
}