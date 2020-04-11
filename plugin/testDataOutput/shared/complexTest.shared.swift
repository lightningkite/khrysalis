//Package: org.liftinggenerations.shared.views
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class ResourceLibraryViewData: ViewData {
    
    public var session: Session
    public var stack: ViewDataStack
    
    public weak var selected
    override public var title: String {
        get {
            return "Resource Library"
        }
    }
    override public var resourceName: String {
        get {
            return "ResourceLibrary"
        }
    }
    public var entries
    public var loading
    public var areMorePages
    public var page
    public var selectMode: Bool {
        get {
            return selected != nil
        }
    }
    public var category: ObservableProperty<Category>
    public var categoryOptions: ObservableProperty<Array<Category>>
    
    public enum Sort: String, StringEnum, CaseIterable, Codable {
        case None = "None"
        case Alphabetical = "Alphabetical"
        case AlphabeticalReversed = "AlphabeticalReversed"
        case Category = "Category"
        public init(from decoder: Decoder) throws {
            self = try Sort(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .None
        }
    }
    public var sort: ObservableProperty<Sort>
    public var sortOptions: ObservableProperty<Array<Sort>>
    public var query
    public var filteredEntries
    
    public func loadMore() -> Void {
        if !areMorePages.value {
            return
        }
        if loading.value {
            return
        }
        loading.value = true
        API.getResources(token: session.token, page: page.value, onResult: weakLambda{ (code, result, error) in 
            if let result = result {
                if result.isEmpty() {
                    self.areMorePages.value = false
                } else {
                    self.entries.value = self.entries.value + result
                }
                self.page.value = self.page.value + 1
            } else {
            }
            self.loading.value = false
        })
    }
    
    public func refresh() -> Void {
        loading.value = false
        areMorePages.value = true
        entries.value = Array<Resource>
        page.value = 1
        loadMore()
    }
    
    public func select(resource: Resource) -> Void {
        var selectedRef = selected
        if let selectedRef = selectedRef {
            selectedRef.value = selectedRef.value + resource
        }
    }
    public func select(_ resource: Resource) -> Void {
        return select(resource: resource)
    }
    
    public func remove(resource: Resource) -> Void {
        var selectedRef = selected
        if let selectedRef = selectedRef {
            selectedRef.value = selectedRef.value - resource
        }
    }
    public func remove(_ resource: Resource) -> Void {
        return remove(resource: resource)
    }
    
    public init(session: Session, stack: ViewDataStack, selectedProperty: ObservableProperty<Array<Resource>>?  = nil) {
        self.session = session
        self.stack = stack
        let selected = selectedProperty
        self.selected = selected
        let entries = ObservableProperty(underlyingValue: Array<Resource>)
        self.entries = entries
        let loading = ObservableProperty(underlyingValue: false)
        self.loading = loading
        let areMorePages = ObservableProperty(underlyingValue: true)
        self.areMorePages = areMorePages
        let page = ObservableProperty(underlyingValue: 1)
        self.page = page
        let category: ObservableProperty<Category> = ObservableProperty(underlyingValue: Category.anyCategory)
        self.category = category
        let categoryOptions: ObservableProperty<Array<Category>> = ObservableProperty(underlyingValue: [Category.anyCategory])
        self.categoryOptions = categoryOptions
        let sort: ObservableProperty<Sort> = ObservableProperty(underlyingValue: Sort.None)
        self.sort = sort
        let sortOptions: ObservableProperty<Array<Sort>> = ObservableProperty(underlyingValue: [Sort.None, Sort.Alphabetical, Sort.AlphabeticalReversed, Sort.Category])
        self.sortOptions = sortOptions
        let query = ObservableProperty("")
        self.query = query
        let filteredEntries = ObservableProperty(underlyingValue: Array<Resource>)
        self.filteredEntries = filteredEntries
        API.getCategories(token: session.token, onResult: weakLambda{ (code, options, error) in 
            if let options = options {
                var allOptions = Array<Category>
                allOptions.add(Category.anyCategory)
                
                for option in options {
                    allOptions.add(option)
                }
                self.categoryOptions.value = allOptions
            }
        })
        filteredEntries.calculatedBy(entries, query, category, sort){ (entries, query, category, sort) in 
            var filtered = entries.filter{ (r) in 
                if query != "", !r.title.toLowerCase().contains(query.toLowerCase()) {
                    return false
                }
                if category.id != Category.anyCategory.id, r.category != category.id {
                    return false
                }
                return true
            }
            var sorted = filtered
            switch sort {
            case Sort.None: break
            case Sort.Alphabetical:
                sorted = sorted.sortedBy{ () in 
                    it.title
                }
            case Sort.AlphabeticalReversed:
                sorted = sorted.sortedByDescending{ () in 
                    it.title
                }
            case Sort.Category:
                sorted = sorted.sortedBy{ () in 
                    it.category
                }
            default: break
            }
            return sorted
        }
        refresh()
    }
    convenience public init(_ session: Session, _ stack: ViewDataStack, _ selectedProperty: ObservableProperty<Array<Resource>>?  = nil) {
        self.init(session: session, stack: stack, selectedProperty: selectedProperty)
    }
}
 
