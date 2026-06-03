package fm.feed.android.simpledemo

import android.app.Application
import fm.feed.android.playersdk.FeedAudioPlayer
import fm.feed.android.playersdk.FeedPlayerService

/**
 * Configures the Feed.fm player with the demo credentials on launch:
 * hardcoded `demo`/`demo`.
 */
class SimpleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val builder = FeedAudioPlayer.Builder(applicationContext, "demo", "demo")
        FeedPlayerService.initialize(builder)
    }
}
