//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation


//--- AutoCompleteTextView.bind(ObservableProperty<List<T>>, (T)->String, (T)->Unit)
public extension UIAutoCompleteTextField {
    func bind<T>(_ options: ObservableProperty<Array<T>>, _ toString: @escaping (T) -> String, _ onItemSelected: @escaping (T) -> Void) -> Void {
        if let font = font { theme.font = font }
        if let textColor = textColor { theme.fontColor = textColor }
        
        var optionsMap = Dictionary<String, T>()
        options.subscribeBy { value in
            optionsMap = [:]
            for item in value {
                let original = toString(item)
                var asString = original
                var index = 2
                while optionsMap[asString] != nil {
                    asString = original + " (\(index))"
                    index += 1
                }
                optionsMap[asString] = item
            }
            let array = Array(optionsMap.keys)
            self.filterStrings(array)
        }.until(self.removed)
        itemSelectionHandler = { (items, itemPosition) in
            if let item = optionsMap[items[itemPosition].title] {
                onItemSelected(item)
            }
        }
    }
    func bind<T>(options: ObservableProperty<Array<T>>, toString: @escaping (T) -> String, onItemSelected: @escaping (T) -> Void) -> Void {
        return bind(options, toString, onItemSelected)
    }
    func bindList<T>(options: ObservableProperty<Array<T>>, toString: @escaping (T) -> String, onItemSelected: @escaping (T) -> Void) -> Void {
        if let font = font { theme.font = font }
        if let textColor = textColor { theme.fontColor = textColor }

        var optionsMap = Dictionary<String, T>()
        options.subscribeBy { value in
            optionsMap = [:]
            for item in value {
                let original = toString(item)
                var asString = original
                var index = 2
                while optionsMap[asString] != nil {
                    asString = original + " (\(index))"
                    index += 1
                }
                optionsMap[asString] = item
            }
            let array = Array(optionsMap.keys)
            self.filterStrings(array)
        }.until(self.removed)
        itemSelectionHandler = { (items, itemPosition) in
            if let item = optionsMap[items[itemPosition].title] {
                onItemSelected(item)
            }
        }
    }
}
