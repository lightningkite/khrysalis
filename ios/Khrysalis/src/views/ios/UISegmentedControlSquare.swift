
import UIKit

public class UISegmentedControlSquare: UISegmentedControl {
    override public init(frame: CGRect) {
        super.init(frame: frame)
        startup()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        startup()
    }
    
    private func startup(){
        
    }
    
    public override func layoutSubviews() {
        super.layoutSubviews()
        layer.cornerRadius = 0
    }
    public var desiredHeight: CGFloat = 32
    public override func sizeThatFits(_ size: CGSize) -> CGSize {
        return CGSize(width: size.width, height: desiredHeight)
    }
    
    public var reselectable: Bool = false
    
    public override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        let previousSelectedSegmentIndex = self.selectedSegmentIndex

        super.touchesEnded(touches, with: event)

        if reselectable, previousSelectedSegmentIndex == self.selectedSegmentIndex {
            let touch = touches.first!
            let touchLocation = touch.location(in: self)
            if bounds.contains(touchLocation) {
                self.sendActions(for: .valueChanged)
            }
        }
    }
}
