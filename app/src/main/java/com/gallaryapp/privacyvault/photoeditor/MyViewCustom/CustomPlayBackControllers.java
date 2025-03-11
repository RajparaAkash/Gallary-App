//package com.gallaryapp.privacyvault.photoeditor.MyViewCustom;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.drawable.Drawable;
//import android.os.SystemClock;
//import android.util.AttributeSet;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import androidx.core.content.ContextCompat;
//
//import com.google.android.exoplayer2.ExoPlaybackException;
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.PlaybackParameters;
//import com.google.android.exoplayer2.Player;
//import com.google.android.exoplayer2.Timeline;
//import com.google.android.exoplayer2.source.TrackGroupArray;
//import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
//import com.gallaryapp.privacyvault.photoeditor.R;
//
//import java.util.Formatter;
//import java.util.Locale;
//
//public class CustomPlayBackControllers extends FrameLayout {
//
//    private LinearLayout exoplayer_controller_background;
//    private final ComponentListener componentListener;
//    private boolean dragging;
//    private final ImageView fastForwardButton;
//    private int fastForwardMs;
//    private final StringBuilder formatBuilder;
//    private final Formatter formatter;
//    private final Runnable hideAction;
//    private long hideAtMs;
//    private boolean isAttachedToWindow;
//    private final View nextButton;
//    private final ImageView playButton;
//    private ExoPlayer player;
//    private final View previousButton;
//    private final SeekBar progressBar;
//    private final ImageView rewindButton;
//    private int rewindMs;
//    private int showTimeoutMs;
//    private final TextView time;
//    private final TextView timeCurrent;
//    private final Runnable updateProgressAction;
//    private VisibilityListener visibilityListener;
//    private final Timeline.Window window;
//
//
//    public interface VisibilityListener {
//        void onVisibilityChange(int i);
//    }
//
//    public CustomPlayBackControllers(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//
//    public CustomPlayBackControllers(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.updateProgressAction = new Runnable() {
//            @Override
//            public void run() {
//                CustomPlayBackControllers.this.updateProgress();
//            }
//        };
//        this.hideAction = new Runnable() {
//            @Override
//            public void run() {
//                CustomPlayBackControllers.this.hide();
//            }
//        };
//        this.rewindMs = 5000;
//        this.fastForwardMs = 15000;
//        this.showTimeoutMs = 5000;
//        if (attrs != null) {
//            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlaybackControlView, 0, 0);
//            try {
//                this.rewindMs = a.getInt(3, this.rewindMs);
//                this.fastForwardMs = a.getInt(1, this.fastForwardMs);
//                this.showTimeoutMs = a.getInt(5, this.showTimeoutMs);
//            } finally {
//                a.recycle();
//            }
//        }
//        this.window = new Timeline.Window();
//        StringBuilder sb = new StringBuilder();
//        this.formatBuilder = sb;
//        this.formatter = new Formatter(sb, Locale.getDefault());
//        ComponentListener componentListener = new ComponentListener();
//        this.componentListener = componentListener;
//        LayoutInflater.from(context).inflate(R.layout.exo_medias_control, this);
//        this.time = (TextView) findViewById(R.id.time);
//        this.timeCurrent = (TextView) findViewById(R.id.time_current);
//        SeekBar seekBar = (SeekBar) findViewById(R.id.mediacontroller_progress);
//        this.progressBar = seekBar;
//        seekBar.setOnSeekBarChangeListener(componentListener);
//        seekBar.setMax(1000);
//        ImageView imageView = (ImageView) findViewById(R.id.play);
//        this.playButton = imageView;
//        imageView.setOnClickListener(componentListener);
//        View findViewById = findViewById(R.id.prev);
//        this.previousButton = findViewById;
//        findViewById.setOnClickListener(componentListener);
//        View findViewById2 = findViewById(R.id.next);
//        this.nextButton = findViewById2;
//        findViewById2.setOnClickListener(componentListener);
//        ImageView imageView2 = (ImageView) findViewById(R.id.rew);
//        this.rewindButton = imageView2;
//        imageView2.setOnClickListener(componentListener);
//        ImageView imageView3 = (ImageView) findViewById(R.id.ffwd);
//        this.fastForwardButton = imageView3;
//        imageView3.setOnClickListener(componentListener);
//
//        exoplayer_controller_background = (LinearLayout) findViewById(R.id.exoplayer_controller_background);
//        exoplayer_controller_background.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//    }
//
//    public ExoPlayer getPlayer() {
//        return this.player;
//    }
//
//    public void setPlayer(ExoPlayer player) {
//        ExoPlayer exoPlayer = this.player;
//        if (exoPlayer == player) {
//            return;
//        }
//        if (exoPlayer != null) {
//            exoPlayer.removeListener(this.componentListener);
//        }
//        this.player = player;
//        if (player != null) {
//            player.addListener(this.componentListener);
//        }
//        updateAll();
//    }
//
//    public void setVisibilityListener(VisibilityListener listener) {
//        this.visibilityListener = listener;
//    }
//
//    public void setRewindIncrementMs(int rewindMs) {
//        this.rewindMs = rewindMs;
//        updateNavigation();
//    }
//
//    public void setFastForwardIncrementMs(int fastForwardMs) {
//        this.fastForwardMs = fastForwardMs;
//        updateNavigation();
//    }
//
//    public int getShowTimeoutMs() {
//        return this.showTimeoutMs;
//    }
//
//    public void setShowTimeoutMs(int showTimeoutMs) {
//        this.showTimeoutMs = showTimeoutMs;
//    }
//
//    public void show() {
//        if (!isVisible()) {
//            setVisibility(View.VISIBLE);
//            VisibilityListener visibilityListener = this.visibilityListener;
//            if (visibilityListener != null) {
//                visibilityListener.onVisibilityChange(getVisibility());
//            }
//            updateAll();
//        }
//        hideAfterTimeout();
//    }
//
//    public void hide() {
//        if (isVisible()) {
//            setVisibility(View.GONE);
//            VisibilityListener visibilityListener = this.visibilityListener;
//            if (visibilityListener != null) {
//                visibilityListener.onVisibilityChange(getVisibility());
//            }
//            removeCallbacks(this.updateProgressAction);
//            removeCallbacks(this.hideAction);
//            this.hideAtMs = -9223372036854775807L;
//        }
//    }
//
//    public boolean isVisible() {
//        return getVisibility() == View.VISIBLE;
//    }
//
//
//    public void hideAfterTimeout() {
//        removeCallbacks(this.hideAction);
//        if (this.showTimeoutMs > 0) {
//            long uptimeMillis = SystemClock.uptimeMillis();
//            int i = this.showTimeoutMs;
//            this.hideAtMs = uptimeMillis + i;
//            if (this.isAttachedToWindow) {
//                postDelayed(this.hideAction, i);
//                return;
//            }
//            return;
//        }
//        this.hideAtMs = -9223372036854775807L;
//    }
//
//    private void updateAll() {
//        updatePlayPauseButton();
//        updateNavigation();
//        updateProgress();
//    }
//
//
//    public void updatePlayPauseButton() {
//        if (!isVisible() || !this.isAttachedToWindow) {
//            return;
//        }
//        ExoPlayer exoPlayer = this.player;
//        boolean playing = exoPlayer != null && exoPlayer.getPlayWhenReady();
//        String contentDescription = getResources().getString(playing ? R.string.str_159 : R.string.str_160);
//        this.playButton.setContentDescription(contentDescription);
//        Drawable playDrawable = ContextCompat.getDrawable(getContext(), R.drawable.exo_controls_play);
//        Drawable pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.exo_controls_pause);
//        this.playButton.setImageDrawable(playing ? pauseDrawable : playDrawable);
//    }
//
//
//    public void updatePausePlayButton() {
//        if (!isVisible() || !this.isAttachedToWindow) {
//            return;
//        }
//        ExoPlayer exoPlayer = this.player;
//        boolean playing = exoPlayer != null && exoPlayer.getPlayWhenReady();
//        String contentDescription = getResources().getString(playing ? R.string.str_159 : R.string.str_160);
//        this.playButton.setContentDescription(contentDescription);
//        Drawable playDrawable = ContextCompat.getDrawable(getContext(), R.drawable.exo_controls_play);
//        Drawable pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.exo_controls_pause);
//        this.playButton.setImageDrawable(playing ? pauseDrawable : playDrawable);
//    }
//
//
//    public void updateNavigation() {
//        if (!isVisible() || !this.isAttachedToWindow) {
//            return;
//        }
//        ExoPlayer exoPlayer = this.player;
//        Timeline timeline = exoPlayer != null ? exoPlayer.getCurrentTimeline() : null;
//        boolean z = true;
//        boolean haveNonEmptyTimeline = (timeline == null || timeline.isEmpty()) ? false : true;
//        boolean isSeekable = false;
//        if (haveNonEmptyTimeline && !this.player.isPlayingAd()) {
//            int windowIndex = this.player.getCurrentWindowIndex();
//            timeline.getWindow(windowIndex, this.window);
//            Timeline.Window window = this.window;
//            isSeekable = window.isSeekable;
//            boolean z2 = (!isSeekable && window.isDynamic && this.player.getPreviousWindowIndex() == -1) ? false : true;
//            boolean z3 = this.window.isDynamic || this.player.getNextWindowIndex() != -1;
//        }
//        setButtonEnabled(false, this.previousButton, true);
//        setButtonEnabled(false, this.nextButton, true);
//        setButtonEnabled(this.fastForwardMs > 0 && isSeekable, this.fastForwardButton, false);
//        setButtonEnabled((this.rewindMs <= 0 || !isSeekable) ? false : true, this.rewindButton, false);
//        this.progressBar.setEnabled(isSeekable);
//    }
//
//
//    public void updateProgress() {
//        long delayMs;
//        if (!isVisible() || !this.isAttachedToWindow) {
//            return;
//        }
//        ExoPlayer exoPlayer = this.player;
//        long duration = exoPlayer == null ? 0L : exoPlayer.getDuration();
//        ExoPlayer exoPlayer2 = this.player;
//        long position = exoPlayer2 == null ? 0L : exoPlayer2.getCurrentPosition();
//        this.time.setText(stringForTime(duration));
//        if (!this.dragging) {
//            this.timeCurrent.setText(stringForTime(position));
//        }
//        if (!this.dragging) {
//            this.progressBar.setProgress(progressBarValue(position));
//        }
//        /*ExoPlayer exoPlayer3 = this.player;
//        long bufferedPosition = exoPlayer3 != null ? exoPlayer3.getBufferedPosition() : 0L;
//        this.progressBar.setSecondaryProgress(progressBarValue(bufferedPosition));*/
//        removeCallbacks(this.updateProgressAction);
//        ExoPlayer exoPlayer4 = this.player;
//        int playbackState = exoPlayer4 == null ? 1 : exoPlayer4.getPlaybackState();
//        if (playbackState != 1 && playbackState != 4) {
//            if (this.player.getPlayWhenReady() && playbackState == 3) {
//                delayMs = 1000 - (position % 1000);
//                if (delayMs < 200) {
//                    delayMs += 1000;
//                }
//            } else {
//                delayMs = 1000;
//            }
//            postDelayed(this.updateProgressAction, delayMs);
//        }
//    }
//
//    private void setButtonEnabled(boolean enabled, View view, boolean hide) {
//        view.setEnabled(enabled);
//        if (!hide) {
//            setViewAlphaV11(view, enabled ? 1.0f : 0.3f);
//            view.setVisibility(View.VISIBLE);
//            return;
//        }
//        view.setVisibility(enabled ? View.VISIBLE : View.GONE);
//    }
//
//    @TargetApi(11)
//    private void setViewAlphaV11(View view, float alpha) {
//        view.setAlpha(alpha);
//    }
//
//
//    public String stringForTime(long timeMs) {
//        long timeMs2;
//        if (timeMs != -9223372036854775807L) {
//            timeMs2 = timeMs;
//        } else {
//            timeMs2 = 0;
//        }
//        long totalSeconds = (500 + timeMs2) / 1000;
//        long seconds = totalSeconds % 60;
//        long minutes = (totalSeconds / 60) % 60;
//        long hours = totalSeconds / 3600;
//        this.formatBuilder.setLength(0);
//        return hours > 0 ? this.formatter.format("%d:%02d:%02d", Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)).toString() : this.formatter.format("%02d:%02d", Long.valueOf(minutes), Long.valueOf(seconds)).toString();
//    }
//
//    private int progressBarValue(long position) {
//        ExoPlayer exoPlayer = this.player;
//        long duration = exoPlayer == null ? -9223372036854775807L : exoPlayer.getDuration();
//        if (duration == -9223372036854775807L || duration == 0) {
//            return 0;
//        }
//        return (int) ((1000 * position) / duration);
//    }
//
//
//    public long positionValue(int progress) {
//        ExoPlayer exoPlayer = this.player;
//        long duration = exoPlayer == null ? -9223372036854775807L : exoPlayer.getDuration();
//        if (duration == -9223372036854775807L) {
//            return 0L;
//        }
//        return (progress * duration) / 1000;
//    }
//
//
//    public void previous() {
//        Timeline currentTimeline = this.player.getCurrentTimeline();
//        if (currentTimeline == null) {
//            return;
//        }
//        int currentWindowIndex = this.player.getCurrentWindowIndex();
//        currentTimeline.getWindow(currentWindowIndex, this.window);
//        if (currentWindowIndex > 0) {
//            if (this.player.getCurrentPosition() > 3000) {
//                Timeline.Window window = this.window;
//                if (window.isDynamic) {
//                }
//            }
//            this.player.seekToDefaultPosition(currentWindowIndex - 1);
//            return;
//        }
//        this.player.seekTo(0L);
//    }
//
//
//    public void next() {
//        Timeline currentTimeline = this.player.getCurrentTimeline();
//        if (currentTimeline == null) {
//            return;
//        }
//        int currentWindowIndex = this.player.getCurrentWindowIndex();
//        if (currentWindowIndex < currentTimeline.getWindowCount() - 1) {
//            this.player.seekToDefaultPosition(currentWindowIndex + 1);
//        } else if (currentTimeline.getWindow(currentWindowIndex, this.window, false).isDynamic) {
//            this.player.seekToDefaultPosition();
//        }
//    }
//
//
//    public void rewind() {
//        if (this.rewindMs <= 0) {
//            return;
//        }
//        ExoPlayer exoPlayer = this.player;
//        exoPlayer.seekTo(Math.max(exoPlayer.getCurrentPosition() - this.rewindMs, 0L));
//    }
//
//
//    public void fastForward() {
//        if (this.fastForwardMs <= 0) {
//            return;
//        }
//        ExoPlayer exoPlayer = this.player;
//        exoPlayer.seekTo(Math.min(exoPlayer.getCurrentPosition() + this.fastForwardMs, this.player.getDuration()));
//    }
//
//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        this.isAttachedToWindow = true;
//        long j = this.hideAtMs;
//        if (j != -9223372036854775807L) {
//            long delayMs = j - SystemClock.uptimeMillis();
//            if (delayMs <= 0) {
//                hide();
//            } else {
//                postDelayed(this.hideAction, delayMs);
//            }
//        }
//        updateAll();
//    }
//
//    @Override
//    public void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        this.isAttachedToWindow = false;
//        removeCallbacks(this.updateProgressAction);
//        removeCallbacks(this.hideAction);
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (this.player == null || event.getAction() != 0) {
//            return super.dispatchKeyEvent(event);
//        }
//        switch (event.getKeyCode()) {
//            case 21:
//            case 89:
//                rewind();
//                break;
//            case 22:
//            case 90:
//                fastForward();
//                break;
//            case 85:
//                ExoPlayer exoPlayer = this.player;
//                exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
//                break;
//            case 87:
//                next();
//                break;
//            case 88:
//                previous();
//                break;
//            case 127:
//                this.player.setPlayWhenReady(false);
//                break;
//            default:
//                return false;
//        }
//        show();
//        return true;
//    }
//
//
//    private final class ComponentListener implements Player.EventListener, SeekBar.OnSeekBarChangeListener, OnClickListener {
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//            CustomPlayBackControllers customPlayBackControllers = CustomPlayBackControllers.this;
//            customPlayBackControllers.removeCallbacks(customPlayBackControllers.hideAction);
//            CustomPlayBackControllers.this.dragging = true;
//        }
//
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if (fromUser) {
//                TextView textView = CustomPlayBackControllers.this.timeCurrent;
//                CustomPlayBackControllers customPlayBackControllers = CustomPlayBackControllers.this;
//                textView.setText(customPlayBackControllers.stringForTime(customPlayBackControllers.positionValue(progress)));
//            }
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            CustomPlayBackControllers.this.dragging = false;
//            CustomPlayBackControllers.this.player.seekTo(CustomPlayBackControllers.this.positionValue(seekBar.getProgress()));
//            CustomPlayBackControllers.this.hideAfterTimeout();
//        }
//
//        @Override
//        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            CustomPlayBackControllers.this.updatePlayPauseButton();
//            CustomPlayBackControllers.this.updateProgress();
//            if (playbackState == 4) {
//                CustomPlayBackControllers.this.player.setPlayWhenReady(false);
//                CustomPlayBackControllers.this.updatePausePlayButton();
//            }
//        }
//
//        public void onTimelineChanged(Timeline timeline, Object manifest) {
//            CustomPlayBackControllers.this.updateNavigation();
//            CustomPlayBackControllers.this.updateProgress();
//        }
//
//        @Override
//        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//        }
//
//        @Override
//        public void onLoadingChanged(boolean isLoading) {
//        }
//
//        @Override
//        public void onPlayerError(ExoPlaybackException error) {
//        }
//
//        @Override
//        public void onPositionDiscontinuity(int reason) {
//        }
//
//        @Override
//        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//        }
//
//        @Override
//        public void onSeekProcessed() {
//        }
//
//        @Override
//        public void onClick(View view) {
//            Timeline currentTimeline = CustomPlayBackControllers.this.player.getCurrentTimeline();
//            if (CustomPlayBackControllers.this.nextButton == view) {
//                CustomPlayBackControllers.this.next();
//            } else if (CustomPlayBackControllers.this.previousButton == view) {
//                CustomPlayBackControllers.this.previous();
//            } else if (CustomPlayBackControllers.this.fastForwardButton == view) {
//                CustomPlayBackControllers.this.fastForward();
//            } else if (CustomPlayBackControllers.this.rewindButton != view || currentTimeline == null) {
//                if (CustomPlayBackControllers.this.playButton == view) {
//                    CustomPlayBackControllers.this.player.setPlayWhenReady(!CustomPlayBackControllers.this.player.getPlayWhenReady());
//                }
//            } else {
//                CustomPlayBackControllers.this.rewind();
//            }
//            CustomPlayBackControllers.this.hideAfterTimeout();
//        }
//    }
//}
