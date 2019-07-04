package com.kyc.favorapp.service


import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.kyc.favorapp.activity.MusicActivity.Companion.PARAM_MUSIC_LIST
import com.kyc.favorapp.model.MusicData
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by AchillesL on 2016/11/18.
 */

class MusicService : Service(), MediaPlayer.OnCompletionListener {

    private var mCurrentMusicIndex = 0
    private var mIsMusicPause = false
    private val mMusicDatas = ArrayList<MusicData>()

    private val mMusicReceiver = MusicReceiver()
    private var mMediaPlayer: MediaPlayer? = MediaPlayer()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        initMusicDatas(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        initBoardCastReceiver()
    }

    private fun initMusicDatas(intent: Intent?) {
        if (intent == null) return
        val musicDatas = intent.getSerializableExtra(PARAM_MUSIC_LIST) as ArrayList<MusicData>
        mMusicDatas.addAll(musicDatas)
    }

    private fun initBoardCastReceiver() {
        val intentFilter = IntentFilter()

        intentFilter.addAction(ACTION_OPT_MUSIC_PLAY)
        intentFilter.addAction(ACTION_OPT_MUSIC_PAUSE)
        intentFilter.addAction(ACTION_OPT_MUSIC_NEXT)
        intentFilter.addAction(ACTION_OPT_MUSIC_LAST)
        intentFilter.addAction(ACTION_OPT_MUSIC_SEEK_TO)

        LocalBroadcastManager.getInstance(this).registerReceiver(mMusicReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer!!.release()
        mMediaPlayer = null
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMusicReceiver)
    }

    private fun play(index: Int) {
        if (index >= mMusicDatas.size) return
        if (mCurrentMusicIndex == index && mIsMusicPause) {
            mMediaPlayer!!.start()
        } else {
            mMediaPlayer!!.stop()
            mMediaPlayer = null

            mMediaPlayer = MediaPlayer.create(
                applicationContext, mMusicDatas[index].musicPicRes
            )
            mMediaPlayer!!.start()
            mMediaPlayer!!.setOnCompletionListener(this)
            mCurrentMusicIndex = index
            mIsMusicPause = false

            val duration = mMediaPlayer!!.duration
            sendMusicDurationBroadCast(duration)
        }
        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PLAY)
    }

    private fun pause() {
        mMediaPlayer!!.pause()
        mIsMusicPause = true
        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PAUSE)
    }

    private fun stop() {
        mMediaPlayer!!.stop()
    }

    private operator fun next() {
        if (mCurrentMusicIndex + 1 < mMusicDatas.size) {
            play(mCurrentMusicIndex + 1)
        } else {
            stop()
        }
    }

    private fun last() {
        if (mCurrentMusicIndex != 0) {
            play(mCurrentMusicIndex - 1)
        }
    }

    private fun seekTo(intent: Intent) {
        if (mMediaPlayer!!.isPlaying) {
            val position = intent.getIntExtra(PARAM_MUSIC_SEEK_TO, 0)
            mMediaPlayer!!.seekTo(position)
        }
    }

    override fun onCompletion(mp: MediaPlayer) {
        sendMusicCompleteBroadCast()
    }

    internal inner class MusicReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_OPT_MUSIC_PLAY -> play(mCurrentMusicIndex)
                ACTION_OPT_MUSIC_PAUSE -> pause()
                ACTION_OPT_MUSIC_LAST -> last()
                ACTION_OPT_MUSIC_NEXT -> next()
                ACTION_OPT_MUSIC_SEEK_TO -> seekTo(intent)
            }
        }
    }

    private fun sendMusicCompleteBroadCast() {
        val intent = Intent(ACTION_STATUS_MUSIC_COMPLETE)
        intent.putExtra(PARAM_MUSIC_IS_OVER, mCurrentMusicIndex == mMusicDatas.size - 1)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun sendMusicDurationBroadCast(duration: Int) {
        val intent = Intent(ACTION_STATUS_MUSIC_DURATION)
        intent.putExtra(PARAM_MUSIC_DURATION, duration)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun sendMusicStatusBroadCast(action: String) {
        val intent = Intent(action)
        if (action == ACTION_STATUS_MUSIC_PLAY) {
            intent.putExtra(PARAM_MUSIC_CURRENT_POSITION, mMediaPlayer!!.currentPosition)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    companion object {

        /*操作指令*/
        const val ACTION_OPT_MUSIC_PLAY = "ACTION_OPT_MUSIC_PLAY"
        const val ACTION_OPT_MUSIC_PAUSE = "ACTION_OPT_MUSIC_PAUSE"
        const val ACTION_OPT_MUSIC_NEXT = "ACTION_OPT_MUSIC_NEXT"
        const val ACTION_OPT_MUSIC_LAST = "ACTION_OPT_MUSIC_LAST"
        const val ACTION_OPT_MUSIC_SEEK_TO = "ACTION_OPT_MUSIC_SEEK_TO"

        /*状态指令*/
        const val ACTION_STATUS_MUSIC_PLAY = "ACTION_STATUS_MUSIC_PLAY"
        const val ACTION_STATUS_MUSIC_PAUSE = "ACTION_STATUS_MUSIC_PAUSE"
        const val ACTION_STATUS_MUSIC_COMPLETE = "ACTION_STATUS_MUSIC_COMPLETE"
        const val ACTION_STATUS_MUSIC_DURATION = "ACTION_STATUS_MUSIC_DURATION"

        const val PARAM_MUSIC_DURATION = "PARAM_MUSIC_DURATION"
        const val PARAM_MUSIC_SEEK_TO = "PARAM_MUSIC_SEEK_TO"
        const val PARAM_MUSIC_CURRENT_POSITION = "PARAM_MUSIC_CURRENT_POSITION"
        const val PARAM_MUSIC_IS_OVER = "PARAM_MUSIC_IS_OVER"
    }

}
