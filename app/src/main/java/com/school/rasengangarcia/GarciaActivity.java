package com.school.rasengangarcia;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class GarciaActivity extends AppCompatActivity {

    private ImageView warriorImage;
    private ImageView windImage;
    private Button blastStartBtn;
    private Button blastStopBtn;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private boolean sceneActive = false;     // is the animation running right now?
    private boolean playRequested = false;   // did the user press START (and not STOP)?

    private TextToSpeech narrator;
    private boolean narratorReady = false;
    private MediaPlayer effectPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_blast);
        warriorImage = findViewById(R.id.warriorFrame);
        windImage = findViewById(R.id.windFrame);
        blastStartBtn = findViewById(R.id.blastStartBtn);
        blastStopBtn = findViewById(R.id.blastStopBtn);

        narrator = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                narrator.setLanguage(Locale.ENGLISH);
                narrator.setPitch(0.90f);
                narrator.setSpeechRate(0.90f);
                narratorReady = true;
            }
        });

        // The two buttons
        blastStartBtn.setOnClickListener(v -> {
            if (!sceneActive) {
                playRequested = true;
                launchScene();
            }
        });
        blastStopBtn.setOnClickListener(v -> {
            playRequested = false;
            freezeScene();
            resetPose();
        });

        resetPose();
    }

    // Pause when the screen is hidden; continue only if the user had pressed START
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (playRequested) launchScene();
        } else {
            freezeScene();
        }
    }

    @Override
    protected void onDestroy() {
        if (narrator != null) narrator.shutdown();
        super.onDestroy();
    }

    /** Shows the first pose with the small ball, not moving. */
    private void resetPose() {
        warriorImage.clearAnimation();
        windImage.clearAnimation();
        warriorImage.setImageResource(R.drawable.warrior_frames);
        windImage.setImageResource(R.drawable.wind_frames);
        refreshButtons();
    }

    /** START is available when stopped, STOP when playing. */
    private void refreshButtons() {
        blastStartBtn.setEnabled(!sceneActive);
        blastStopBtn.setEnabled(sceneActive);
    }

    private void launchScene() {
        sceneActive = true;
        timerHandler.removeCallbacksAndMessages(null);
        windImage.clearAnimation();

        // Step 1: play the frame animations (setting the image again restarts them)
        warriorImage.setImageResource(R.drawable.warrior_frames);
        windImage.setImageResource(R.drawable.wind_frames);
        AnimationDrawable warriorSequence = (AnimationDrawable) warriorImage.getDrawable();
        AnimationDrawable windSequence = (AnimationDrawable) windImage.getDrawable();
        warriorSequence.start();
        windSequence.start();
        shoutMove();
        refreshButtons();

        // Step 2: throw as soon as the LAST frame appears (no waiting)
        long waitMs = 0;
        for (int i = 0; i < warriorSequence.getNumberOfFrames() - 1; i++) waitMs += warriorSequence.getDuration(i);
        timerHandler.postDelayed(this::blastWind, waitMs);
    }

    private void blastWind() {
        Animation blastMotion = AnimationUtils.loadAnimation(this, R.anim.blast_wind);
        blastMotion.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Step 3: loop
                if (sceneActive) timerHandler.post(GarciaActivity.this::launchScene);
            }
        });
        windImage.startAnimation(blastMotion);
    }

    private void freezeScene() {
        sceneActive = false;
        timerHandler.removeCallbacksAndMessages(null);
        windImage.clearAnimation();
        if (effectPlayer != null) {
            effectPlayer.release();
            effectPlayer = null;
        }
        if (narrator != null) narrator.stop();
        refreshButtons();
    }

    private void shoutMove() {
        // The sound clip (res/raw/rasengan_shout.mp3) is longer than one loop:
        // let it finish, then it plays again on the next loop.
        if (effectPlayer != null && effectPlayer.isPlaying()) return;

        int resId = getResources().getIdentifier("rasengan_shout", "raw", getPackageName());
        if (resId != 0) {
            if (effectPlayer != null) effectPlayer.release();
            effectPlayer = MediaPlayer.create(this, resId);
            if (effectPlayer != null) {
                effectPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    if (effectPlayer == mp) effectPlayer = null;
                });
                effectPlayer.start();
            }
        } else if (narratorReady) {
            narrator.speak("Rasengan!", TextToSpeech.QUEUE_FLUSH, null, "rasengan");
        }
    }
}
