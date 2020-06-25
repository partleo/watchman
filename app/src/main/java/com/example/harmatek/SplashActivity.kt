package com.example.harmatek

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.example.harmatek.MainActivity.Companion.SYNE_BOLD
import com.example.harmatek.MainActivity.Companion.SYNE_EXTRA
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

    private var mDelayHandler: Handler? = null
    private var paused = false
    private val wait: Long = 1000
    private val delay: Long = 3000

    private val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            if (!paused) {
                if (Build.VERSION.SDK_INT >= 21) {
                    val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(mainIntent, ActivityOptions.makeSceneTransitionAnimation(this@SplashActivity).toBundle())
                    object : CountDownTimer(wait, wait) {
                        override fun onTick(millisUntilFinished: Long) {
                        }
                        override fun onFinish() {
                            finish()
                        }
                    }.start()
                }
                else {
                    val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
            }
            else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        brand_name_text.changeBrandNameFont()
        app_name_text.changeAppNameFont()

        mDelayHandler = Handler()
        mDelayHandler!!.postDelayed(mRunnable, delay)
    }

    override fun onDestroy() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        paused = true
    }

    override fun onResume() {
        super.onResume()
        paused = false
    }

    private fun TextView.changeBrandNameFont() {
        typeface = Typeface.createFromAsset(context.assets, SYNE_BOLD)
        /*
        val font = Typeface.createFromAsset(context.assets, SYNE_EXTRA)
        val font2 = Typeface.createFromAsset(context.assets, SYNE_BOLD)
        val ss = SpannableStringBuilder(text.substring(0, text.length - 1))
        ss.setSpan (CustomTypefaceSpan("", font), 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        ss.setSpan (CustomTypefaceSpan("", font2), 4, 8, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        text = ss
        */
    }

    private fun TextView.changeAppNameFont() {
        typeface = Typeface.createFromAsset(context.assets, SYNE_BOLD)
    }
}