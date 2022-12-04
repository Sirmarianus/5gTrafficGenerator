package mk.trafficgenerator5g;

import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubeActivity extends YouTubeBaseActivity {
    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        youTubePlayerView = findViewById(R.id.youtube_player);
        url = String.valueOf(this.getIntent().getData());

        youTubePlayerView.initialize(getString(R.string.GOOGLE_API),
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                Log.d("YoutubeActivity", "onInitializationSuccess");
                player.loadVideo(getYoutubeVideoFromUrl(url));
                player.play();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                Log.d("YoutubeActivity", "onInitializationFailure");
                Log.d("YoutubeActivity", String.valueOf(error));
            }
        });
    }

    private String getYoutubeVideoFromUrl(String url) {
        if (url.contains("youtu.be")) {
            return url.substring(url.lastIndexOf("/")+1);
        }
        else if (url.contains("www.youtube.com")) {
            return url.substring(url.lastIndexOf("watch?v=")+8);
        }
        else {
            return null;
        }
    }
}