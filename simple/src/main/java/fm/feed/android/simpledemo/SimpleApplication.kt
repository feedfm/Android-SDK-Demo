package fm.feed.android.simpledemo

import android.app.Application
import fm.feed.android.playersdk.FeedAudioPlayer
import fm.feed.android.playersdk.FeedPlayerService

/**
 * Configures the Feed.fm player with the demo credentials on launch:
 * hardcoded `demo`/`demo`, with a fresh client id per install.
 */
class SimpleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val builder = FeedAudioPlayer.Builder(applicationContext, "demo", "demo")
        builder.setCreateNewClientId(true)
        FeedPlayerService.initialize(builder)
    }
}
