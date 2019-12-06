//
//  UITableView+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public extension UITableView {
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
        _ data: ObservableProperty<[T]>,
        _ defaultValue: T,
        _ spacing: CGFloat = 0,
        _ makeView: @escaping (ObservableProperty<T>) -> UIView
    ) {
        bind(data: data, defaultValue: defaultValue, spacing: spacing, makeView: makeView)
    }
    func bind<T>(
        data: ObservableProperty<[T]>,
        defaultValue: T,
        spacing: CGFloat = 0,
        makeView: @escaping (ObservableProperty<T>) -> UIView
    ) {
        register(CustomUITableViewCell.self, forCellReuseIdentifier: "main-cell")
        let boundDataSource = BoundDataSource(source: data, defaultValue: defaultValue, spacing: spacing, makeView: makeView)
        dataSource = boundDataSource
        delegate = boundDataSource
        retain(as: "boundDataSource", item: boundDataSource)

        self.backgroundColor = UIColor.clear
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

    func bindMulti(
        viewDependency: ViewDependency,
        data: ObservableProperty<Array<Any>>,
        spacing: CGFloat = 0,
        typeHandlerSetup: (RVTypeHandler)->Void
    ) {
        let handler = RVTypeHandler(viewDependency)
        typeHandlerSetup(handler)
        for i in 0...handler.typeCount {
            register(CustomUITableViewCell.self, forCellReuseIdentifier: i.toString())
        }
        let boundDataSource = BoundMultiDataSource(source: data, handler: handler, spacing: spacing)
        dataSource = boundDataSource
        delegate = boundDataSource
        retain(as: "boundDataSource", item: boundDataSource)

        self.backgroundColor = UIColor.clear
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
        self.backgroundColor = UIColor.clear
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        layoutIfNeeded()
        var size = CGSize.zero
        for child in contentView.subviews {
            let childSize = child.sizeThatFits(size)
            size.width = max(size.width, childSize.width)
            size.height = max(size.height, childSize.height)
        }
        size.width += spacing * 2
        size.height += spacing * 2
        return size
    }

    override public func layoutSubviews() {
        super.layoutSubviews()
        contentView.frame = self.bounds.insetBy(dx: spacing, dy: spacing)
        for child in contentView.subviews {
            child.frame = contentView.bounds
            child.layoutSubviews()
        }
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

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let s = source.value
        var cell: CustomUITableViewCell = tableView.dequeueReusableCell(withIdentifier: "main-cell") as! CustomUITableViewCell
        cell.spacing = self.spacing
        cell.selectionStyle = .none
        if cell.obs == nil {
            var obs = StandardObservableProperty(defaultValue)
            cell.obs = obs
            let new = makeView(obs)
            cell.contentView.addSubview(new)
        }
        if let obs = cell.obs as? StandardObservableProperty<T> {
            obs.value = s[indexPath.row]
        }
        return cell
    }
}

class BoundMultiDataSource: NSObject, UITableViewDataSource, UITableViewDelegate, HasAtEnd {

    var source: ObservableProperty<[Any]>
    let handler: RVTypeHandler
    var atEnd: () -> Void = {}
    let spacing: CGFloat

    init(source: ObservableProperty<[Any]>, handler: RVTypeHandler, spacing: CGFloat) {
        self.source = source
        self.spacing = spacing
        self.handler = handler
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

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let s = source.value
        let typeIndex = self.handler.type(s)
        let cell: CustomUITableViewCell = tableView.dequeueReusableCell(withIdentifier: typeIndex.toString()) as! CustomUITableViewCell
        cell.spacing = self.spacing
        cell.selectionStyle = .none
        if cell.obs == nil {
            let (view, obs) = handler.make(type: typeIndex)
            cell.obs = obs
            cell.contentView.addSubview(view)
        }
        if let obs = cell.obs as? StandardObservableProperty<Any> {
            obs.value = s[indexPath.row]
        }
        return cell
    }
}

public class RVTypeHandler {

    public var viewDependency: ViewDependency


    public class Handler {

        public var type: Any.Type
        public var defaultValue: Any
        public var handler:  (ObservableProperty<Any>) -> View


        public init(type: Any.Type, defaultValue: Any, handler: @escaping (ObservableProperty<Any>) -> View) {
            self.type = type
            self.defaultValue = defaultValue
            self.handler = handler
        }
        convenience public init(_ type: Any.Type, _ defaultValue: Any, _ handler: @escaping (ObservableProperty<Any>) -> View) {
            self.init(type: type, defaultValue: defaultValue, handler: handler)
        }
    }
    var typeCount: Int32
    private var handlers: Array<Handler>
    private var defaultHandler: Handler

    public func handle(type: Any.Type, defaultValue: Any, action: @escaping (ObservableProperty<Any>) -> View) -> Void {
        handlers.add(Handler(type: type, defaultValue: defaultValue, handler: action))
        typeCount += 1
    }
    public func handle(_ type: Any.Type, _ defaultValue: Any, _ action: @escaping (ObservableProperty<Any>) -> View) -> Void {
        return handle(type: type, defaultValue: defaultValue, action: action)
    }

    public func handle<T: Any>(defaultValue: T, action: @escaping (ObservableProperty<T>) -> View) -> Void {
        handle(T.self, defaultValue) { (obs) in
            action(obs.map{ (it) in
                it as! T
            })
        }
    }
    public func handle<T: Any>(_ defaultValue: T, _ action: @escaping (ObservableProperty<T>) -> View) -> Void {
        return handle(defaultValue: defaultValue, action: action)
    }

    func canCast(_ x: Any, toConcreteType destType: Any.Type) -> Bool {
        return sequence(
            first: Mirror(reflecting: x), next: { $0.superclassMirror }
        )
        .contains { $0.subjectType == destType }
    }

    func type(item: Any) -> Int32 {
        var index = 0
        for handler in handlers{
            if canCast(item, toConcreteType: handler.type) {
                return Int32(index)
            }
                index += 1
            }
            return typeCount
        }
    func type(_ item: Any) -> Int32 {
        return type(item: item)
    }

    func make(type: Int32) -> (View, MutableObservableProperty<Any>) {
        var handler = defaultHandler
        if type < typeCount {
            handler = handlers[ type ]
        }
        let event = StandardObservableProperty<Any>(handler.defaultValue)
        let subview = handler.handler(event)
        return (subview, event)
    }

    public init(viewDependency: ViewDependency) {
        self.viewDependency = viewDependency
        let typeCount: Int32 = 0
        self.typeCount = typeCount
        let handlers: Array<Handler> = Array<Handler>()
        self.handlers = handlers
        let defaultHandler: Handler = Handler(type: Any.self, defaultValue: (), handler: { (obs) in
        EmptyView(viewDependency)
        })
        self.defaultHandler = defaultHandler
    }
    convenience public init(_ viewDependency: ViewDependency) {
        self.init(viewDependency: viewDependency)
    }
}
