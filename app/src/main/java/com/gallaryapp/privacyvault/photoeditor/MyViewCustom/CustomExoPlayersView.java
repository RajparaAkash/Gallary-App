//package com.gallaryapp.privacyvault.photoeditor.MyViewCustom;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.SurfaceView;
//import android.view.TextureView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.Player;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.text.Cue;
//import com.google.android.exoplayer2.text.TextOutput;
//import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
//import com.google.android.exoplayer2.ui.SubtitleView;
//import com.gallaryapp.privacyvault.photoeditor.R;
//import com.google.android.exoplayer2.video.VideoSize;
//
//import java.util.List;
//
//public final class CustomExoPlayersView extends FrameLayout {
//    private final ComponentListener componentListener;
//    private final CustomPlayBackControllers controller;
//    private int controllerShowTimeoutMs;
//    private final AspectRatioFrameLayout layout;
//    private ExoPlayer player;
//    private final View shutterView;
//    private final SubtitleView subtitleLayout;
//    private final View surfaceView;
//    private boolean useController;
//
//    public CustomExoPlayersView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public CustomExoPlayersView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.useController = true;
//        LayoutInflater.from(context).inflate(R.layout.exo_players, this);
//        this.componentListener = new ComponentListener();
//        AspectRatioFrameLayout aspectRatioFrameLayout = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
//        this.layout = aspectRatioFrameLayout;
//        aspectRatioFrameLayout.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//        this.shutterView = findViewById(R.id.shutter);
//        SubtitleView subtitleView = (SubtitleView) findViewById(R.id.subtitles);
//        this.subtitleLayout = subtitleView;
//        subtitleView.setUserDefaultStyle();
//        subtitleView.setUserDefaultTextSize();
//        CustomPlayBackControllers customPlayBackControllers = (CustomPlayBackControllers) findViewById(R.id.control);
//        this.controller = customPlayBackControllers;
//        customPlayBackControllers.hide();
//        customPlayBackControllers.setRewindIncrementMs(5000);
//        customPlayBackControllers.setFastForwardIncrementMs(15000);
//        this.controllerShowTimeoutMs = 5000;
//        View view = 0 != 0 ? new TextureView(context) : new SurfaceView(context);
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
//        view.setLayoutParams(params);
//        this.surfaceView = view;
//        aspectRatioFrameLayout.addView(view, 0);
//    }
//
//    public ExoPlayer getPlayer() {
//        return this.player;
//    }
//
//    public void setPlayer(ExoPlayer player) {
//        ExoPlayer simpleExoPlayer = this.player;
//        if (simpleExoPlayer == player) {
//            return;
//        }
//        if (simpleExoPlayer != null) {
//            simpleExoPlayer.removeListener(this.componentListener);
////            this.player.removeTextOutput(this.componentListener);
////            this.player.removeVideoListener(this.componentListener);
//            View view = this.surfaceView;
//            if (view instanceof TextureView) {
//                this.player.clearVideoTextureView((TextureView) view);
//            } else if (view instanceof SurfaceView) {
//                this.player.clearVideoSurfaceView((SurfaceView) view);
//            }
//        }
//        this.player = player;
//        if (this.useController) {
//            this.controller.setPlayer(player);
//        }
//        View view2 = this.shutterView;
//        if (view2 != null) {
//            view2.setVisibility(View.VISIBLE);
//        }
//        if (player != null) {
//            View view3 = this.surfaceView;
//            if (view3 instanceof TextureView) {
//                player.setVideoTextureView((TextureView) view3);
//            } else if (view3 instanceof SurfaceView) {
//                player.setVideoSurfaceView((SurfaceView) view3);
//            }
////            player.addVideoListener(this.componentListener);
////            player.addTextOutput(this.componentListener);
//            player.addListener(this.componentListener);
//            maybeShowController(false);
//            return;
//        }
//        this.shutterView.setVisibility(View.VISIBLE);
//        this.controller.hide();
//    }
//
//    public void setResizeMode(int resizeMode) {
//        this.layout.setResizeMode(resizeMode);
//    }
//
//    public boolean getUseController() {
//        return this.useController;
//    }
//
//    public void setUseController(boolean useController) {
//        if (this.useController == useController) {
//            return;
//        }
//        this.useController = useController;
//        if (useController) {
//            this.controller.setPlayer(this.player);
//            return;
//        }
//        this.controller.hide();
//        this.controller.setPlayer(null);
//    }
//
//    public int getControllerShowTimeoutMs() {
//        return this.controllerShowTimeoutMs;
//    }
//
//    public void setControllerShowTimeoutMs(int controllerShowTimeoutMs) {
//        this.controllerShowTimeoutMs = controllerShowTimeoutMs;
//    }
//
//    public void setControllerVisibilityListener(CustomPlayBackControllers.VisibilityListener listener) {
//        this.controller.setVisibilityListener(listener);
//    }
//
//    public void setRewindIncrementMs(int rewindMs) {
//        this.controller.setRewindIncrementMs(rewindMs);
//    }
//
//    public void setFastForwardIncrementMs(int fastForwardMs) {
//        this.controller.setFastForwardIncrementMs(fastForwardMs);
//    }
//
//    public View getVideoSurfaceView() {
//        return this.surfaceView;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (!this.useController || this.player == null || ev.getActionMasked() != 0) {
//            return false;
//        }
//        if (this.controller.isVisible()) {
//            this.controller.hide();
//        } else {
//            maybeShowController(true);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onTrackballEvent(MotionEvent ev) {
//        if (!this.useController || this.player == null) {
//            return false;
//        }
//        maybeShowController(true);
//        return true;
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        return this.useController ? this.controller.dispatchKeyEvent(event) : super.dispatchKeyEvent(event);
//    }
//
//    private boolean shouldShowControllerIndefinitely() {
//        int playbackState;
//        ExoPlayer simpleExoPlayer = this.player;
//        return simpleExoPlayer == null || (playbackState = simpleExoPlayer.getPlaybackState()) == 1 || playbackState == 4 || !this.player.getPlayWhenReady();
//    }
//
//
//    public void maybeShowController(boolean isForced) {
//        if (!this.useController || this.player == null) {
//            return;
//        }
//        boolean wasShowingIndefinitely = this.controller.isVisible() && this.controller.getShowTimeoutMs() <= 0;
//        boolean shouldShowIndefinitely = shouldShowControllerIndefinitely();
//        if (isForced || wasShowingIndefinitely || shouldShowIndefinitely) {
//            this.controller.show();
//        }
//    }
//
//    public void hideController() {
//        this.controller.hide();
//    }
//
//
//    /*private final class ComponentListener extends Player.DefaultEventListener implements TextOutput, SimpleExoPlayer.VideoListener {
//        private ComponentListener() {
//        }
//
//        @Override
//        public void onCues(List<Cue> cues) {
//            if (CustomExoPlayersView.this.subtitleLayout != null) {
//                CustomExoPlayersView.this.subtitleLayout.onCues(cues);
//            }
//        }
//
//        @Override
//        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
//            if (CustomExoPlayersView.this.layout != null) {
//                float aspectRatio = height == 0 ? 1.0f : (width * pixelWidthHeightRatio) / height;
//                CustomExoPlayersView.this.layout.setAspectRatio(aspectRatio);
//            }
//        }
//
//        @Override
//        public void onRenderedFirstFrame() {
//            if (CustomExoPlayersView.this.shutterView != null) {
//                CustomExoPlayersView.this.shutterView.setVisibility(View.INVISIBLE);
//            }
//        }
//
//        @Override
//        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            CustomExoPlayersView.this.maybeShowController(false);
//        }
//    }*/
//
//    private final class ComponentListener implements Player.Listener, TextOutput {
//        @Override
//        public void onCues(@NonNull List<Cue> cues) {
//            if (CustomExoPlayersView.this.subtitleLayout != null) {
//                CustomExoPlayersView.this.subtitleLayout.onCues(cues);
//            }
//        }
//
//        @Override
//        public void onVideoSizeChanged(@NonNull VideoSize videoSize) {
//            if (CustomExoPlayersView.this.layout != null) {
//                float aspectRatio = videoSize.height == 0 ? 1.0f : (float) videoSize.width / videoSize.height;
//                CustomExoPlayersView.this.layout.setAspectRatio(aspectRatio);
//            }
//        }
//
//        @Override
//        public void onRenderedFirstFrame() {
//            if (CustomExoPlayersView.this.shutterView != null) {
//                CustomExoPlayersView.this.shutterView.setVisibility(View.INVISIBLE);
//            }
//        }
//
//        @Override
//        public void onPlaybackStateChanged(int playbackState) {
//            CustomExoPlayersView.this.maybeShowController(false);
//        }
//    }
//}
