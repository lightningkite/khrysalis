package com.lightningkite.kwift.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import java.io.Closeable
import java.util.ArrayList


class BluetoothGattListener() : BluetoothGattCallback(), Closeable {

    val onReadRemoteRssi = ArrayList<(gatt: BluetoothGatt?, rssi: Int, status: Int) -> Unit>()
    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        onReadRemoteRssi.forEach { it(gatt, rssi, status) }
    }

    val onCharacteristicRead = ArrayList<(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) -> Unit>()
    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        onCharacteristicRead.forEach { it(gatt, characteristic, status) }
    }

    val onCharacteristicWrite = ArrayList<(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) -> Unit>()
    override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        onCharacteristicWrite.forEach { it(gatt, characteristic, status) }
    }

    val onServicesDiscovered = ArrayList<(gatt: BluetoothGatt?, status: Int) -> Unit>()
    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        onServicesDiscovered.forEach { it(gatt, status) }
    }

    val onMtuChanged = ArrayList<(gatt: BluetoothGatt?, mtu: Int, status: Int) -> Unit>()
    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
        onMtuChanged.forEach { it(gatt, mtu, status) }
    }

    val onReliableWriteCompleted = ArrayList<(gatt: BluetoothGatt?, status: Int) -> Unit>()
    override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
        onReliableWriteCompleted.forEach { it(gatt, status) }
    }

    val onDescriptorWrite = ArrayList<(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) -> Unit>()
    override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
        onDescriptorWrite.forEach { it(gatt, descriptor, status) }
    }

    val onCharacteristicChanged = ArrayList<(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) -> Unit>()
    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        onCharacteristicChanged.forEach { it(gatt, characteristic) }
    }

    val onDescriptorRead = ArrayList<(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) -> Unit>()
    override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
        onDescriptorRead.forEach { it(gatt, descriptor, status) }
    }

    val onConnectionStateChange = ArrayList<(gatt: BluetoothGatt?, status: Int, newState: Int) -> Unit>()
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        Log.i("BluetoothGattListener", "Connection state change Status: $status, newState: $newState")
        onConnectionStateChange.forEach { it(gatt, status, newState) }
    }

    override fun close() {
        onReadRemoteRssi.clear()
        onCharacteristicRead.clear()
        onCharacteristicWrite.clear()
        onServicesDiscovered.clear()
        onMtuChanged.clear()
        onReliableWriteCompleted.clear()
        onDescriptorWrite.clear()
        onCharacteristicChanged.clear()
        onDescriptorRead.clear()
        onConnectionStateChange.clear()
    }

    init {
        val listener = this
        listener.onReadRemoteRssi.add { _, rssi, status ->
            Log.i("ble.kt", "onReadRemoteRssi rssi $rssi status $status")
        }
        listener.onCharacteristicRead.add { _, char, status ->
            Log.i("ble.kt", "onCharacteristicRead char ${char?.uuid} status $status")
        }
        listener.onCharacteristicWrite.add { _, char, status ->
            Log.i("ble.kt", "onCharacteristicWrite char ${char?.uuid} status $status")
        }
        listener.onServicesDiscovered.add { _, status ->
            Log.i("ble.kt", "onServicesDiscovered status $status")
        }
        listener.onMtuChanged.add { _, mtu, status ->
            Log.i("ble.kt", "onMtuChanged mtu $mtu status $status")
        }
        listener.onReliableWriteCompleted.add { _, status ->
            Log.i("ble.kt", "onReliableWriteCompleted status $status")
        }
        listener.onDescriptorWrite.add { _, desc, status ->
            Log.i("ble.kt", "onDescriptorWrite desc $desc status $status")
        }
        listener.onCharacteristicChanged.add { _, char ->
            Log.i("ble.kt", "onCharacteristicChanged char ${char?.uuid}")
        }
        listener.onDescriptorRead.add { _, desc, status ->
            Log.i("ble.kt", "onDescriptorRead desc $desc status $status")
        }
        listener.onConnectionStateChange.add { _, status, newState ->
            Log.i("ble.kt", "onConnectionStateChange status $status newState $newState")
        }
    }
}
