//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- Spinner.bind(ObservableProperty<List<T>>, MutableObservableProperty<T>, (ObservableProperty<T>)->View)
public extension Dropdown {
    func bind<T: Equatable>(_ options: ObservableProperty<Array<T>>, _ selected: MutableObservableProperty<T>, _ makeView: @escaping (ObservableProperty<T>) -> View) -> Void {
        let boundDataSource = PickerBoundDataSource(data: options, selected: selected, makeView: makeView)
        self.dataSource = boundDataSource
        self.delegate = boundDataSource
        retain(as: "boundDataSource", item: boundDataSource, until: removed)

        options.subscribeBy { value in
            self.pickerView.reloadAllComponents()
        }.until(self.removed)
        self.selectedView = makeView(selected)
        selected.subscribeBy { value in
            var index = Int(options.value.indexOf(value))
            if index != -1 {
                self.pickerView.selectRow(index, inComponent: 0, animated: false)
            }
        }.until(self.removed)
    }
    func bind<T: Equatable>(options: ObservableProperty<Array<T>>, selected: MutableObservableProperty<T>, makeView: @escaping (ObservableProperty<T>) -> View) -> Void {
        return bind(options, selected, makeView)
    }
}

//TODO: Check potential memory leak
class PickerBoundDataSource<T, VIEW: UIView>: NSObject, UIPickerViewDataSource, UIPickerViewDelegate {
    var data: ObservableProperty<[T]>
    var selected: MutableObservableProperty<T>
    let makeView: (ObservableProperty<T>) -> UIView

    private var ext = ExtensionProperty<UIView, MutableObservableProperty<T>>()

    init(data: ObservableProperty<[T]>, selected: MutableObservableProperty<T>, makeView: @escaping (ObservableProperty<T>) -> UIView) {
        self.data = data
        self.selected = selected
        self.makeView = makeView
        super.init()
    }

    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }

    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        let options = data.value
        return options.count
    }

    func pickerView(_ pickerView: UIPickerView, viewForRow row: Int, forComponent component: Int, reusing view: UIView?) -> UIView {
        let v = view ?? {
            let obs = StandardObservableProperty(selected.value)
            let new = makeView(obs)
            ext.set(new, obs)
            return new
        }()
        if let obs = ext.get(v) {
            let value = data.value[row]
            obs.value = value
        }
        return v
    }

    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        selected.value = data.value[row]
    }

}
