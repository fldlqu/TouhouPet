package k.p.song;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import k.p.main.MainService;
import k.p.main.R;

/* JADX INFO: loaded from: classes.dex */
public class SongService {
    private static MainService context;
    private static int currentPosition;
    private static SongInfo currentSongInfo;
    private static TextView currentTime;
    private static long lastClickTime;
    private static ListView listView;
    private static boolean loopFlag;
    private static MediaPlayer mediaPlayer;
    private static ImageView nextButton;
    private static ImageView playButton;
    private static int playMode;
    private static ImageView playModeButton;
    private static ImageView previousButton;
    private static SeekBar seekBar;
    private static List<SongInfo> songList;
    private static ImageView songListButton;
    public static View songMenuView;
    private static TextView songName;
    public static View songView;
    private static TextView totalTime;
    private static UpdateSeekBarTask updateSeekBarThread;
    private static SparseArray<View> viewArray;
    public static boolean songViewShow = false;
    public static boolean songMenuViewShow = false;
    private static boolean playing = false;
    private static Handler seekBarHandler = new Handler() { // from class: k.p.song.SongService.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            SongService.seekBar.setProgress(msg.what);
            SongService.currentTime.setText(SongService.getTimeFromDuration(msg.what));
        }
    };

    public static void loadSong(Context context2) {
        if (songList == null) {
            context = (MainService) context2;
            songList = new ArrayList();
            viewArray = new SparseArray<>();
            mediaPlayer = new MediaPlayer();
            Cursor cursor = context2.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_display_name", "title", "duration", "artist", "album", "year", "mime_type", "_size", "_data"}, "mime_type=? or mime_type=?", new String[]{"audio/mpeg", "audio/x-ms-wma"}, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            addSongByCursor(cursor);
                        } catch (Exception e) {
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            songView = View.inflate(context2, R.layout.song, null);
            listView = (ListView) songView.findViewById(R.id.song_listview);
            listView.setAdapter((ListAdapter) new BaseAdapter() { // from class: k.p.song.SongService.2
                @Override // android.widget.Adapter
                public View getView(int position, View convertView, ViewGroup parent) {
                    return SongService.getSongBarView(position);
                }

                @Override // android.widget.Adapter
                public long getItemId(int position) {
                    return position;
                }

                @Override // android.widget.Adapter
                public Object getItem(int position) {
                    return SongService.songList.get(position);
                }

                @Override // android.widget.Adapter
                public int getCount() {
                    return SongService.songList.size();
                }
            });
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            params.flags = 520;
            params.gravity = 85;
            params.width = 400;
            params.height = 800;
            params.format = 1;
            songView.setLayoutParams(params);
            songMenuView = View.inflate(context2, R.layout.songmenu, null);
            seekBar = (SeekBar) songMenuView.findViewById(R.id.songmenu_progress);
            currentTime = (TextView) songMenuView.findViewById(R.id.songmenu_currenttime);
            totalTime = (TextView) songMenuView.findViewById(R.id.songmenu_totaltime);
            songName = (TextView) songMenuView.findViewById(R.id.songmenu_songname);
            playModeButton = (ImageView) songMenuView.findViewById(R.id.songmenu_playmode);
            previousButton = (ImageView) songMenuView.findViewById(R.id.songmenu_previous);
            playButton = (ImageView) songMenuView.findViewById(R.id.songmenu_play);
            nextButton = (ImageView) songMenuView.findViewById(R.id.songmenu_next);
            songListButton = (ImageView) songMenuView.findViewById(R.id.songmenu_songlist);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: k.p.song.SongService.3
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar2, int progress, boolean fromUser) {
                    if (fromUser) {
                        SongService.mediaPlayer.seekTo(progress);
                        seekBar2.setProgress(progress);
                    }
                }
            });
            playModeButton.setOnClickListener(new View.OnClickListener() { // from class: k.p.song.SongService.4
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (SongService.playMode == 0) {
                        SongService.playMode = 1;
                        SongService.playModeButton.setImageResource(R.drawable.song_randomloop);
                    } else if (SongService.playMode == 1) {
                        SongService.playMode = 2;
                        SongService.playModeButton.setImageResource(R.drawable.song_singleloop);
                    } else if (SongService.playMode == 2) {
                        SongService.playMode = 0;
                        SongService.playModeButton.setImageResource(R.drawable.song_allloop);
                    }
                }
            });
            playButton.setOnClickListener(new View.OnClickListener() { // from class: k.p.song.SongService.5
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (SongService.songList.size() > 0) {
                        if (SongService.playing) {
                            SongService.mediaPlayer.pause();
                            SongService.playing = false;
                            SongService.playButton.setImageResource(R.drawable.song_play);
                        } else {
                            SongService.mediaPlayer.start();
                            SongService.playing = true;
                            SongService.playButton.setImageResource(R.drawable.song_pause);
                        }
                    }
                }
            });
            previousButton.setOnClickListener(new View.OnClickListener() { // from class: k.p.song.SongService.6
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (SongService.songList.size() > 0) {
                        int i = SongService.currentPosition - 1;
                        SongService.currentPosition = i;
                        if (i < 0) {
                            SongService.currentPosition = SongService.songList.size() - 1;
                        }
                        SongService.currentSongInfo = (SongInfo) SongService.songList.get(SongService.currentPosition);
                        SongService.totalTime.setText(SongService.getTimeFromDuration(SongService.currentSongInfo.getDuration()));
                        SongService.seekBar.setMax(SongService.currentSongInfo.getDuration());
                        SongService.seekBar.setProgress(0);
                        SongService.songName.setText(SongService.currentSongInfo.getSongName());
                        SongService.mediaPlayer.stop();
                        SongService.playButton.setImageResource(R.drawable.song_pause);
                        SongService.mediaPlayer.reset();
                        try {
                            SongService.mediaPlayer.setDataSource(SongService.currentSongInfo.getFilePath());
                            SongService.mediaPlayer.prepare();
                        } catch (Exception e2) {
                        }
                    }
                }
            });
            nextButton.setOnClickListener(new View.OnClickListener() { // from class: k.p.song.SongService.7
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (SongService.songList.size() > 0) {
                        int i = SongService.currentPosition + 1;
                        SongService.currentPosition = i;
                        if (i > SongService.songList.size()) {
                            SongService.currentPosition = 0;
                        }
                        SongService.currentSongInfo = (SongInfo) SongService.songList.get(SongService.currentPosition);
                        SongService.totalTime.setText(SongService.getTimeFromDuration(SongService.currentSongInfo.getDuration()));
                        SongService.seekBar.setMax(SongService.currentSongInfo.getDuration());
                        SongService.seekBar.setProgress(0);
                        SongService.songName.setText(SongService.currentSongInfo.getSongName());
                        SongService.mediaPlayer.stop();
                        SongService.playButton.setImageResource(R.drawable.song_pause);
                        SongService.mediaPlayer.reset();
                        try {
                            SongService.mediaPlayer.setDataSource(SongService.currentSongInfo.getFilePath());
                            SongService.mediaPlayer.prepare();
                        } catch (Exception e2) {
                        }
                    }
                }
            });
            songListButton.setOnClickListener(new View.OnClickListener() { // from class: k.p.song.SongService.8
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    SongService.requestSongView();
                }
            });
            WindowManager.LayoutParams params2 = new WindowManager.LayoutParams();
            params2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            params2.flags = 520;
            params2.gravity = 80;
            params2.width = -1;
            params2.height = -2;
            params2.format = 1;
            songMenuView.setLayoutParams(params2);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: k.p.song.SongService.9
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mp) {
                    if (SongService.currentSongInfo.getDuration() - SongService.mediaPlayer.getDuration() <= 50) {
                        if (SongService.playMode == 0) {
                            int i = SongService.currentPosition + 1;
                            SongService.currentPosition = i;
                            if (i > SongService.songList.size() - 1) {
                                SongService.currentPosition = 0;
                            }
                            SongService.currentSongInfo = (SongInfo) SongService.songList.get(SongService.currentPosition);
                            SongService.totalTime.setText(SongService.getTimeFromDuration(SongService.currentSongInfo.getDuration()));
                            SongService.seekBar.setMax(SongService.currentSongInfo.getDuration());
                            SongService.seekBar.setProgress(0);
                            SongService.songName.setText(SongService.currentSongInfo.getSongName());
                            SongService.mediaPlayer.stop();
                            SongService.playButton.setImageResource(R.drawable.song_pause);
                            SongService.mediaPlayer.reset();
                            try {
                                SongService.mediaPlayer.setDataSource(SongService.currentSongInfo.getFilePath());
                                SongService.mediaPlayer.prepare();
                                return;
                            } catch (Exception e2) {
                                return;
                            }
                        }
                        if (SongService.playMode == 1) {
                            SongService.currentPosition = new Random().nextInt(SongService.songList.size());
                            SongService.currentSongInfo = (SongInfo) SongService.songList.get(SongService.currentPosition);
                            SongService.totalTime.setText(SongService.getTimeFromDuration(SongService.currentSongInfo.getDuration()));
                            SongService.seekBar.setMax(SongService.currentSongInfo.getDuration());
                            SongService.seekBar.setProgress(0);
                            SongService.songName.setText(SongService.currentSongInfo.getSongName());
                            SongService.mediaPlayer.stop();
                            SongService.playButton.setImageResource(R.drawable.song_pause);
                            SongService.mediaPlayer.reset();
                            try {
                                SongService.mediaPlayer.setDataSource(SongService.currentSongInfo.getFilePath());
                                SongService.mediaPlayer.prepare();
                                return;
                            } catch (Exception e3) {
                                return;
                            }
                        }
                        if (SongService.playMode == 2) {
                            SongService.mediaPlayer.start();
                        }
                    }
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: k.p.song.SongService.10
                @Override // android.media.MediaPlayer.OnPreparedListener
                public void onPrepared(MediaPlayer mp) {
                    SongService.mediaPlayer.start();
                }
            });
            if (songList.size() > 0) {
                currentPosition = new Random().nextInt(songList.size());
                currentSongInfo = songList.get(currentPosition);
                totalTime.setText(getTimeFromDuration(currentSongInfo.getDuration()));
                seekBar.setMax(currentSongInfo.getDuration());
                seekBar.setProgress(0);
                songName.setText(currentSongInfo.getSongName());
                playButton.setImageResource(R.drawable.song_play);
                try {
                    mediaPlayer.setDataSource(currentSongInfo.getFilePath());
                } catch (Exception e2) {
                }
            }
        }
    }

    public static void addSongByCursor(Cursor cursor) {
        String sizeStr;
        SongInfo song = new SongInfo();
        song.setFileName(cursor.getString(1));
        song.setSongName(cursor.getString(2));
        song.setDuration(cursor.getInt(3));
        song.setArtist(cursor.getString(4));
        song.setAlbum(cursor.getString(5));
        if (cursor.getString(6) != null) {
            song.setReleaseYear(cursor.getString(6));
        } else {
            song.setReleaseYear("undefine");
        }
        if ("audio/mpeg".equals(cursor.getString(7).trim())) {
            song.setFileType("mp3");
        } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
            song.setFileType("wma");
        }
        if (cursor.getString(8) != null) {
            float temp = (cursor.getInt(8) / 1024.0f) / 1024.0f;
            try {
                sizeStr = new StringBuilder(String.valueOf(temp)).toString().substring(0, 4);
            } catch (Exception e) {
                sizeStr = "0";
            }
            song.setFileSize(String.valueOf(sizeStr) + "M");
        } else {
            song.setFileSize("undefine");
        }
        if (cursor.getString(9) != null) {
            song.setFilePath(cursor.getString(9));
        }
        songList.add(song);
    }

    public static View getSongBarView(final int position) {
        View view = viewArray.get(position);
        if (view == null) {
            View view2 = View.inflate(context, R.layout.songbar, null);
            ((TextView) view2.findViewById(R.id.songbar_songname)).setText(songList.get(position).getSongName());
            ((TextView) view2.findViewById(R.id.songbar_artist)).setText(songList.get(position).getArtist());
            view2.setOnClickListener(new View.OnClickListener() { // from class: k.p.song.SongService.11
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    long currentClickTime = SystemClock.elapsedRealtime();
                    if (currentClickTime - SongService.lastClickTime >= 500) {
                        SongService.lastClickTime = currentClickTime;
                        if (SongService.mediaPlayer.isPlaying()) {
                            SongService.mediaPlayer.stop();
                        }
                        SongService.currentPosition = position;
                        SongInfo info = (SongInfo) SongService.songList.get(position);
                        SongService.currentSongInfo = info;
                        SongService.currentTime.setText("00:00");
                        SongService.totalTime.setText(SongService.getTimeFromDuration(info.getDuration()));
                        SongService.seekBar.setMax(info.getDuration());
                        SongService.seekBar.setProgress(0);
                        SongService.songName.setText(info.getSongName());
                        SongService.playing = true;
                        SongService.playButton.setImageResource(R.drawable.song_pause);
                        SongService.mediaPlayer.reset();
                        try {
                            SongService.mediaPlayer.setDataSource(info.getFilePath());
                            SongService.mediaPlayer.prepare();
                            SongService.requestSongView();
                        } catch (Exception e) {
                        }
                    }
                }
            });
            viewArray.put(position, view2);
            return view2;
        }
        return view;
    }

    public static void requestSongMenuView() {
        UpdateSeekBarTask updateSeekBarTask = null;
        if (songMenuViewShow) {
            context.hideSongMenuView();
            songMenuViewShow = false;
            if (songViewShow) {
                context.hideSongView();
                songViewShow = false;
            }
            if (updateSeekBarThread != null) {
                loopFlag = false;
                updateSeekBarThread = null;
                return;
            }
            return;
        }
        context.showSongMenuView();
        songMenuViewShow = true;
        if (updateSeekBarThread != null) {
            updateSeekBarThread.interrupt();
            updateSeekBarThread = null;
        }
        updateSeekBarThread = new UpdateSeekBarTask(updateSeekBarTask);
        loopFlag = true;
        updateSeekBarThread.start();
    }

    public static void requestSongView() {
        if (songViewShow) {
            context.hideSongView();
            songViewShow = false;
            return;
        }
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) songView.getLayoutParams();
        if (p.y == 0) {
            p.y += songMenuView.getHeight();
        }
        context.showSongView();
        songViewShow = true;
        listView.setSelection(currentPosition);
    }

    public static void exit() {
        if (updateSeekBarThread != null) {
            loopFlag = false;
            updateSeekBarThread = null;
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        songView = null;
        songMenuView = null;
        songList = null;
        viewArray = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getTimeFromDuration(int duration) {
        int minutes = (duration / 1000) / 60;
        int seconds = (duration / 1000) % 60;
        return (minutes >= 10 ? Integer.valueOf(minutes) : "0" + minutes) + ":" + (seconds >= 10 ? Integer.valueOf(seconds) : "0" + seconds);
    }

    private static class UpdateSeekBarTask extends Thread {
        private UpdateSeekBarTask() {
        }

        /* synthetic */ UpdateSeekBarTask(UpdateSeekBarTask updateSeekBarTask) {
            this();
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (SongService.loopFlag) {
                if (SongService.mediaPlayer != null && SongService.mediaPlayer.isPlaying()) {
                    try {
                        int currentPosition = SongService.mediaPlayer.getCurrentPosition();
                        Message msg = new Message();
                        msg.what = currentPosition;
                        SongService.seekBarHandler.sendMessage(msg);
                    } catch (Exception e) {
                    }
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e2) {
                }
            }
        }
    }
}
