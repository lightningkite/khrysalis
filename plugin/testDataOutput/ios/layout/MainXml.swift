//
// MainXml.swift
// Created by Kwift XML
//

import UIKit
import FlexLayout
import PinLayout

class MainXml {
    
    weak var mainBack: UIButton!
    weak var mainContent: UIView!
    weak var title: UILabel!
    
    func setup(_ dependency: ViewDependency) -> UIView {
        return { () -> UIView in 
            let view = UIView(frame: .zero)
            view.backgroundColor = ResourcesColors.white
            view.layoutMargins = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
            view.flex.direction(.column).padding(0, 0, 0, 0).alignContent(.start).justifyContent(.start).define{ (flex) in 
                flex.addItem({ () -> UIView in 
                    let view = UIView(frame: .zero)
                    view.backgroundColor = ResourcesColors.colorPrimary
                    view.layoutMargins = UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8)
                    view.flex.direction(.row).padding(8, 8, 8, 8).alignContent(.center).justifyContent(.center).define{ (flex) in 
                        flex.addItem({ () -> UIButton in 
                            let view = UIButton(frame: .zero)
                            self.mainBack = view
                            view.layoutMargins = UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8)
                            view.contentHorizontalAlignment = .center
                            view.setImage(UIImage(named: "ic_arrow_back_white_24dp"), for: .normal)
                            view.contentMode = .scaleAspectFit
                            view.contentEdgeInsets = UIEdgeInsets(top: 8, left:8, bottom:8, right:8)
                            view.titleLabel?.numberOfLines = 0
                            view.titleLabel?.font = UIFont.get(size: 12, style: [])
                            return view
                        }()
                        ).margin(0, 0, 0, 0).alignSelf(.center)
                        
                        flex.addItem({ () -> UILabel in 
                            let view = UILabel(frame: .zero)
                            self.title = view
                            view.layoutMargins = UIEdgeInsets(top: 4, left: 4, bottom: 4, right: 4)
                            view.text = ResourcesStrings.appName
                            view.numberOfLines = 0
                            view.font = UIFont.get(size: 18, style: ["bold"])
                            view.textColor = UIColor(argb: 0xFFffffff)
                            return view
                        }()
                        ).margin(0, 0, 0, 0).grow(1).shrink(1).width(0).alignSelf(.center)
                        
                    }
                    return view
                }()
                ).margin(0, 0, 0, 0).alignSelf(.stretch)
                
                flex.addItem({ () -> UIView in 
                    let view = UIView(frame: .zero)
                    self.mainContent = view
                    view.layoutMargins = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
                    return view
                }()
                ).margin(0, 0, 0, 0).grow(1).shrink(1).height(0).alignSelf(.stretch)
                
            }
            return view
        }()
        
    }
}