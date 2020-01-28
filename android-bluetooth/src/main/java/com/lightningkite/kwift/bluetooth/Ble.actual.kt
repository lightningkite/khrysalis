package com.lightningkite.kwift.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.lightningkite.kwift.PlatformSpecific
import com.lightningkite.kwift.views.ViewDependency
import com.lightningkite.kwift.views.android.startIntent
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.util.*

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@SuppressLint("MissingPermission")
object Ble {

    val notificationDescriptorUuid: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    @PlatformSpecific
    fun activateBluetoothDialog(
        dependency: ViewDependency,
        onPermissionRejected: () -> Unit,
        onBluetooth: (BluetoothAdapter) -> Unit
    ) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter != null) {
            if (adapter.isEnabled) {
                if (ContextCompat.checkSelfPermission(
                        dependency.context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    onBluetooth.invoke(adapter)
                } else {
                    dependency.requestPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) {
                        if (it) {
                            onBluetooth.invoke(adapter)
                        } else {
                            onPermissionRejected.invoke()
                        }
                    }
                }
            } else {
                dependency.startIntent(android.content.Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)) { code, intent ->
                    activateBluetoothDialog(dependency, onPermissionRejected, onBluetooth)
                }
            }
        }
    }

    @PlatformSpecific
    fun getBluetooth(dependency: ViewDependency): Observable<RxBleClient> {
        return Observable.create { emitter: ObservableEmitter<RxBleClient> ->
            activateBluetoothDialog(
                dependency = dependency,
                onPermissionRejected = {
                    emitter.onComplete()
                },
                onBluetooth = {
                    emitter.onNext(RxBleClient.create(dependency.context))
                }
            )
        }
    }

    /**
     * @param serviceUuids If default, advertises all services described by [characteristics].
     */
    fun serve(
        viewDependency: ViewDependency,
        characteristics: List<BleCharacteristicServer>,
        serviceUuids: List<UUID>? = null,
        advertisingIntensity: Float = .5f
    ): BleServer {
        val impl =
            BleServerImpl(
                characteristics = characteristics.groupBy { it.characteristic.serviceUuid }.mapValues { it.value.associateBy { it.characteristic.characteristicUuid } },
                serviceUuids = serviceUuids,
                advertisingIntensity = advertisingIntensity
            )
        val context = viewDependency.context
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val server = manager.openGattServer(context, impl)
        impl.advertiser = manager.adapter.bluetoothLeAdvertiser
        impl.server = server
        return impl
    }

    fun scan(
        viewDependency: ViewDependency,
        withServices: List<UUID> = listOf(),
        intensity: Float = .5f
    ): Observable<BleScanResult> = getBluetooth(viewDependency)
        .switchMap { it ->
            it.scanBleDevices(
                ScanSettings.Builder().let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        it.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    } else {
                        it
                    }
                }.setScanMode(
                    when (intensity) {
                        in 0f..0.33f -> ScanSettings.SCAN_MODE_LOW_POWER
                        in 0.33f..0.66f -> ScanSettings.SCAN_MODE_BALANCED
                        in 0.66f..1f -> ScanSettings.SCAN_MODE_LOW_LATENCY
                        else -> ScanSettings.SCAN_MODE_LOW_POWER
                    }
                ).build(),
                *(withServices.map {
                    ScanFilter.Builder().setServiceUuid(ParcelUuid(it)).build()
                }.toTypedArray() as Array<ScanFilter>)
            )
        }
        .map { it ->
            BleScanResult(
                info = BleDeviceInfo(
                    id = it.bleDevice.macAddress,
                    name = it.bleDevice.name
                ),
                rssi = it.rssi
            )
        }

    fun connect(viewDependency: ViewDependency, deviceId: String): Observable<BleConnection> {
        var device = BleDeviceInfo(deviceId, null)
        return getBluetooth(viewDependency)
            .flatMap {
                val fullDevice = it.getBleDevice(deviceId)
                device = BleDeviceInfo(fullDevice.macAddress, fullDevice.name)
                fullDevice.establishConnection(false)
            }
            .flatMap { it.discoverServices().map { _ -> it }.toObservable() }
            .map { BleConnectionImpl(it, device) }
    }
}
