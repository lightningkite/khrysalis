//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- RecyclerView.whenScrolledToEnd(()->Unit)
public extension UICollectionView {
    func whenScrolledToEnd(action: @escaping () -> Void) -> Void{
        post{
            if let delegate = self.delegate as? HasAtEnd {
                delegate.setAtEnd(action: action)
            } else {
                fatalError("You must give the view a delegate implementing the HasAtEnd protocol first.  You can do so using a 'bind'.")
            }
        }
    }

    //--- RecyclerView.bind(ObservableProperty<List<T>>, T, (ObservableProperty<T>)->View)
    private func setupVertical() {
        self.addOnLayoutSubviews {
            if let layout = self.collectionViewLayout as? ReversibleFlowLayout {
                layout.estimatedItemSize = CGSize(width: self.bounds.size.width - self.contentInset.left - self.contentInset.right, height: 50)
                layout.itemSize = UICollectionViewFlowLayout.automaticSize
            }
        }
    }
    func bind<T>(data: ObservableProperty<Array<T>>, defaultValue: T, makeView: @escaping (ObservableProperty<T>) -> View) -> Void {
        post {
            let dg = GeneralCollectionDelegate(
                itemCount: data.value.count,
                getItem: { data.value[$0] },
                makeView: { (obs, _) in makeView(obs) }
            )
            self.retain(as: "delegate", item: dg, until: self.removed)
            self.delegate = dg
            self.dataSource = dg
            data.subscribeBy { it in
                dg.itemCount = it.count
                self.reloadData()
            }.until(self.removed)
            self.setupVertical()
        }
    }


    //--- RecyclerView.bindMulti(ViewDependency, ObservableProperty<List<Any>>, (RVTypeHandler)->Unit)
    func bindMulti(viewDependency: ViewDependency, data: ObservableProperty<Array<Any>>, typeHandlerSetup: (RVTypeHandler) -> Void) -> Void {
        let handler = RVTypeHandler(viewDependency)
        typeHandlerSetup(handler)
        post {
            
            let dg = GeneralCollectionDelegate(
                itemCount: data.value.count,
                getItem: { data.value[$0] },
                makeView: { (obs, type) in handler.make(type: type, property: obs) },
                getType: { handler.type(item: $0) }
            )
            self.retain(as: "delegate", item: dg, until: self.removed)
            self.delegate = dg
            self.dataSource = dg
            data.subscribeBy { it in
                dg.itemCount = it.count
                self.reloadData()
            }.until(self.removed)
            self.setupVertical()
        }
    }

    //--- RecyclerView.bindMulti(ObservableProperty<List<T>>, T, (T)->Int, (Int,ObservableProperty<T>)->View)
    func bindMulti<T>(data: ObservableProperty<Array<T>>, defaultValue: T, determineType: @escaping (T) -> Int, makeView: @escaping (Int, ObservableProperty<T>) -> View) -> Void {
        post {
            let dg = GeneralCollectionDelegate(
                itemCount: data.value.count,
                getItem: { data.value[$0] },
                makeView: { (obs, type) in makeView(type, obs) },
                getType: determineType
            )
            self.retain(as: "delegate", item: dg, until: self.removed)
            self.delegate = dg
            self.dataSource = dg
            data.subscribeBy { it in
                dg.itemCount = it.count
                self.reloadData()
            }.until(self.removed)
            self.setupVertical()
        }
    }

    //--- RecyclerView.bindRefresh(ObservableProperty<Boolean>, ()->Unit)
    func bindRefresh(loading: ObservableProperty<Bool>, refresh: @escaping () -> Void) -> Void {
        let control = UIRefreshControl()
        control.addAction(for: .valueChanged, action: refresh)
        if #available(iOS 10.0, *) {
            refreshControl = control
        } else {
            addSubview(control)
        }
        loading.subscribeBy { (value) in
            if value {
                control.beginRefreshing()
            } else {
                control.endRefreshing()
            }
        }.until(control.removed)
    }
}


//--- RVTypeHandler.{

public class RVTypeHandler {

    public var viewDependency: ViewDependency

