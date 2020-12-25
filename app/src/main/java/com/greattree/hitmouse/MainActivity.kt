package com.greattree.hitmouse

import android.annotation.SuppressLint
import android.app.Service
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.random.Random

class MainActivity : BaseActivity(), CoroutineScope by MainScope() {
    // 動畫檔list
    private val animationAssets =
        arrayOf("mouse1.json", "mouse2.json", "mouse3.json", "mouse4.json")

    private val lottieView by lazy {
        arrayOf(lottie1, lottie2, lottie3, lottie4, lottie5, lottie6, lottie7, lottie8, lottie9)
    }

    private val startRipple by lazy {
        CustomerRipple(
            ContextCompat.getColor(this, R.color.red_D45757),
            UIHelper.getDp(this, 8)
        ).rippleDrawable()
    }

    private val mShowAction by lazy {
        TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 1f,
            Animation.RELATIVE_TO_SELF, 0.0f
        )
    }

    private val mHideAction by lazy {
        TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 1f
        )
    }

    private val vibrator by lazy {
        getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
    }

    private val touchListener = object : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent?): Boolean {
            event?.run {
                if (view.visibility == View.VISIBLE && event.action == MotionEvent.ACTION_DOWN) {
                    vibrator.vibrate(1000)
                    Toast.makeText(this@MainActivity, "打到拉", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
            return false
        }
    }

    private val mediaPlayer by lazy {
        MediaPlayer.create(applicationContext, R.raw.sound)
    }

    private var mDuration = 1500L
    private var delayTime = 1500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListener()
    }

    private fun initView() {
        bt_start.background = startRipple
    }

    private fun initListener() {
        bt_start.setOnClickListener {
            bt_start.visibility = View.GONE
            startGame()
        }


        for (view in lottieView) {
            view.setOnTouchListener(touchListener)
        }
    }

    private fun startGame() {
        mediaPlayer.start()
        launch(Dispatchers.IO) {
            var count = 0
            for (i in 0..1000) {
                count++
                if (count == 4 || count == 10 ) {
                    mDuration -= 150L
                    delayTime -= 150L
                }

                if (count == 15 ||count == 24) {
                    mDuration -= 200L
                    delayTime -= 200L
                }

                if (count == 30) {
                    mDuration += 200L
                    delayTime += 200L
                }

                if (count == 40) {
                    mDuration = 1500L
                    delayTime = 1500L
                    count = 0
                }

                Log.d("TAG", "startGame : 第 $count 次")
                Log.d("TAG", "startGame : ${mDuration + delayTime}")
                showMouse(lottieView[Random.nextInt(9)])
                delay(mDuration + delayTime)
            }
        }
    }

    private fun showMouse(view: LottieAnimationView) {
        val assets = animationAssets[Random.nextInt(4)]
        launch(Dispatchers.Main) {
            mShowAction.duration = mDuration
            view.visibility = View.VISIBLE
            view.setAnimation(assets)
            view.playAnimation()
            view.repeatCount = -1
            view.startAnimation(mShowAction)

            delay(delayTime)

            mHideAction.duration = mDuration
            view.startAnimation(mHideAction)
            delay(1000)
            view.visibility = View.GONE
        }
    }
}