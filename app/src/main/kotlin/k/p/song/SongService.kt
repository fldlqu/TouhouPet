package k.p.song

import android.content.Context
import android.database.Cursor
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.provider.MediaStore
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import k.p.main.MainService
import k.p.main.R
import k.p.modern.Poller
import java.util.ArrayList
import java.util.Random

object SongService {
    @JvmField
    var songMenuView: View? = null
    @JvmField
    var songView: View? = null
    @JvmField
    var songViewShow = false
    @JvmField
    var songMenuViewShow = false

    private var context: MainService? = null
    private var currentPosition = 0
    private var currentSongInfo: SongInfo? = null
    private var currentTime: TextView? = null
    private var lastClickTime = 0L
    private var listView: ListView? = null
    private var mediaPlayer: MediaPlayer? = null
    private var mediaSession: MediaSession? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var ducking = false
    private var resumeAfterFocusLoss = false
    private val focusHandler = Handler(Looper.getMainLooper())
    private var nextButton: ImageView? = null
    private var playButton: ImageView? = null
    private var playMode = 0
    private var playModeButton: ImageView? = null
    private var previousButton: ImageView? = null
    private var seekBar: SeekBar? = null
    private var songList: MutableList<SongInfo>? = null
    private var songListButton: ImageView? = null
    private var songName: TextView? = null
    private var totalTime: TextView? = null
    private var updateSeekBarThread: Poller? = null
    private var viewArray: SparseArray<View>? = null
    private var playing = false
    private var songLoaded = false
    private val seekBarHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            seekBar!!.progress = msg.what
            currentTime!!.text = getTimeFromDuration(msg.what)
        }
    }

    @JvmStatic
    fun loadSong(context2: Context) {
        if (songLoaded) {
            return
        }
        context = context2 as MainService
        songLoaded = true
        songList = ArrayList()
        viewArray = SparseArray()
        mediaPlayer = MediaPlayer()
        audioManager = context2.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setOnAudioFocusChangeListener { focusChange ->
                /* 焦点回调在非 UI 线程, 播放逻辑触碰界面控件, 切到主线程 */
                focusHandler.post { onFocusChange(focusChange) }
            }
            .build()
        mediaSession = MediaSession(context2, "TouhouPet").apply {
            setCallback(object : MediaSession.Callback() {
                override fun onPlay() = resumePlayback()
                override fun onPause() = pausePlayback()
                override fun onSkipToNext() = playNext()
                override fun onSkipToPrevious() = playPrevious()
            })
        }
        val cursor = context2.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf("_id", "_display_name", "title", "duration", "artist", "album", "year", "mime_type", "_size", "_data"),
            "mime_type=? or mime_type=?",
            arrayOf("audio/mpeg", "audio/x-ms-wma"),
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        addSongByCursor(cursor)
                    } catch (e: Exception) {
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        songView = View.inflate(context2, R.layout.song, null)
        listView = songView!!.findViewById(R.id.song_listview) as ListView
        listView!!.adapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                return getSongBarView(position)
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getItem(position: Int): Any? {
                return songList!!.get(position)
            }

            override fun getCount(): Int {
                return songList!!.size
            }
        }
        val params = WindowManager.LayoutParams()
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        params.flags = 520
        params.gravity = 85
        params.width = 400
        params.height = 800
        params.format = 1
        songView!!.layoutParams = params
        songMenuView = View.inflate(context2, R.layout.songmenu, null)
        seekBar = songMenuView!!.findViewById(R.id.songmenu_progress) as SeekBar
        currentTime = songMenuView!!.findViewById(R.id.songmenu_currenttime) as TextView
        totalTime = songMenuView!!.findViewById(R.id.songmenu_totaltime) as TextView
        songName = songMenuView!!.findViewById(R.id.songmenu_songname) as TextView
        playModeButton = songMenuView!!.findViewById(R.id.songmenu_playmode) as ImageView
        previousButton = songMenuView!!.findViewById(R.id.songmenu_previous) as ImageView
        playButton = songMenuView!!.findViewById(R.id.songmenu_play) as ImageView
        nextButton = songMenuView!!.findViewById(R.id.songmenu_next) as ImageView
        songListButton = songMenuView!!.findViewById(R.id.songmenu_songlist) as ImageView
        seekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar2: SeekBar) {
            }

            override fun onStartTrackingTouch(seekBar2: SeekBar) {
            }

            override fun onProgressChanged(seekBar2: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer!!.seekTo(progress)
                    seekBar2.progress = progress
                }
            }
        })
        playModeButton!!.setOnClickListener {
            if (playMode == 0) {
                playMode = 1
                playModeButton!!.setImageResource(R.drawable.song_randomloop)
            } else if (playMode == 1) {
                playMode = 2
                playModeButton!!.setImageResource(R.drawable.song_singleloop)
            } else if (playMode == 2) {
                playMode = 0
                playModeButton!!.setImageResource(R.drawable.song_allloop)
            }
        }
        playButton!!.setOnClickListener {
            if (songList!!.size > 0) {
                if (playing) {
                    pausePlayback()
                } else {
                    resumePlayback()
                }
            }
        }
        previousButton!!.setOnClickListener {
            playPrevious()
        }
        nextButton!!.setOnClickListener {
            playNext()
        }
        songListButton!!.setOnClickListener {
            requestSongView()
        }
        val params2 = WindowManager.LayoutParams()
        params2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        params2.flags = 520
        params2.gravity = 80
        params2.width = -1
        params2.height = -2
        params2.format = 1
        songMenuView!!.layoutParams = params2
        mediaPlayer!!.setOnCompletionListener {
            if (currentSongInfo!!.duration - mediaPlayer!!.duration <= 50) {
                if (playMode == 0) {
                    var i = currentPosition + 1
                    currentPosition = i
                    if (i > songList!!.size - 1) {
                        currentPosition = 0
                    }
                    currentSongInfo = songList!!.get(currentPosition)
                    totalTime!!.setText(getTimeFromDuration(currentSongInfo!!.duration))
                    seekBar!!.max = currentSongInfo!!.duration
                    seekBar!!.progress = 0
                    songName!!.text = currentSongInfo!!.songName
                    mediaPlayer!!.stop()
                    playButton!!.setImageResource(R.drawable.song_pause)
                    mediaPlayer!!.reset()
                    try {
                        mediaPlayer!!.setDataSource(currentSongInfo!!.filePath)
                        mediaPlayer!!.prepare()
                        return@setOnCompletionListener
                    } catch (e: Exception) {
                        return@setOnCompletionListener
                    }
                }
                if (playMode == 1) {
                    currentPosition = Random().nextInt(songList!!.size)
                    currentSongInfo = songList!!.get(currentPosition)
                    totalTime!!.setText(getTimeFromDuration(currentSongInfo!!.duration))
                    seekBar!!.max = currentSongInfo!!.duration
                    seekBar!!.progress = 0
                    songName!!.text = currentSongInfo!!.songName
                    mediaPlayer!!.stop()
                    playButton!!.setImageResource(R.drawable.song_pause)
                    mediaPlayer!!.reset()
                    try {
                        mediaPlayer!!.setDataSource(currentSongInfo!!.filePath)
                        mediaPlayer!!.prepare()
                        return@setOnCompletionListener
                    } catch (e: Exception) {
                        return@setOnCompletionListener
                    }
                }
                if (playMode == 2) {
                    mediaPlayer!!.start()
                }
            }
        }
        mediaPlayer!!.setOnPreparedListener {
            mediaPlayer!!.start()
            requestFocusIfNeeded()
            syncSession()
        }
        if (songList!!.size > 0) {
            currentPosition = Random().nextInt(songList!!.size)
            currentSongInfo = songList!!.get(currentPosition)
            totalTime!!.setText(getTimeFromDuration(currentSongInfo!!.duration))
            seekBar!!.max = currentSongInfo!!.duration
            seekBar!!.progress = 0
            songName!!.text = currentSongInfo!!.songName
            playButton!!.setImageResource(R.drawable.song_play)
            try {
                mediaPlayer!!.setDataSource(currentSongInfo!!.filePath)
            } catch (e: Exception) {
            }
        }
    }

    @JvmStatic
    fun addSongByCursor(cursor: Cursor) {
        var sizeStr: String
        val song = SongInfo()
        song.fileName = cursor.getString(1)
        song.songName = cursor.getString(2)
        song.duration = cursor.getInt(3)
        song.artist = cursor.getString(4)
        song.album = cursor.getString(5)
        song.releaseYear = cursor.getString(6) ?: "undefine"
        if ("audio/mpeg" == cursor.getString(7).trim()) {
            song.fileType = "mp3"
        } else if ("audio/x-ms-wma" == cursor.getString(7).trim()) {
            song.fileType = "wma"
        }
        val sizeStr2 = cursor.getString(8)
        if (sizeStr2 != null) {
            val temp = (cursor.getInt(8) / 1024.0f) / 1024.0f
            try {
                sizeStr = StringBuilder(temp.toString()).toString().substring(0, 4)
            } catch (e: Exception) {
                sizeStr = "0"
            }
            song.fileSize = sizeStr + "M"
        } else {
            song.fileSize = "undefine"
        }
        if (cursor.getString(9) != null) {
            song.filePath = cursor.getString(9)
        }
        songList!!.add(song)
    }

    @JvmStatic
    fun getSongBarView(position: Int): View {
        var view = viewArray?.get(position)
        if (view == null) {
            val view2 = View.inflate(context, R.layout.songbar, null)
            (view2.findViewById(R.id.songbar_songname) as TextView).text = songList!!.get(position).songName
            (view2.findViewById(R.id.songbar_artist) as TextView).text = songList!!.get(position).artist
            view2.setOnClickListener {
                val currentClickTime = SystemClock.elapsedRealtime()
                if (currentClickTime - lastClickTime >= 500) {
                    lastClickTime = currentClickTime
                    if (mediaPlayer!!.isPlaying) {
                        mediaPlayer!!.stop()
                    }
                    currentPosition = position
                    val info = songList!!.get(position)
                    currentSongInfo = info
                    currentTime!!.setText("00:00")
                    totalTime!!.setText(getTimeFromDuration(info.duration))
                    seekBar!!.max = info.duration
                    seekBar!!.progress = 0
                    songName!!.text = info.songName
                    playing = true
                    playButton!!.setImageResource(R.drawable.song_pause)
                    mediaPlayer!!.reset()
                    try {
                        mediaPlayer!!.setDataSource(info.filePath)
                        mediaPlayer!!.prepare()
                        requestSongView()
                    } catch (e: Exception) {
                    }
                }
            }
            viewArray!!.put(position, view2)
            return view2
        }
        return view
    }

    @JvmStatic
    fun requestSongMenuView() {
        if (songMenuViewShow) {
            context!!.hideSongMenuView()
            songMenuViewShow = false
            if (songViewShow) {
                context!!.hideSongView()
                songViewShow = false
            }
            if (updateSeekBarThread != null) {
                updateSeekBarThread!!.stop()
                updateSeekBarThread = null
                return
            }
            return
        }
        context!!.showSongMenuView()
        songMenuViewShow = true
        if (updateSeekBarThread != null) {
            updateSeekBarThread!!.stop()
            updateSeekBarThread = null
        }
        updateSeekBarThread = Poller(100L) {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                try {
                    val cp = mediaPlayer!!.getCurrentPosition()
                    val msg = Message()
                    msg.what = cp
                    seekBarHandler.sendMessage(msg)
                } catch (e: Exception) {
                }
            }
        }
        updateSeekBarThread!!.start()
    }

    @JvmStatic
    fun requestSongView() {
        if (songViewShow) {
            context!!.hideSongView()
            songViewShow = false
            return
        }
        val p = songView!!.layoutParams as WindowManager.LayoutParams
        if (p.y == 0) {
            p.y += songMenuView!!.height
        }
        context!!.showSongView()
        songViewShow = true
        listView!!.setSelection(currentPosition)
    }

    @JvmStatic
    fun exit() {
        if (updateSeekBarThread != null) {
            updateSeekBarThread!!.stop()
            updateSeekBarThread = null
        }
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
            }
            mediaPlayer!!.release()
            mediaPlayer = null
        }
        mediaSession?.isActive = false
        mediaSession?.release()
        mediaSession = null
        audioFocusRequest?.let { req ->
            try {
                audioManager?.abandonAudioFocusRequest(req)
            } catch (e: Exception) {
            }
        }
        audioManager = null
        audioFocusRequest = null
        songView = null
        songMenuView = null
        songLoaded = false
        songList = null
        viewArray = null
    }

    private fun resumePlayback() {
        resumeAfterFocusLoss = false
        mediaPlayer!!.start()
        requestFocusIfNeeded()
        playing = true
        playButton!!.setImageResource(R.drawable.song_pause)
        syncSession()
    }

    private fun pausePlayback() {
        mediaPlayer!!.pause()
        playing = false
        playButton!!.setImageResource(R.drawable.song_play)
        syncSession()
    }

    private fun playPrevious() {
        if (songList!!.size > 0) {
            var i = currentPosition - 1
            currentPosition = i
            if (i < 0) {
                currentPosition = songList!!.size - 1
            }
            currentSongInfo = songList!!.get(currentPosition)
            totalTime!!.text = getTimeFromDuration(currentSongInfo!!.duration)
            seekBar!!.max = currentSongInfo!!.duration
            seekBar!!.progress = 0
            songName!!.text = currentSongInfo!!.songName
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            playButton!!.setImageResource(R.drawable.song_pause)
            try {
                mediaPlayer!!.setDataSource(currentSongInfo!!.filePath)
                mediaPlayer!!.prepare()
            } catch (e: Exception) {
            }
        }
    }

    private fun playNext() {
        if (songList!!.size > 0) {
            var i = currentPosition + 1
            currentPosition = i
            /* 原版 bug: i > size 应为 >=; 最后一首点下一首会越界 get(size) 崩溃,
             * 正确行为是回到第一首(与自动播放 completion 的 i > size-1 语义一致) */
            if (i > songList!!.size - 1) {
                currentPosition = 0
            }
            currentSongInfo = songList!!.get(currentPosition)
            totalTime!!.setText(getTimeFromDuration(currentSongInfo!!.duration))
            seekBar!!.max = currentSongInfo!!.duration
            seekBar!!.progress = 0
            songName!!.text = currentSongInfo!!.songName
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            playButton!!.setImageResource(R.drawable.song_pause)
            try {
                mediaPlayer!!.setDataSource(currentSongInfo!!.filePath)
                mediaPlayer!!.prepare()
            } catch (e: Exception) {
            }
        }
    }

    private fun syncSession() {
        val ms = mediaSession ?: return
        val info = currentSongInfo
        if (info != null) {
            ms.setMetadata(
                MediaMetadata.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE, info.songName ?: "")
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, info.artist ?: "")
                    .putLong(MediaMetadata.METADATA_KEY_DURATION, info.duration.toLong())
                    .build()
            )
        }
        val playingNow = try {
            mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
        ms.isActive = true
        val pbState = if (playingNow) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED
        val pos = try {
            (mediaPlayer?.currentPosition ?: 0).toLong()
        } catch (e: Exception) {
            0L
        }
        ms.setPlaybackState(
            PlaybackState.Builder()
                .setActions(
                    PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE or PlaybackState.ACTION_PLAY_PAUSE or
                        PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS
                )
                .setState(pbState, pos, 1f)
                .build()
        )
    }

    private fun requestFocusIfNeeded() {
        val afr = audioFocusRequest ?: return
        val am = audioManager ?: return
        try {
            am.requestAudioFocus(afr)
        } catch (e: Exception) {
        }
    }

    /* 音频焦点变化(已在主线程): 来电/其他应用播放时礼让, 结束后恢复 */
    private fun onFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                resumeAfterFocusLoss = playing
                pausePlayback()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (playing) {
                    ducking = true
                    try {
                        mediaPlayer!!.setVolume(0.25f, 0.25f)
                    } catch (e: Exception) {
                    }
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                resumeAfterFocusLoss = false
                pausePlayback()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (ducking) {
                    ducking = false
                    try {
                        mediaPlayer!!.setVolume(1f, 1f)
                    } catch (e: Exception) {
                    }
                }
                if (resumeAfterFocusLoss) {
                    resumeAfterFocusLoss = false
                    resumePlayback()
                }
            }
        }
    }

    @JvmStatic
    fun getTimeFromDuration(duration: Int): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return (if (minutes >= 10) minutes.toString() else "0" + minutes) + ":" +
            (if (seconds >= 10) seconds.toString() else "0" + seconds)
    }
}