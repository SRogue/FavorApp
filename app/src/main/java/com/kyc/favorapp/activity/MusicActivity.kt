package com.kyc.favorapp.activity


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.kyc.favorapp.R
import com.kyc.favorapp.model.MusicData
import com.kyc.favorapp.service.MusicService
import com.kyc.favorapp.util.DisplayUtil
import com.kyc.favorapp.util.FastBlurUtil
import com.kyc.favorapp.view.DiscView
import com.kyc.favorapp.view.DiscView.Companion.DURATION_NEEDLE_ANIAMTOR
import kotlinx.android.synthetic.main.activity_music.*
import java.io.Serializable
import java.util.*


class MusicActivity : AppCompatActivity(), DiscView.IPlayInfo, View.OnClickListener {


    private val myDiscuview by lazy { discview as DiscView }


    private val mMusicHandler =
        @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                musicSeekBar.progress = musicSeekBar.progress + 1000
                tvCurrentTime.text = duration2Time(musicSeekBar.progress)
                startUpdateSeekBarProgress()
            }
        }

    private val mMusicReceiver = MusicReceiver()
    private val mMusicDatas = ArrayList<MusicData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
        initMusicDatas()
        initView()
        initMusicReceiver()
        makeStatusBarTransparent()
    }

    private fun initMusicReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(MusicService.ACTION_STATUS_MUSIC_PLAY)
            addAction(MusicService.ACTION_STATUS_MUSIC_PAUSE)
            addAction(MusicService.ACTION_STATUS_MUSIC_DURATION)
            addAction(MusicService.ACTION_STATUS_MUSIC_COMPLETE)
        }
        /*注册本地广播*/
        LocalBroadcastManager.getInstance(this).registerReceiver(mMusicReceiver, intentFilter)
    }

    private fun initView() {
        setSupportActionBar(toolBar)
        myDiscuview.setPlayInfoListener(this)
        ivLast.setOnClickListener(this)
        ivNext.setOnClickListener(this)
        ivPlayOrPause.setOnClickListener(this)

        musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tvCurrentTime.text = duration2Time(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                stopUpdateSeekBarProgree()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekTo(seekBar.progress)
                startUpdateSeekBarProgress()
            }
        })

        tvCurrentTime.text = duration2Time(0)
        tvTotalTime.text = duration2Time(0)
        myDiscuview.setMusicDataList(mMusicDatas)
    }

    private fun stopUpdateSeekBarProgree() {
        mMusicHandler.removeMessages(MUSIC_MESSAGE)
    }

    /*设置透明状态栏*/
    private fun makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = Color.TRANSPARENT
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    private fun initMusicDatas() {
        val musicData1 = MusicData(R.raw.music1, R.raw.ic_music1, "寻", "三亩地")
        val musicData2 = MusicData(R.raw.music2, R.raw.ic_music2, "Nightingale", "YANI")
        val musicData3 = MusicData(R.raw.music3, R.raw.ic_music3, "Cornfield Chase", "Hans Zimmer")

        mMusicDatas.add(musicData1)
        mMusicDatas.add(musicData2)
        mMusicDatas.add(musicData3)

        val intent = Intent(this, MusicService::class.java)
        intent.putExtra(PARAM_MUSIC_LIST, mMusicDatas as Serializable)
        startService(intent)
    }

    private fun try2UpdateMusicPicBackground(musicPicRes: Int) {
        if (rootLayout.isNeed2UpdateBackground(musicPicRes)) {
            Thread(Runnable {
                val foregroundDrawable = getForegroundDrawable(musicPicRes)
                runOnUiThread {
                    rootLayout.foreground = foregroundDrawable
                    rootLayout.beginAnimation()
                }
            }).start()
        }
    }

    private fun getForegroundDrawable(musicPicRes: Int): Drawable {
        /*得到屏幕的宽高比，以便按比例切割图片一部分*/
        val widthHeightSize =
            (DisplayUtil.getScreenWidth(this@MusicActivity) * 1.0 / DisplayUtil.getScreenHeight(this) * 1.0) as Float

        val bitmap = getForegroundBitmap(musicPicRes)
        val cropBitmapWidth = (widthHeightSize * bitmap.height).toInt()
        val cropBitmapWidthX = ((bitmap.width - cropBitmapWidth) / 2.0).toInt()

        /*切割部分图片*/
        val cropBitmap = Bitmap.createBitmap(
            bitmap, cropBitmapWidthX, 0, cropBitmapWidth,
            bitmap.height
        )
        /*缩小图片*/
        val scaleBitmap = Bitmap.createScaledBitmap(
            cropBitmap, bitmap.width / 50, bitmap
                .height / 50, false
        )
        /*模糊化*/
        val blurBitmap = FastBlurUtil.doBlur(scaleBitmap, 8, true)

        val foregroundDrawable = BitmapDrawable(blurBitmap)
        /*加入灰色遮罩层，避免图片过亮影响其他控件*/
        foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
        return foregroundDrawable
    }

    private fun getForegroundBitmap(musicPicRes: Int): Bitmap {
        val screenWidth = DisplayUtil.getScreenWidth(this)
        val screenHeight = DisplayUtil.getScreenHeight(this)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        BitmapFactory.decodeResource(getResources(), musicPicRes, options)
        val imageWidth = options.outWidth
        val imageHeight = options.outHeight

        if (imageWidth < screenWidth && imageHeight < screenHeight) {
            return BitmapFactory.decodeResource(getResources(), musicPicRes)
        }

        var sample = 2
        val sampleX = imageWidth / DisplayUtil.getScreenWidth(this)
        val sampleY = imageHeight / DisplayUtil.getScreenHeight(this)

        if (sampleX > sampleY && sampleY > 1) {
            sample = sampleX
        } else if (sampleY > sampleX && sampleX > 1) {
            sample = sampleY
        }

        options.inJustDecodeBounds = false
        options.inSampleSize = sample
        options.inPreferredConfig = Bitmap.Config.RGB_565

        return BitmapFactory.decodeResource(resources, musicPicRes, options)
    }

    override fun onMusicInfoChanged(musicName: String, musicAuthor: String) {
        actionBar?.apply {
            title = musicName
            subtitle = musicAuthor
        }
    }

    override fun onMusicPicChanged(musicPicRes: Int) {
        try2UpdateMusicPicBackground(musicPicRes)
    }

    override fun onMusicChanged(musicChangedStatus: DiscView.MusicChangedStatus) {
        when (musicChangedStatus) {
            DiscView.MusicChangedStatus.PLAY -> {
                play()
            }
            DiscView.MusicChangedStatus.PAUSE -> {
                pause()
            }
            DiscView.MusicChangedStatus.NEXT -> {
                next()
            }
            DiscView.MusicChangedStatus.LAST -> {
                last()
            }
            DiscView.MusicChangedStatus.STOP -> {
                stop()
            }
        }
    }

    override fun onClick(v: View) {
        when (v) {
            ivPlayOrPause -> myDiscuview.playOrPause()
            ivNext -> myDiscuview.next()
            ivLast -> myDiscuview.last()
        }
    }

    private fun play() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PLAY)
        startUpdateSeekBarProgress()
    }

    private fun pause() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PAUSE)
        stopUpdateSeekBarProgree()
    }

    private fun stop() {
        stopUpdateSeekBarProgree()
        ivPlayOrPause.setImageResource(R.drawable.ic_play)
        tvCurrentTime.text = duration2Time(0)
        tvTotalTime.text = duration2Time(0)
        musicSeekBar.progress = 0
    }

    private operator fun next() {
        rootLayout.postDelayed(Runnable { optMusic(MusicService.ACTION_OPT_MUSIC_NEXT) }, DURATION_NEEDLE_ANIAMTOR)
        stopUpdateSeekBarProgree()
        tvCurrentTime.text = duration2Time(0)
        tvTotalTime.text = duration2Time(0)
    }

    private fun last() {
        rootLayout.postDelayed(Runnable { optMusic(MusicService.ACTION_OPT_MUSIC_LAST) }, DURATION_NEEDLE_ANIAMTOR)
        stopUpdateSeekBarProgree()
        tvCurrentTime.text = duration2Time(0)
        tvTotalTime.text = duration2Time(0)
    }

    private fun complete(isOver: Boolean) {
        if (isOver) {
            myDiscuview.stop()
        } else {
            myDiscuview.next()
        }
    }

    private fun optMusic(action: String) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(action))
    }

    private fun seekTo(position: Int) {
        val intent = Intent(MusicService.ACTION_OPT_MUSIC_SEEK_TO)
        intent.putExtra(MusicService.PARAM_MUSIC_SEEK_TO, position)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun startUpdateSeekBarProgress() {
        /*避免重复发送Message*/
        stopUpdateSeekBarProgree()
        mMusicHandler.sendEmptyMessageDelayed(0, 1000)
    }

    /*根据时长格式化称时间文本*/
    private fun duration2Time(duration: Int): String {
        val min = duration / 1000 / 60
        val sec = duration / 1000 % 60

        return (if (min < 10) "0$min" else min.toString() + "") + ":" + if (sec < 10) "0$sec" else sec.toString() + ""
    }

    private fun updateMusicDurationInfo(totalDuration: Int) {
        musicSeekBar.progress = 0
        musicSeekBar.max = totalDuration
        tvTotalTime.text = duration2Time(totalDuration)
        tvCurrentTime.text = duration2Time(0)
        startUpdateSeekBarProgress()
    }

    internal inner class MusicReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MusicService.ACTION_STATUS_MUSIC_PLAY) {
                ivPlayOrPause.setImageResource(R.drawable.ic_pause)
                val currentPosition = intent.getIntExtra(MusicService.PARAM_MUSIC_CURRENT_POSITION, 0)
                musicSeekBar.progress = currentPosition
                if (myDiscuview.isPlaying) {
                    myDiscuview.playOrPause()
                }
            } else if (action == MusicService.ACTION_STATUS_MUSIC_PAUSE) {
                ivPlayOrPause.setImageResource(R.drawable.ic_play)
                if (myDiscuview.isPlaying) {
                    myDiscuview.playOrPause()
                }
            } else if (action == MusicService.ACTION_STATUS_MUSIC_DURATION) {
                val duration = intent.getIntExtra(MusicService.PARAM_MUSIC_DURATION, 0)
                updateMusicDurationInfo(duration)
            } else if (action == MusicService.ACTION_STATUS_MUSIC_COMPLETE) {
                val isOver = intent.getBooleanExtra(MusicService.PARAM_MUSIC_IS_OVER, true)
                complete(isOver)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMusicReceiver)
    }

    companion object {
        const val MUSIC_MESSAGE = 0

        const val PARAM_MUSIC_LIST = "PARAM_MUSIC_LIST"
    }
}
