package com.panic.button.core.base

import android.os.CountDownTimer

object TimerHelper {
    var currentDuration = 0L
    var timerListener: TimerListener? = null
    private var countDownTimer: CountDownTimer? = null

    fun startTimer(second: Int = 60) {
        stopTimer()
        countDownTimer = object : CountDownTimer((second * 1000).toLong(), 1000) {
            override fun onTick(millis: Long) {
                currentDuration = millis
                timerListener?.onTimerTick(millis)
            }

            override fun onFinish() {
                currentDuration = 0L
                timerListener?.onTimerFinished()
            }
        }.start()
    }

    fun stopTimer() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
            countDownTimer = null
            currentDuration = 0L
        }
    }

    interface TimerListener {
        fun onTimerTick(millis: Long)
        fun onTimerFinished()
    }
}