    //--- RVTypeHandler.Primary Constructor
    public init(viewDependency: ViewDependency) {
        self.viewDependency = viewDependency
        let typeCount: Int = 0
        self.typeCount = typeCount
        let handlers: Array<Handler> = Array<Handler>()
        self.handlers = handlers
        let defaultHandler: Handler = Handler(type: Any.self, defaultValue: (), handler: { (obs) in
        newEmptyView(viewDependency)
        })
        self.defaultHandler = defaultHandler
    }
    convenience public init(_ viewDependency: ViewDependency) {
        self.init(viewDependency: viewDependency)
    }

    //--- RVTypeHandler.Handler.{
    public class Handler {

        public var type: Any.Type
        public var defaultValue: Any
        public var handler:  (ObservableProperty<Any>) -> View

        //--- RVTypeHandler.Handler.Primary Constructor
        public init(type: Any.Type, defaultValue: Any, handler: @escaping (ObservableProperty<Any>) -> View) {
            self.type = type
            self.defaultValue = defaultValue
            self.handler = handler
        }
        convenience public init(_ type: Any.Type, _ defaultValue: Any, _ handler: @escaping (ObservableProperty<Any>) -> View) {
            self.init(type: type, defaultValue: defaultValue, handler: handler)
        }

        //--- RVTypeHandler.Handler.}
    }

    var typeCount: Int
    private var handlers: Array<Handler>
    private var defaultHandler: Handler

    //--- RVTypeHandler.handle(KClass<*>, Any,  @escaping()(ObservableProperty<Any>)->View)
    public func handle(type: Any.Type, defaultValue: Any, action: @escaping (ObservableProperty<Any>) -> View) -> Void {
        handlers.append(Handler(type: type, defaultValue: defaultValue, handler: action))
        typeCount += 1
    }
    public func handle(_ type: Any.Type, _ defaultValue: Any, _ action: @escaping (ObservableProperty<Any>) -> View) -> Void {
        return handle(type: type, defaultValue: defaultValue, action: action)
    }

    //--- RVTypeHandler.handle(T,  @escaping()(ObservableProperty<T>)->View)
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

    //--- RVTypeHandler Helpers
    func canCast(_ x: Any, toConcreteType destType: Any.Type) -> Bool {
        return sequence(
            first: Mirror(reflecting: x), next: { $0.superclassMirror }
        )
        .contains { $0.subjectType == destType }
    }

    func type(item: Any) -> Int {
        var index = 0
        for handler in handlers{
            if canCast(item, toConcreteType: handler.type) {
                return Int(index)
            }
            index += 1
        }
        return typeCount
    }
    func type(_ item: Any) -> Int {
        return type(item: item)
    }

    func make(type: Int, property: ObservableProperty<Any>) -> UIView {
        var handler = defaultHandler
        if type < typeCount {
            handler = handlers[ type ]
        }
        let subview = handler.handler(property)
        return subview
    }

    //--- RVTypeHandler.}
}

protocol HasAtEnd {
    var atEnd: () -> Void { get set }
    func setAtEnd(action: @escaping () -> Void)
}

class GeneralCollectionDelegate<T>: NSObject, UICollectionViewDelegate, UICollectionViewDataSource, HasAtEnd {
    var atEnd: () -> Void = {}
    
    func setAtEnd(action: @escaping () -> Void) {
        self.atEnd = action
    }
    
    var itemCount: Int
    let getItem: (Int) -> T
    let makeView: (ObservableProperty<T>, Int) -> UIView
    let getType: (T) -> Int
    var atPosition: (Int) -> Void = { _ in }
    
    init(
        itemCount: Int = 0,
        getItem: @escaping (Int) -> T,
        makeView: @escaping (ObservableProperty<T>, Int) -> UIView,
        getType: @escaping (T) -> Int = { _ in 0 },
        atPosition: @escaping (Int) -> Void = { _ in }
    ) {
        self.itemCount = itemCount
        self.getItem = getItem
        self.makeView = makeView
        self.getType = getType
        self.atPosition = atPosition
    }
    
