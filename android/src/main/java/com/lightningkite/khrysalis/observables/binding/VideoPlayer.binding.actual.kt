package com.lightningkite.khrysalis.observables.binding

import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.observables.ObservableProperty
import com.lightningkite.khrysalis.observables.observableNN
import com.lightningkite.khrysalis.observables.subscribeBy
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.VideoPlayer
import com.lightningkite.khrysalis.views.onClick

fun VideoPlayer.bind(video: ObservableProperty<Video?>){
    bindVideoToView(this, video)
}

fun PlayerView.bind(video: ObservableProperty<Video?>) {
    bindVideoToView(this, video)
}

fun bindVideoToView(view:PlayerView, video: ObservableProperty<Video?>){
    val player: SimpleExoPlayer = SimpleExoPlayer.Builder(view.context).build()
    view.player = player
    video.observableNN.doOnDispose { player.release() }.subscribe { video ->
        when (video) {
            is VideoReference -> {
                val agent = Util.getUserAgent(view.context, view.context.getString(R.string.app_name))
                val factory = DefaultDataSourceFactory(view.context, agent)
                val source = ProgressiveMediaSource.Factory(factory).createMediaSource(video.uri)
                player.prepare(source)
            }
            is VideoRemoteUrl -> {
                val agent = Util.getUserAgent(view.context, view.context.getString(R.string.app_name))
                val factory = DefaultDataSourceFactory(view.context, agent)
                val source = ProgressiveMediaSource.Factory(factory).createMediaSource(Uri.parse(video.url))
                player.prepare(source)
            }
            else -> {
            }
        }
    }.until(view.removed)
}

fun VideoPlayer.bindAndStart(video: ObservableProperty<Video?>){
    bindVideoToViewAndStart(this, video)
}

fun PlayerView.bindAndStart(video: ObservableProperty<Video?>) {
    bindVideoToViewAndStart(this, video)
}

fun bindVideoToViewAndStart(view:PlayerView, video:ObservableProperty<Video?>){
    val player: SimpleExoPlayer = SimpleExoPlayer.Builder(view.context).build()
    view.player = player
    video.observableNN.doOnDispose { player.release() }.subscribe { video ->
        when (video) {
            is VideoReference -> {
                val agent = Util.getUserAgent(view.context, view.context.getString(R.string.app_name))
                val factory = DefaultDataSourceFactory(view.context, agent)
                val source = ProgressiveMediaSource.Factory(factory).createMediaSource(video.uri)
                player.playWhenReady = true
                player.prepare(source)
            }
            is VideoRemoteUrl -> {
                val agent = Util.getUserAgent(view.context, view.context.getString(R.string.app_name))
                val factory = DefaultDataSourceFactory(view.context, agent)
                val source = ProgressiveMediaSource.Factory(factory).createMediaSource(Uri.parse(video.url))
                player.playWhenReady = true
                player.prepare(source)
            }
            else -> {
            }
        }
    }.until(view.removed)
}