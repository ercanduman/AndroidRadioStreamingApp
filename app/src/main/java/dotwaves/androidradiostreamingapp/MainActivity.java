package dotwaves.androidradiostreamingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dotwaves.androidradiostreamingapp.R;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener {

    private ProgressDialog dialog;
    private static Context context;
    private static String STREAM_URL;
    private ImageButton btnPlay;
    private MediaPlayer player;
    private SeekBar bar;
    private AudioManager audioManager;
    private static boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        init();
    }

    private void init() {
        STREAM_URL = context.getResources().getString(R.string.stream_url);

        TextView desc = (TextView) findViewById(R.id.textView);
        desc.setText(Html.fromHtml(getString(R.string.by_ercan_duman)));

        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.loading));
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        player = new MediaPlayer();
        audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        btnPlay = (ImageButton) findViewById(R.id.btnPlayPause);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRadio();
            }
        });
        btnPlay.setImageResource(android.R.drawable.ic_media_play);

        seekBarStuff();
    }

    private void seekBarStuff() {
        bar = (SeekBar) findViewById(R.id.seekBar);
        bar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        bar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //Pressing play button
    public void startRadio() {
        if (CheckNetwork.isNetworkAvailable(context)) {
            dialog.show();
            if (!isPlaying) {
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                playMusic();
            } else {
                stopRadio();
            }
        } else {
            if (isPlaying) {
                stopRadio();
            }
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void stopRadio() {
        isPlaying = false;
        if (dialog != null) dialog.dismiss();
        if (player != null && player.isPlaying()) {
            player.stop();
            player.reset();
        }
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
    }

    private void playMusic() {
        new Thread((new Runnable() {
            @Override
            public void run() {
                try {
                    player.setAudioStreamType(audioManager.STREAM_MUSIC);
                    player.setDataSource(STREAM_URL);
                    player.prepareAsync();
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                            isPlaying = true;
                            if (dialog != null) dialog.dismiss();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        })).start();
    }

    //Listen for the volume button events
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_VOLUME_DOWN) {
            int currVolume = bar.getProgress();
            bar.setProgress(currVolume - 1);
            return true;
        } else if (keyCode == event.KEYCODE_VOLUME_UP) {
            int currVolume = bar.getProgress();
            bar.setProgress(currVolume + 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRadio();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    public void getMoreApps(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=ercanduman")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=ercanduman")));
        }
    }

    public void getItOnGooglePlay(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ercanduman.radioom")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=ercanduman.radioom")));
        }
    }

    public void goGithub(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ercanduman")));
    }

    // check if any phone call receives, if so, stop music
    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange <= 0) {
            //LOSS -> pause
            stopRadio();
        } else {
            // GAIN -> play
            startRadio();
        }
    }
}
