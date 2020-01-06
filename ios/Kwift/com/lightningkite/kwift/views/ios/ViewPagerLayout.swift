//
//  ViewPagerLayout.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/12/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

public class ViewPagerLayout: UICollectionViewFlowLayout {
    override public func prepare() {
        self.scrollDirection = .horizontal
        self.sectionInset = UIEdgeInsets(top: 0.0, left: 0.0, bottom: 0.0, right: 0.0)
        if #available(iOS 11.0, *) {
            self.sectionInsetReference = .fromSafeArea
        } else {
            // Fallback on earlier versions
        }
        
        guard let collectionView = collectionView else { return }
        collectionView.addOnLayoutSubviews { [weak self] in
            guard let self = self, let collectionView = self.collectionView else { return }
            let newSize = CGSize(
                width: collectionView.bounds.width,
                height: collectionView.bounds.height
            )
            if newSize != self.itemSize {
                self.itemSize = newSize
                print("Item Size: \(self.itemSize) VS Self Size: \(collectionView.bounds.size) VS Insets \(self.sectionInset) / \(collectionView.contentInset)")
            }
        }
    }
    
    override public func targetContentOffset(forProposedContentOffset proposedContentOffset: CGPoint, withScrollingVelocity velocity: CGPoint) -> CGPoint {

        guard let collectionView = self.collectionView else {
            let latestOffset = super.targetContentOffset(forProposedContentOffset: proposedContentOffset, withScrollingVelocity: velocity)
            return latestOffset
        }

        // Page width used for estimating and calculating paging.
        let pageWidth = self.itemSize.width + self.minimumInteritemSpacing

        // Make an estimation of the current page position.
        let approximatePage = collectionView.contentOffset.x/pageWidth

        // Determine the current page based on velocity.
        let currentPage = velocity.x == 0 ? round(approximatePage) : (velocity.x < 0.0 ? floor(approximatePage) : ceil(approximatePage))

        // Create custom flickVelocity.
        let flickVelocity = velocity.x * 0.3

        // Check how many pages the user flicked, if <= 1 then flickedPages should return 0.
        let flickedPages = (abs(round(flickVelocity)) <= 1) ? 0 : round(flickVelocity)

        // Calculate newHorizontalOffset.
        let newHorizontalOffset = ((currentPage + flickedPages) * pageWidth) - collectionView.contentInset.left

        return CGPoint(x: newHorizontalOffset, y: proposedContentOffset.y)
    }
}
