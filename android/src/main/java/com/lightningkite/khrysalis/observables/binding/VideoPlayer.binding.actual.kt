package com.lightningkite.khrysalis.observables.binding

import com.lightningkite.khrysalis.Uri
import com.lightningkite.khrysalis.Video
import com.lightningkite.khrysalis.VideoReference
import com.lightningkite.khrysalis.VideoRemoteUrl
import com.lightningkite.khrysalis.observables.ObservableProperty
import com.lightningkite.khrysalis.observables.subscribeBy
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.VideoPlayer
import com.lightningkite.khrysalis.views.onClick

fun VideoPlayer.bind(video: ObservableProperty<Video?>) {
    video.subscribeBy { video ->
        when (video) {
            is VideoReference -> {
                this.videoView.setVideoURI(video.uri)
            }
            is VideoRemoteUrl -> {
                val videoUrl = Uri.parse(video.url)
                this.videoView.setVideoURI(videoUrl)
            }
            else -> {
                this.videoView.setVideoURI(null)
            }
        }
    }.until(removed)
    this.onClick{}
}

fun VideoPlayer.bindAndStart(video: ObservableProperty<Video?>) {
    video.subscribeBy { video ->
        when (video) {
            is VideoReference -> {
                this.videoView.setVideoURI(video.uri)
                this.videoView.start()
            }
            is VideoRemoteUrl -> {
                val videoUrl = Uri.parse(video.url)
                this.videoView.setVideoURI(videoUrl)
                this.videoView.start()
            }
            else -> {
                this.videoView.setVideoURI(null)
            }
        }
    }.until(removed)
    this.onClick{}
}