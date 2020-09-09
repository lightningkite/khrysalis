
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
}
