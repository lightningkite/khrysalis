//
// ExampleContentXml.swift
// Created by Kwift XML
//

import UIKit
import FlexLayout
import PinLayout

class ExampleContentXml {
    
    weak var exampleContentGoToAnotherScreen: UIButton!
    weak var exampleContentNumber: UILabel!
    weak var exampleContentIncrement: UIButton!
    
    func setup(_ dependency: ViewDependency) -> UIView {
        return { () -> UIScrollView in 
            let view = UIScrollView(frame: .zero)
            view.layoutMargins = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
            view.flex.direction(.column).alignContent(.center).addItem({
                let sub = { () -> UIView in 
                    let view = UIView(frame: .zero)
                    view.layoutMargins = UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8)
                    view.flex.direction(.column).padding(8, 8, 8, 8).alignContent(.start).justifyContent(.start).define{ (flex) in 
                        flex.addItem({ () -> UILabel in 
                            let view = UILabel(frame: .zero)
                            view.layoutMargins = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
                            view.text = "Welcome!"
                            view.numberOfLines = 0
                            view.font = UIFont.get(size: 24, style: ["bold"])
                            view.textColor = UIColor(argb: 0xFF000000)
                            return view
                        }()
                        ).margin(8, 8, 8, 8).alignSelf(.start)
                        
                        flex.addItem({ () -> UILabel in 
                            let view = UILabel(frame: .zero)
                            view.layoutMargins = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
                            view.text = "This is the template for a Kwift project. This screen exists to help you figure out stuff about it."
                            view.numberOfLines = 0
                            view.font = UIFont.get(size: 16, style: [])
                            view.textColor = UIColor(argb: 0xFF222222)
                            return view
                        }()
                        ).margin(8, 8, 8, 8).alignSelf(.start)
                        
                        flex.addItem({ () -> UIView in 
                            let view = UIView(frame: .zero)
                            view.layoutMargins = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
                            view.flex.direction(.row).padding(0, 0, 0, 0).alignContent(.center).justifyContent(.center).define{ (flex) in 
                                flex.addItem({ () -> UILabel in 
                                    let view = UILabel(frame: .zero)
                                    self.exampleContentNumber = view
                                    view.layoutMargins = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
                                    view.text = "0"
                                    view.numberOfLines = 0
                                    view.font = UIFont.get(size: 16, style: [])
                                    view.textColor = UIColor(argb: 0xFF222222)
                                    return view
                                }()
                                ).margin(8, 8, 8, 8).grow(1).shrink(1).width(0).alignSelf(.center)
                                
                                flex.addItem({ () -> UIButton in 
                                    let view = UIButton(frame: .zero)
                                    self.exampleContentIncrement = view
                                    if let image = UIImage(named: "button_primary") {
                                        view.backgroundColor = UIColor(patternImage: image)
                                    } else {
                                        ResourcesBackground.apply(view, "button_primary")
                                    }
                                    view.layoutMargins = UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8)
                                    view.setTitle("Increment the Number", for: .normal)
                                    view.setTitleColor(UIColor(argb: 0xFFFFFFFF), for: .normal)
                                    view.contentHorizontalAlignment = .center
                                    view.contentMode = .scaleAspectFit
                                    view.contentEdgeInsets = UIEdgeInsets(top: 8, left:8, bottom:8, right:8)
                                    view.titleLabel?.text = "Increment the Number"
                                    view.titleLabel?.numberOfLines = 0
                                    view.titleLabel?.font = UIFont.get(size: 16, style: [])
                                    view.titleLabel?.textColor = UIColor(argb: 0xFFFFFFFF)
                                    return view
                                }()
                                ).margin(8, 8, 8, 8).alignSelf(.center)
                                
                            }
                            return view
                        }()
                        ).margin(0, 0, 0, 0).alignSelf(.stretch)
                        
                        flex.addItem({ () -> UIButton in 
                            let view = UIButton(frame: .zero)
                            self.exampleContentGoToAnotherScreen = view
                            if let image = UIImage(named: "button_primary") {
                                view.backgroundColor = UIColor(patternImage: image)
                            } else {
                                ResourcesBackground.apply(view, "button_primary")
                            }
                            view.layoutMargins = UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8)
                            view.setTitle("Go to Another Screen", for: .normal)
                            view.setTitleColor(UIColor(argb: 0xFFFFFFFF), for: .normal)
                            view.contentHorizontalAlignment = .center
                            view.contentMode = .scaleAspectFit
                            view.contentEdgeInsets = UIEdgeInsets(top: 8, left:8, bottom:8, right:8)
                            view.titleLabel?.text = "Go to Another Screen"
                            view.titleLabel?.numberOfLines = 0
                            view.titleLabel?.font = UIFont.get(size: 16, style: [])
                            view.titleLabel?.textColor = UIColor(argb: 0xFFFFFFFF)
                            return view
                        }()
                        ).margin(8, 8, 8, 8).alignSelf(.start)
                        
                    }
                    return view
                }()
                
                let dg = ScrollSavingDelegate()
                view.delegate = dg
                view.addOnLayoutSubviews { [weak view, weak sub] in
                    if let view = view, let sub = sub {
                        view.contentSize = sub.frame.size
                        view.contentOffset = dg.lastNonzeroOffset
                    }
                }
                
                return sub
            }()
            ).shrink(0)
            return view
        }()
        
    }
}