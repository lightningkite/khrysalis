//
//  UITableView+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


extension UITableView {
    func bindRefresh(_ loading: ObservableProperty<Bool>, _ onRefresh: @escaping () -> Void) {
        return bindRefresh(loading: loading, onRefresh: onRefresh)
    }
    func bindRefresh(loading: ObservableProperty<Bool>, onRefresh: @escaping () -> Void) {
        let control = UIRefreshControl()
        control.addAction(for: .valueChanged, action: onRefresh)
        if #available(iOS 10.0, *) {
            refreshControl = control
        } else {
            addSubview(control)
        }
        loading.addAndRunWeak(referenceA: control) { (this, value) in
            if value {
                this.beginRefreshing()
            } else {
                this.endRefreshing()
            }
        }
    }
    
    func whenScrolledToEnd(action: @escaping ()->Void) {
        if let delegate = delegate as? HasAtEnd {
            delegate.setAtEnd(action: action)
        }
    }
    
    func bind<T>(
        data: ObservableProperty<[T]>,
        defaultValue: T,
        spacing: CGFloat = 8,
        makeView: @escaping (ObservableProperty<T>) -> UIView
    ) {
        register(CustomUITableViewCell.self, forCellReuseIdentifier: "main-cell")
        let boundDataSource = BoundDataSource(source: data, defaultValue: defaultValue, spacing: spacing, makeView: makeView)
        dataSource = boundDataSource
        delegate = boundDataSource
        retain(as: "boundDataSource", item: boundDataSource)
        
        self.separatorStyle = .none
        self.rowHeight = UITableView.automaticDimension
        
        var previouslyEmpty = data.value.isEmpty
        data.addAndRunWeak(self) { this, value in
            let emptyNow = data.value.isEmpty
            this.reloadData()
            if previouslyEmpty && !emptyNow {
                this.scrollToRow(at: IndexPath(item: 0, section: 0), at: .top, animated: true)
            }
            previouslyEmpty = emptyNow
        }
    }
}

class CustomUITableViewCell: UITableViewCell {
    var obs: Any?
    var spacing: CGFloat = 0
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.flex
            .direction(.column)
            .alignItems(.stretch)
            .alignContent(.stretch)
            .padding(spacing)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        contentView.flex.layout(mode: .adjustHeight)
    }

    
    override func sizeThatFits(_ size: CGSize) -> CGSize {
        contentView.pin.width(size.width)
        contentView.flex.layout(mode: .adjustHeight)
        return contentView.frame.size
    }
}

protocol HasAtEnd {
    var atEnd: () -> Void { get set }
    func setAtEnd(action: @escaping () -> Void)
}

class BoundDataSource<T, VIEW: UIView>: NSObject, UITableViewDataSource, UITableViewDelegate, HasAtEnd {
    
    var source: ObservableProperty<[T]>
    let makeView: (ObservableProperty<T>) -> UIView
    let defaultValue: T
    var atEnd: () -> Void = {}
    let spacing: CGFloat
    
    init(source: ObservableProperty<[T]>, defaultValue: T, spacing: CGFloat, makeView: @escaping (ObservableProperty<T>) -> UIView) {
        self.source = source
        self.spacing = spacing
        self.makeView = makeView
        self.defaultValue = defaultValue
        super.init()
    }
    
    func setAtEnd(action: @escaping () -> Void) {
        self.atEnd = action
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        let value = self.source.value
        let count = value.count
        return count
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if indexPath.row >= (source.value.count) - 1 {
            atEnd()
        }
    }
    
    //    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
    //        if let s = source?.value {
    //            updateCell(stubView, s[indexPath.row])
    //            return stubView.flex.sizeThatFits(size: CGSize(width: tableView.frame.width, height: 100000)).height
    //        } else {
    //            return 50
    //        }
    //    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let s = source.value
        var cell: CustomUITableViewCell = tableView.dequeueReusableCell(withIdentifier: "main-cell") as! CustomUITableViewCell
        cell.spacing = self.spacing
        cell.selectionStyle = .none
        if cell.obs == nil {
            var obs = StandardObservableProperty(defaultValue)
            cell.obs = obs
            let new = makeView(obs)
            cell.contentView.flex.addItem(new)
        }
        if let obs = cell.obs as? StandardObservableProperty<T> {
            obs.value = s[indexPath.row]
        }
        return cell
    }
    
}