    private var registered: Set<Int> = []
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let item = getItem(indexPath.row)
        let type = getType(item)
        if registered.insert(type).inserted {
            collectionView.register(SizedUICollectionViewCell.self, forCellWithReuseIdentifier: String(type))
        }
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: String(type), for: indexPath) as! SizedUICollectionViewCell
        if cell.obs == nil {
            let obs = StandardObservableProperty<T>(underlyingValue: item)
            cell.contentView.addSubview(makeView(obs, type))
            cell.obs = obs
        }
        if let obs = cell.obs as? MutableObservableProperty<T> {
            obs.value = item
        } else {
            fatalError("Could not find cell property")
        }
        post {
            cell.refreshLifecycle()
        }
        cell.layoutIfNeeded()
        cell.bounds.size = cell.systemLayoutSizeFitting(collectionView.bounds.size, withHorizontalFittingPriority: .required, verticalFittingPriority: .fittingSizeLevel)
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return itemCount
    }
    func collectionView(_ collectionView: UICollectionView, willDisplay cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
        if let obs = (cell as? SizedUICollectionViewCell)?.obs as? MutableObservableProperty<T> {
            obs.value = getItem(indexPath.row)
        }
        if(indexPath.row >= itemCount - 1 && itemCount > 1){
            print("Triggered end with \(indexPath.row) size \(itemCount)")
            atEnd()
        }
        if let cell = cell as? SizedUICollectionViewCell {
            cell.resizeEnabled = true
        }
    }
    func collectionView(_ collectionView: UICollectionView, didEndDisplaying cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
        
        if let cell = cell as? SizedUICollectionViewCell {
            cell.resizeEnabled = false
        }
    }
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        let collectionView = scrollView as! UICollectionView
        if let x = collectionView.currentIndex {
            atPosition(Int(x))
        }
    }
}

class SizedUICollectionViewCell: UICollectionViewCell {
    var obs: Any?
    var isVertical = true

    override init(frame: CGRect) {
        super.init(frame: frame)
        self.contentView.clipsToBounds = true
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        self.contentView.clipsToBounds = true
    }

    override public func layoutSubviews() {
        super.layoutSubviews()
        contentView.frame = self.bounds
        for child in contentView.subviews {
            child.frame = contentView.bounds
            child.layoutSubviews()
        }
    }
    override func sizeThatFits(_ size: CGSize) -> CGSize {
        var outSize = CGSize.zero
        layoutIfNeeded()
        for child in contentView.subviews {
            let childSize = child.sizeThatFits(size)
            outSize.width = max(outSize.width, childSize.width)
            outSize.height = max(outSize.height, childSize.height)
        }
        outSize.width = max(outSize.width, 20)
        outSize.height = max(outSize.height, 20)
        if isVertical {
            outSize.width = size.width
        } else {
            outSize.height = size.height
        }
        return outSize
    }
    var resizeEnabled = false
    public func refreshSize() {
        guard resizeEnabled else { return }
        var current = self.superview
        while current != nil && !(current is UICollectionView) {
            current = current?.superview
        }
        if let current = current as? UICollectionView {
            if let dataSource = current.dataSource,
                dataSource.collectionView(current, numberOfItemsInSection: 0) == current.numberOfItems(inSection: 0)
            {
//                UIView.performWithoutAnimation {
                    current.performBatchUpdates({}, completion: nil)
//                }
            } else {
                current.reloadData()
            }
        }
    }
//    override func systemLayoutSizeFitting(_ targetSize: CGSize, withHorizontalFittingPriority horizontalFittingPriority: UILayoutPriority, verticalFittingPriority: UILayoutPriority) -> CGSize {
//        var outSize = isVertical ? super.systemLayoutSizeFitting(CGSize(width: targetSize.width, height: .greatestFiniteMagnitude), withHorizontalFittingPriority: .required, verticalFittingPriority: .fittingSizeLevel) : super.systemLayoutSizeFitting(targetSize, withHorizontalFittingPriority: horizontalFittingPriority, verticalFittingPriority: verticalFittingPriority)
//        for child in contentView.subviews {
//            let childSize = child.sizeThatFits(targetSize)
//            outSize.width = max(outSize.width, childSize.width)
//            outSize.height = max(outSize.height, childSize.height)
//        }
//        outSize.width = max(outSize.width, 20)
//        outSize.height = max(outSize.height, 20)
//        return outSize
//    }
}
public extension UICollectionView {
    
    //--- RecyclerView.reverseDirection
    class ReversibleFlowLayout: UICollectionViewFlowLayout {
        
        var reversed: Bool = false

        override public func layoutAttributesForItem(at indexPath: IndexPath) -> UICollectionViewLayoutAttributes? {
            let attrs = super.layoutAttributesForItem(at: indexPath)
            if reversed {
                attrs?.transform = CGAffineTransform(scaleX: 1, y: -1)
            }
            return attrs
        }

