package com.dev

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.*

class LocalService : Service() {
    private val mBinder: IBinder = LocalBinder()
    private var mSeed: Long = 0
    private val mGenerator = Random()

    override fun onBind(intent: Intent?): IBinder? {
        if (intent?.hasExtra(SEED_KEY) == true) {
            mSeed = intent.getLongExtra(SEED_KEY, 0)
            mGenerator.setSeed(mSeed)
        }
        return mBinder
    }

    fun getRandomInt(): Int {
        return mGenerator.nextInt(100)
    }

    inner class LocalBinder : Binder() {
        val service: LocalService
            get() =
                this@LocalService
    }

    companion object {
        // Used as a key for the Intent.
        const val SEED_KEY = "SEED_KEY"
    }
}