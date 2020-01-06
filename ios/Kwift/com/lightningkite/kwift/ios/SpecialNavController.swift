//
//  SpecialNavController.swift
//  Alamofire
//
//  Created by Joseph Ivie on 11/20/19.
//

import UIKit

public class SpecialNavController: UINavigationController, UINavigationControllerDelegate {
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        common()
    }
    
    init(){
        super.init(nibName: nil, bundle: nil)
        common()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        common()
    }
    
    func common(){
        delegate = self
    }
    
    public func navigationController(_ navigationController: UINavigationController, willShow viewController: UIViewController, animated: Bool) {
        isNavigationBarHidden = viewControllers.count == 1
    }
    
    public func navigationController(_ navigationController: UINavigationController, didShow viewController: UIViewController, animated: Bool) {
        isNavigationBarHidden = viewControllers.count == 1
    }
}