        override public func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
            let attrsList = super.layoutAttributesForElements(in: rect)
            if reversed, let list = attrsList {
                for i in 0..<list.count {
                    list[i].transform = CGAffineTransform(scaleX: 1, y: -1)
                }
            }
            return attrsList
        }
    }
    var reverseDirection: Bool {
        get {
            return (collectionViewLayout as? ReversibleFlowLayout)?.reversed ?? false
        }
        set(value) {
            if value {
                self.transform = CGAffineTransform(scaleX: 1, y: -1)
            } else {
                self.transform = CGAffineTransform(scaleX: 1, y: 1)
            }
            if let l = collectionViewLayout as? ReversibleFlowLayout {
                l.reversed = value
            }
        }
    }
}
// //--- Adapters
// class CustomUITableViewCell: UITableViewCell {
//     var obs: Any?
//     var spacing: CGFloat = 0
//
//     override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
//         super.init(style: style, reuseIdentifier: reuseIdentifier)
//         self.backgroundColor = UIColor.clear
//     }
//
//     required init?(coder aDecoder: NSCoder) {
//         fatalError("init(coder:) has not been implemented")
//     }
//
//     override func sizeThatFits(_ size: CGSize) -> CGSize {
//         layoutIfNeeded()
//         var outSize = CGSize.zero
//         for child in contentView.subviews {
//             let childSize = child.sizeThatFits(size)
//             outSize.width = max(outSize.width, childSize.width)
//             outSize.height = max(outSize.height, childSize.height)
//         }
//         outSize.width += spacing * 2
//         outSize.height += spacing * 2
//         return outSize
//     }
//
//     override public func layoutSubviews() {
//         super.layoutSubviews()
//         contentView.frame = self.bounds.insetBy(dx: spacing, dy: spacing)
//         for child in contentView.subviews {
//             child.frame = contentView.bounds
//             child.layoutSubviews()
//         }
//     }
//     public func refreshSize() {
//         var current = self.superview
//         while current != nil && !(current is UITableView) {
//             current = current?.superview
//         }
//         if let current = current as? UITableView {
//             if let dataSource = current.dataSource,
//                 dataSource.tableView(current, numberOfRowsInSection: 0) == current.numberOfRows(inSection: 0)
//             {
//                 current.beginUpdates()
//                 current.endUpdates()
//             } else {
//                 current.reloadData()
//             }
//         }
//     }
// }
//
//
// class BoundDataSource<T, VIEW: UIView>: NSObject, UITableViewDataSource, UITableViewDelegate, HasAtEnd {
//
//     var source: ObservableProperty<[T]>
//     let makeView: (ObservableProperty<T>) -> UIView
//     let defaultValue: T
//     var atEnd: () -> Void = {}
//     let spacing: CGFloat
//
//     var reversedDirection: Bool = false
//
//     init(source: ObservableProperty<[T]>, defaultValue: T, makeView: @escaping (ObservableProperty<T>) -> UIView) {
//         self.source = source
//         self.spacing = 0
//         self.makeView = makeView
//         self.defaultValue = defaultValue
//         super.init()
//     }
//
//     func setAtEnd(action: @escaping () -> Void) {
//         self.atEnd = action
//     }
//
//     func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
//         let value = self.source.value
//         let count = value.count
//         return count
//     }
//
//     private var showingLast = false
//     func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
//         let nowShowingLast = indexPath.row >= (source.value.count) - 1
//         if nowShowingLast && !showingLast {
//             atEnd()
//         }
//         showingLast = nowShowingLast
//     }
//
//     func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
//         let s = source.value
//         let cell: CustomUITableViewCell = tableView.dequeueReusableCell(withIdentifier: "main-cell") as! CustomUITableViewCell
//         cell.spacing = self.spacing
//         if reversedDirection {
//             cell.transform = CGAffineTransform(rotationAngle: CGFloat.pi)
//         } else {
//             cell.transform = CGAffineTransform(rotationAngle: 0)
//         }
//         cell.selectionStyle = .none
//         if cell.obs == nil {
//             let obs = StandardObservableProperty(underlyingValue: defaultValue)
//             cell.obs = obs
//             let new = makeView(obs)
//             cell.contentView.addSubview(new)
//         }
//         if let obs = cell.obs as? StandardObservableProperty<T> {
//             obs.value = s[indexPath.row]
//         }
//         post {
//             cell.refreshLifecycle()
//         }
//         return cell
//     }
//
// }
//
// class BoundMultiDataSourceSameType<T>: NSObject, UITableViewDataSource, UITableViewDelegate, HasAtEnd {
//
//     var source: ObservableProperty<[T]>
//     let getType: (T) -> Int
//     let makeView: (Int, ObservableProperty<T>) -> UIView
//     let defaultValue: T
//     var atEnd: () -> Void = {}
//     let spacing: CGFloat
//     var registered: Set<Int> = []
//
//     var reversedDirection: Bool = false
//
//     init(source: ObservableProperty<[T]>, defaultValue: T, getType: @escaping (T)->Int, makeView: @escaping (Int, ObservableProperty<T>) -> UIView) {
//         self.source = source
//         self.spacing = 0
//         self.makeView = makeView
//         self.defaultValue = defaultValue
//         self.getType = getType
//         super.init()
//     }
//
//     func setAtEnd(action: @escaping () -> Void) {
//         self.atEnd = action
//     }
//
//     func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
//         let value = self.source.value
//         let count = value.count
//         return count
//     }
//
//     private var showingLast = false
//     func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
//         let nowShowingLast = indexPath.row >= (source.value.count) - 1
//         if nowShowingLast && !showingLast {
//             atEnd()
//         }
//         showingLast = nowShowingLast
//     }
//
//     func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
//         let s = source.value
//         let typeIndex = getType(s[indexPath.row])
//         if registered.insert(typeIndex).inserted {
//             tableView.register(CustomUITableViewCell.self, forCellReuseIdentifier: String(typeIndex))
//         }
//         let cell: CustomUITableViewCell = tableView.dequeueReusableCell(withIdentifier: String(typeIndex)) as! CustomUITableViewCell
//         cell.spacing = self.spacing
//         if reversedDirection {
//             cell.transform = CGAffineTransform(rotationAngle: CGFloat.pi)
//         } else {
//             cell.transform = CGAffineTransform(rotationAngle: 0)
//         }
//         cell.selectionStyle = .none
//         if cell.obs == nil {
//             let obs = StandardObservableProperty(underlyingValue: defaultValue)
//             cell.obs = obs
//             let new = makeView(typeIndex, obs)
//             cell.contentView.addSubview(new)
//         }
//         if let obs = cell.obs as? StandardObservableProperty<T> {
//             obs.value = s[indexPath.row]
//         }
//         return cell
//     }
// }
//
// class BoundMultiDataSource: NSObject, UITableViewDataSource, UITableViewDelegate, HasAtEnd {
//
//     var source: ObservableProperty<[Any]>
//     let handler: RVTypeHandler
//     var atEnd: () -> Void = {}
//     let spacing: CGFloat
//
//     var reversedDirection: Bool = false
//
//     init(source: ObservableProperty<[Any]>, handler: RVTypeHandler) {
//         self.source = source
//         self.spacing = 0
//         self.handler = handler
//         super.init()
//     }
//
//     func setAtEnd(action: @escaping () -> Void) {
//         self.atEnd = action
//     }
//
//     func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
//         let value = self.source.value
//         let count = value.count
//         return count
//     }
//
//     private var showingLast = false
//     func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
//         let nowShowingLast = indexPath.row >= (source.value.count) - 1
//         if nowShowingLast && !showingLast {
//             atEnd()
//         }
//         showingLast = nowShowingLast
//     }
//
//     func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
//         let s = source.value
//         let typeIndex = self.handler.type(s[indexPath.row])
//         let cell: CustomUITableViewCell = tableView.dequeueReusableCell(withIdentifier: String(typeIndex)) as! CustomUITableViewCell
//         cell.spacing = self.spacing
//         if reversedDirection {
//             cell.transform = CGAffineTransform(rotationAngle: CGFloat.pi)
//         } else {
//             cell.transform = CGAffineTransform(rotationAngle: 0)
//         }
//         cell.selectionStyle = .none
//         if cell.obs == nil {
//             let (view, obs) = handler.make(type: typeIndex)
//             cell.obs = obs
//             cell.contentView.addSubview(view)
//         }
//         if let obs = cell.obs as? StandardObservableProperty<Any> {
//             obs.value = s[indexPath.row]
//         }
//         return cell
//     }
//
// }
