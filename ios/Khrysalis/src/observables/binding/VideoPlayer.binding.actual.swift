import UIKit
import AVKit
import AVFoundation
import RxSwift

public extension UIVideoView {
    func bind(video: ObservableProperty<Video?>) {
        video.subscribeBy { v in
            if let v = v {
                self.setVideo(video: v)
            } else {
                self.clearVideo()
            }
        }.until(self.removed)
    }
    func bindAndStart(video: ObservableProperty<Video?>) {
        video.subscribeBy { v in
            if let v = v {
                self.setVideo(video: v)
                self.controller.player?.play()
            } else {
                self.clearVideo()
            }
        }.until(self.removed)
    }
}
