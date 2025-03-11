package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.PictureInPictureParams;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.InputDeviceCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterVideoPlayerIcons;
import com.gallaryapp.privacyvault.photoeditor.Ads.DebounceClickListener;
import com.gallaryapp.privacyvault.photoeditor.Model.IconModel;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.DialogBrightness;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.DialogVolume;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.OnSwipeTouchListener;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.gallaryapp.privacyvault.photoeditor.databinding.ActVideoViewBinding;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;

public class ActVideoView extends AppCompatActivity {

    private ActVideoViewBinding binding;

    TextView videoNameTxt;
    RecyclerView optionRv;
    AppCompatImageView lockScreenImg;
    ImageView scaling;
    RelativeLayout root;
    TextView vol_text;
    TextView brt_text;
    ProgressBar vol_progress;
    ProgressBar brt_progress;
    LinearLayout vol_progress_container;
    LinearLayout brt_progress_container;
    LinearLayout vol_text_container;
    LinearLayout brt_text_container;
    ImageView vol_icon;
    ImageView brt_icon;
    RelativeLayout zoomLayout;
    TextView zoom_perc;
    RelativeLayout double_tap_playpause;

    SimpleExoPlayer player;
    AudioManager audioManager;
    private float baseX;
    private float baseY;
    private int brightness;
    ConcatenatingMediaSource concatenatingMediaSource;
    private ContentResolver contentResolver;
    private int deviceHeight;
    private int deviceWidth;
    private long diffX;
    private long diffY;
    boolean isCrossChecked;
    boolean left;
    private int media_volume;
    PlaybackParameters parameters;
    PictureInPictureParams.Builder pictureInPicture;
    AdapterVideoPlayerIcons adapterVideoPlayerIcons;
    boolean right;
    ScaleGestureDetector scaleGestureDetector;
    float speed;
    String videoPath;
    RelativeLayout zoomContainer;
    private Window window;
    boolean dark = false;
    boolean mute = false;
    boolean start = false;
    boolean swipe_move = false;
    boolean success = false;
    boolean singleTap = false;
    boolean double_tap = false;
    private final ArrayList<IconModel> iconList = new ArrayList<>();
    private float scale_factor = 1.0f;

