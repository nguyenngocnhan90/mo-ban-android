package com.moban.flow.youtube;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.moban.R;

public class YoutubeVideoFragment extends YouTubePlayerFragment
        implements YouTubePlayer.OnInitializedListener {
    public static final String TAG = YoutubeVideoFragment.class.getSimpleName();

    private YouTubePlayer player;
    private String videoId;
    private boolean isInitialed = false;
    private boolean isShowFullscreenButton = true;
    private OnInitializedListener onInitializedListener;
    private YouTubePlayer.OnFullscreenListener onFullscreenListener;
    private YouTubePlayer.PlaybackEventListener playbackEventListener;
    private YouTubePlayer.PlayerStyle playerStyle = YouTubePlayer.PlayerStyle.DEFAULT;
    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            if (errorReason != null) {
                Log.e(TAG, "PlayerStateChange error: " + errorReason.toString());
            }
        }
    };

    public static YoutubeVideoFragment newInstance() {
        return new YoutubeVideoFragment();
    }

    public YoutubeVideoFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize(getString(R.string.youtube_api_key), this);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        // disable view state saving to prevent saving states from youtube apk which cannot be restored.
        View view = getView();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = ((ViewGroup) view);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                viewGroup.getChildAt(i).setSaveFromParentEnabled(false);
            }
        }
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void setVideoId(String videoId) {
        if (videoId != null && !videoId.equals(this.videoId)) {
            this.videoId = videoId;
            if (player != null) {
                player.cueVideo(videoId);
            }
        }
    }

    public void loadVideoId(String videoId) {
        if (videoId != null) {
            this.videoId = videoId;
            if (player != null) {
                player.loadVideo(videoId);
            }
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    public void play() {
        if (player != null) {
            player.play();
        }
    }

    public void setPlayerStyle(YouTubePlayer.PlayerStyle playerStyle) {
        this.playerStyle = playerStyle;
        if (player != null) {
            player.setPlayerStyle(playerStyle);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
        this.player = player;
        this.isInitialed = true;
        player.setShowFullscreenButton(isShowFullscreenButton);
        if (playerStateChangeListener != null) {
            player.setPlayerStateChangeListener(playerStateChangeListener);
        }
        if (playbackEventListener != null) {
            player.setPlaybackEventListener(playbackEventListener);
        }
//        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        if (onFullscreenListener != null) {
            player.setOnFullscreenListener(onFullscreenListener);
        }
        if (!restored && videoId != null) {
            player.loadVideo(videoId);
        }
        player.setPlayerStyle(playerStyle);

        if (onInitializedListener != null) {
            onInitializedListener.onInitializationSuccess(provider, player, restored);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        this.player = null;
        this.isInitialed = true;
        if (onInitializedListener != null) {
            onInitializedListener.onInitializationFailure(provider, result);
        }
    }


    public YouTubePlayer.OnFullscreenListener getOnFullscreenListener() {
        return onFullscreenListener;
    }

    public void setOnFullscreenListener(YouTubePlayer.OnFullscreenListener onFullscreenListener) {
        this.onFullscreenListener = onFullscreenListener;
        if (player != null) {
            player.setOnFullscreenListener(onFullscreenListener);
        }
    }

    public void setPlayerStateChangeListener(YouTubePlayer.PlayerStateChangeListener playerStateChangeListener) {
        this.playerStateChangeListener = playerStateChangeListener;
        if (player != null) {
            player.setPlayerStateChangeListener(playerStateChangeListener);
        }
    }

    public void setOnInitializedListener(OnInitializedListener onInitializedListener) {
        this.onInitializedListener = onInitializedListener;
    }

    public void setPlaybackEventListener(YouTubePlayer.PlaybackEventListener playbackEventListener) {
        this.playbackEventListener = playbackEventListener;
        if (player != null) {
            player.setPlaybackEventListener(playbackEventListener);
        }
    }

    public void setAutoFullScreen(boolean isAutoFullScreen) {
        if (player != null) {
            int controlFlags = player.getFullscreenControlFlags();
            if (isAutoFullScreen) {
                controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
            } else {
                controlFlags &= ~YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
            }
            player.setFullscreenControlFlags(controlFlags);
        }
    }

    public void setShowFullscreenButton(boolean isShow) {
        isShowFullscreenButton = isShow;
        if (player != null) {
            player.setShowFullscreenButton(isShow);
        }
    }

    public boolean isInitialed() {
        return isInitialed;
    }

    public void setIsInitialed(boolean isInitialed) {
        this.isInitialed = isInitialed;
    }

    public int getDurationMillis() {
        if (player != null) {
            return player.getDurationMillis();
        }
        return 0;
    }

    public int getCurrentTimeMillis() {
        if (player != null) {
            return player.getCurrentTimeMillis();
        }
        return 0;
    }

    public void seekToMillis(int millis) {
        if (player != null) {
            player.seekToMillis(millis);
        }
    }

    public boolean isPlaying() {
        if (player != null) {
            return player.isPlaying();
        }
        return false;
    }

    public interface OnInitializedListener {
        void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored);

        void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result);
    }
}
