import UIKit
import AVKit
import AVFoundation

public class UIVideoView: UIView {

    public let controller = AVPlayerViewController()

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setup()
    }

    override public init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    private func setup(){
        self.addSubview(controller.view)
    }

    override public func layoutSubviews() {
        super.layoutSubviews()
        controller.view.frame = self.bounds
    }
    
    public func clearVideo() {
        self.controller.player = nil
    }
    
    public func setVideo(video: Video){
        var player: AVPlayer;
        switch video {
        case let video as VideoReference:
            player = AVPlayer(url: video.uri)
        case let video as VideoRemoteUrl:
            guard let url = URL(string: video.url) else { return }
            player = AVPlayer(url: url)
        default:
            return
        }
        self.controller.player = player
    }
}