    View.OnClickListener firstListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            player.setVideoScalingMode(1);
            scaling.setImageResource(R.drawable.icon_fit_screen);
            scaling.setOnClickListener(secondListener);
        }
    };

    View.OnClickListener secondListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            player.setVideoScalingMode(1);
            scaling.setImageResource(R.drawable.icon_fit_screen);
            scaling.setOnClickListener(thirdListener);
        }
    };

    View.OnClickListener thirdListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            player.setVideoScalingMode(1);
            scaling.setImageResource(R.drawable.icon_fit_screen);
            scaling.setOnClickListener(firstListener);
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setFullScreen();
        binding = ActVideoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        hideBottomBar();

        Intent intent = getIntent();
        if (intent != null) {
            videoPath = intent.getStringExtra("videoPath");

            initViews();
            playVideo();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            deviceWidth = displayMetrics.widthPixels;
            deviceHeight = displayMetrics.heightPixels;

            binding.playerView.setOnTouchListener(new OnSwipeTouchListener(this) {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        binding.playerView.showController();
                        start = true;
                        if (motionEvent.getX() >= deviceWidth / 2) {
                            if (motionEvent.getX() > deviceWidth / 2) {
                                left = false;
                                right = true;
                            }
                        } else {
                            left = true;
                            right = false;
                        }
                        baseX = motionEvent.getX();
                        baseY = motionEvent.getY();
                    } else if (action == 1) {
                        swipe_move = false;
                        start = false;
                        vol_progress_container.setVisibility(View.GONE);
                        brt_progress_container.setVisibility(View.GONE);
                        vol_text_container.setVisibility(View.GONE);
                        brt_text_container.setVisibility(View.GONE);
                    } else if (action == 2) {
                        swipe_move = true;
                        diffX = (long) Math.ceil(motionEvent.getX() - baseX);
                        diffY = (long) Math.ceil(motionEvent.getY() - baseY);
                        if (Math.abs(diffY) > 100) {
                            start = true;
                            if (Math.abs(diffY) > Math.abs(diffX)) {
                                if (Settings.System.canWrite(getApplicationContext())) {
                                    if (left) {
                                        contentResolver = getContentResolver();
                                        window = getWindow();
                                        try {
                                            Settings.System.putInt(contentResolver, "screen_brightness_mode", 0);
                                            brightness = Settings.System.getInt(contentResolver, "screen_brightness");
                                        } catch (Settings.SettingNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        int i2 = (int) (brightness - (diffY * 0.01d));
                                        if (i2 > 250) {
                                            i2 = 250;
                                        } else if (i2 < 1) {
                                            i2 = 1;
                                        }
                                        double ceil = Math.ceil((i2 / 250.0d) * 100.0d);
                                        brt_progress_container.setVisibility(View.VISIBLE);
                                        brt_text_container.setVisibility(View.VISIBLE);
                                        int i3 = (int) ceil;
                                        brt_progress.setProgress(i3);
                                        if (ceil < 30.0d) {
                                            brt_icon.setImageResource(R.drawable.ic_brightness_low);
                                        } else if (ceil > 30.0d && ceil < 80.0d) {
                                            brt_icon.setImageResource(R.drawable.ic_brightness_moderate);
                                        } else if (ceil > 80.0d) {
                                            brt_icon.setImageResource(R.drawable.ic_brightness);
                                        }
                                        brt_text.setText(" " + i3 + "%");
                                        Settings.System.putInt(contentResolver, "screen_brightness", i2);
                                        WindowManager.LayoutParams attributes = window.getAttributes();
                                        attributes.screenBrightness = brightness / 255.0f;
                                        window.setAttributes(attributes);
                                    } else if (right) {
                                        vol_text_container.setVisibility(View.VISIBLE);
                                        media_volume = audioManager.getStreamVolume(3);
                                        int streamMaxVolume = audioManager.getStreamMaxVolume(3);
                                        double d = streamMaxVolume;
                                        int i4 = media_volume - ((int) (diffY * (d / ((deviceHeight * 2) - 0.01d))));
                                        if (i4 <= streamMaxVolume) {
                                            streamMaxVolume = i4 < 1 ? 0 : i4;
                                        }
                                        audioManager.setStreamVolume(3, streamMaxVolume, AudioManager.FLAG_SHOW_UI);
                                        double ceil2 = Math.ceil((streamMaxVolume / d) * 100.0d);
                                        int i5 = (int) ceil2;
                                        vol_text.setText(" " + i5 + "%");
                                        if (ceil2 < 1.0d) {
                                            vol_icon.setImageResource(R.drawable.ic_volume_off);
                                            vol_text.setVisibility(View.VISIBLE);
                                            vol_text.setText("Off");
                                        } else if (ceil2 >= 1.0d) {
                                            vol_icon.setImageResource(R.drawable.ic_volume);
                                            vol_text.setVisibility(View.VISIBLE);
                                            vol_progress_container.setVisibility(View.VISIBLE);
                                            vol_progress.setProgress(i5);
                                        }
                                        vol_progress_container.setVisibility(View.VISIBLE);
                                        vol_progress.setProgress(i5);
                                    }
                                    success = true;
                                } else {
                                    Toast.makeText(getApplicationContext(), "Allow write settings for swipe controls", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivityForResult(intent, 111);
                                }
                            }
                        }
                    }
                    scaleGestureDetector.onTouchEvent(motionEvent);
                    return super.onTouch(view, motionEvent);
                }

                @Override
                public void onDoubleTouch() {
                    super.onDoubleTouch();
                    if (double_tap) {
                        player.setPlayWhenReady(true);
                        double_tap_playpause.setVisibility(View.GONE);
                        double_tap = false;
                        return;
                    }
                    player.setPlayWhenReady(false);
                    double_tap_playpause.setVisibility(View.VISIBLE);
                    double_tap = true;
                }

                @Override
                public void onSingleTouch() {
                    super.onSingleTouch();
                    if (singleTap) {
                        binding.playerView.showController();
                        singleTap = false;
                    } else {
                        binding.playerView.hideController();
                        singleTap = true;
                    }
                    if (double_tap_playpause.getVisibility() == View.VISIBLE) {
                        double_tap_playpause.setVisibility(View.GONE);
                    }
                }
            });
            horizontalIconList();
            setUpButtonClick();
            handleBackPressBehavior();
        } else {
            getOnBackPressedDispatcher().onBackPressed();
        }
    }

    private void horizontalIconList() {
        iconList.add(new IconModel(R.drawable.ic_night_mode, "Night"));
        iconList.add(new IconModel(R.drawable.ic_rotate, "Rotate"));
        iconList.add(new IconModel(R.drawable.ic_volume_off, "Mute"));
        iconList.add(new IconModel(R.drawable.ic_volume, "Volume"));
        iconList.add(new IconModel(R.drawable.ic_brightness, "Brightness"));
        iconList.add(new IconModel(R.drawable.ic_speed, "Speed"));

        adapterVideoPlayerIcons = new AdapterVideoPlayerIcons(iconList, this);
        optionRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        optionRv.setAdapter(this.adapterVideoPlayerIcons);

        adapterVideoPlayerIcons.setOnItemClickListener(i -> {
            if (i == 0) {
                if (dark) {
                    binding.nightModeView.setVisibility(View.GONE);
                    iconList.set(i, new IconModel(R.drawable.ic_night_mode, "Night"));
                    adapterVideoPlayerIcons.notifyDataSetChanged();
                    dark = false;
                } else {
                    binding.nightModeView.setVisibility(View.VISIBLE);
                    iconList.set(i, new IconModel(R.drawable.ic_night_mode, "Day"));
                    adapterVideoPlayerIcons.notifyDataSetChanged();
                    dark = true;
                }
            }
            if (i == 1) {
                if (getResources().getConfiguration().orientation == 1) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    adapterVideoPlayerIcons.notifyDataSetChanged();
                } else if (getResources().getConfiguration().orientation == 2) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    adapterVideoPlayerIcons.notifyDataSetChanged();
                }
            }
            if (i == 2) {
                if (mute) {
                    player.setVolume(1.0f);
                    iconList.set(i, new IconModel(R.drawable.ic_volume_off, "Mute"));
                    adapterVideoPlayerIcons.notifyDataSetChanged();
                    mute = false;
                } else {
                    player.setVolume(0.0f);
                    iconList.set(i, new IconModel(R.drawable.ic_volume, "UnMute"));
                    adapterVideoPlayerIcons.notifyDataSetChanged();
                    mute = true;
                }
            }
            if (i == 3) {
                new DialogVolume().show(getSupportFragmentManager(), "dialog");
                adapterVideoPlayerIcons.notifyDataSetChanged();
            }
            if (i == 4) {
                new DialogBrightness().show(getSupportFragmentManager(), "dialog");
                adapterVideoPlayerIcons.notifyDataSetChanged();
            }
            if (i == 5) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActVideoView.this);
                builder.setTitle("Select Playback Speed").setPositiveButton("OK", (DialogInterface.OnClickListener) null);
                builder.setSingleChoiceItems(new String[]{"0.5x", "1x Normal Speed", "1.25x", "1.5x", "2x"}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        if (i2 == 0) {
                            speed = 0.5f;
                            parameters = new PlaybackParameters(speed);
                            player.setPlaybackParameters(parameters);
                        } else if (i2 == 1) {
                            speed = 1.0f;
                            parameters = new PlaybackParameters(speed);
                            player.setPlaybackParameters(parameters);
                        } else if (i2 == 2) {
                            speed = 1.25f;
                            parameters = new PlaybackParameters(speed);
                            player.setPlaybackParameters(parameters);
                        } else if (i2 == 3) {
                            speed = 1.5f;
                            parameters = new PlaybackParameters(speed);
                            player.setPlaybackParameters(parameters);
                        } else if (i2 != 4) {
                        } else {
                            speed = 2.0f;
                            parameters = new PlaybackParameters(speed);
                            player.setPlaybackParameters(parameters);
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    private void initViews() {
        videoNameTxt = findViewById(R.id.videoNameTxt);
        optionRv = findViewById(R.id.optionRv);
        lockScreenImg = findViewById(R.id.lockScreenImg);
        scaling = findViewById(R.id.scaling);
        root = findViewById(R.id.root_layout);
        vol_text = findViewById(R.id.vol_text);
        brt_text = findViewById(R.id.brt_text);
        vol_progress = findViewById(R.id.vol_progress);
        brt_progress = findViewById(R.id.brt_progress);
        vol_progress_container = findViewById(R.id.vol_progress_container);
        brt_progress_container = findViewById(R.id.brt_progress_container);
        vol_text_container = findViewById(R.id.vol_text_container);
        brt_text_container = findViewById(R.id.brt_text_container);
        vol_icon = findViewById(R.id.vol_icon);
        brt_icon = findViewById(R.id.brt_icon);
        zoomLayout = findViewById(R.id.zoom_layout);
        zoom_perc = findViewById(R.id.zoom_percentage);
        zoomContainer = findViewById(R.id.zoom_container);
        double_tap_playpause = findViewById(R.id.double_tap_play_pause);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleDetector());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        videoNameTxt.setText(new File(videoPath).getName());
        scaling.setOnClickListener(this.firstListener);
        if (Build.VERSION.SDK_INT >= 26) {
            this.pictureInPicture = new PictureInPictureParams.Builder();
        }
    }

    private void playVideo() {
        this.player = new SimpleExoPlayer.Builder(this).build();
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "app"));
        this.concatenatingMediaSource = new ConcatenatingMediaSource(new MediaSource[0]);
        this.concatenatingMediaSource.addMediaSource(new ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoPath)));
        binding.playerView.setPlayer(this.player);
        binding.playerView.setKeepScreenOn(true);
        this.player.setPlaybackParameters(this.parameters);
        this.player.prepare(this.concatenatingMediaSource);
        this.player.seekTo(0);
    }

    private void setUpButtonClick() {
        findViewById(R.id.lockUnlockImg).setOnClickListener(new DebounceClickListener() {
            @Override
            public void performClick(@Nullable View view) {
                root.setVisibility(View.INVISIBLE);
                lockScreenImg.setVisibility(View.VISIBLE);
            }
        });

        lockScreenImg.setOnClickListener(new DebounceClickListener() {
            @Override
            public void performClick(@Nullable View view) {
                root.setVisibility(View.VISIBLE);
                lockScreenImg.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.player.setPlayWhenReady(false);
        this.player.getPlaybackState();
        if (isInPictureInPictureMode()) {
            this.player.setPlayWhenReady(true);
            return;
        }
        this.player.setPlayWhenReady(false);
        this.player.getPlaybackState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.player.setPlayWhenReady(true);
        this.player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.player.setPlayWhenReady(true);
        this.player.getPlaybackState();
    }

    private void setFullScreen() {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
    }

    public void hideBottomBar() {
        getWindow().getDecorView().setSystemUiVisibility(InputDeviceCompat.SOURCE_TOUCHSCREEN);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean z, @NonNull Configuration configuration) {
        if (Build.VERSION.SDK_INT >= 26) {
            super.onPictureInPictureModeChanged(z, configuration);
        }
        this.isCrossChecked = z;
        if (z) {
            binding.playerView.hideController();
        } else {
            binding.playerView.showController();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isCrossChecked) {
            if (player != null) {
                player.release();
            }
            finish();
        }
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 111) {
            if (Settings.System.canWrite(getApplicationContext())) {
                this.success = true;
            } else {
                Toast.makeText(getApplicationContext(), "Not Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ScaleDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            scale_factor *= scaleGestureDetector.getScaleFactor();
            scale_factor = Math.max(0.5f, Math.min(scale_factor, 6.0f));

            zoomLayout.setScaleX(scale_factor);
            zoomLayout.setScaleY(scale_factor);

            updateZoomDisplay((int) (scale_factor * 100.0f));

            hideOtherUIContainers();
            return true;
        }

        @Override
        public void onScaleEnd(@NonNull ScaleGestureDetector scaleGestureDetector) {
            zoomContainer.setVisibility(View.GONE);
            super.onScaleEnd(scaleGestureDetector);
        }

        private void updateZoomDisplay(int zoomPercentage) {
            zoom_perc.setText(String.format(" %d%%", zoomPercentage));
            zoomContainer.setVisibility(View.VISIBLE);
        }

        private void hideOtherUIContainers() {
            brt_text_container.setVisibility(View.GONE);
            vol_text_container.setVisibility(View.GONE);
            brt_progress_container.setVisibility(View.GONE);
            vol_progress_container.setVisibility(View.GONE);
        }
    }

    private void handleBackPressBehavior() {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (player != null) {
                    player.release();
                }
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        findViewById(R.id.back_img).setOnClickListener(new DebounceClickListener() {
            @Override
            public void performClick(@Nullable View view) {
                onBackPressedCallback.handleOnBackPressed();
            }
        });
    }
}
