package com.lightningkite.kwift.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.lightningkite.kwift.Failable
import com.lightningkite.kwift.PlatformSpecific
import com.lightningkite.kwift.net.HttpClient
import com.lightningkite.kwift.observables.Close
import com.lightningkite.kwift.views.ViewDependency
import com.lightningkite.kwift.views.android.startIntent
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
            BleServerImpl(characteristics.groupBy { it.serviceUuid }.mapValues { it.value.associate { it.uuid to it } })
        val context = viewDependency.context
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val server = manager.openGattServer(context, impl)
        impl.server = server
        val advertiser = manager.adapter.bluetoothLeAdvertiser
        val advertiserCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                println("Advertising successfully!")
            }

            override fun onStartFailure(errorCode: Int) {
                Log.e("Ble.Actual", "Failed to begin advertising.  Code: $errorCode")
            }
        }
        advertiser.startAdvertising(
            AdvertiseSettings.Builder()
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setTimeout(0)
                .setConnectable(true)
                .setAdvertiseMode(
                    when (advertisingIntensity) {
                        in 0f..0.33f -> AdvertiseSettings.ADVERTISE_MODE_LOW_POWER
                        in 0.33f..0.66f -> AdvertiseSettings.ADVERTISE_MODE_BALANCED
                        in 0.66f..1f -> AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
                        else -> AdvertiseSettings.ADVERTISE_MODE_LOW_POWER
                    }
                )
                .build(),
            AdvertiseData.Builder()
                .apply {
                    serviceUuids?.forEach {
                        addServiceUuid(ParcelUuid(it))
                    } ?: run {
                        impl.characteristics.keys.forEach {
                            addServiceUuid(ParcelUuid(it))
                        }
                    }
                    //TODO: Service/manufacturing data?
                }
                .build(),
            advertiserCallback
        )
        impl.advertiser = advertiser
        impl.advertiserCallback = advertiserCallback
        return impl
    }

    fun scan(
        viewDependency: ViewDependency,
        withServices: List<UUID> = listOf(),
        intensity: Float = .5f,
        onDeviceFound: (info: BleDeviceInfo) -> Unit
    ): Disposable {
        return object : Disposable, ScanCallback() {

            var scanner: BluetoothLeScanner? = null

            init {
                activateBluetoothDialog(
                    dependency = viewDependency,
                    onPermissionRejected = { },
                    onBluetooth = {
                        scanner = it.bluetoothLeScanner
                        it.bluetoothLeScanner.startScan(
                            withServices.map {
                                ScanFilter.Builder().setServiceUuid(ParcelUuid(it)).build()
                            },
                            ScanSettings.Builder().setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).setScanMode(when (intensity) {
                                in 0f..0.33f -> ScanSettings.SCAN_MODE_LOW_POWER
                                in 0.33f..0.66f -> ScanSettings.SCAN_MODE_BALANCED
                                in 0.66f..1f -> ScanSettings.SCAN_MODE_LOW_LATENCY
                                else -> ScanSettings.SCAN_MODE_LOW_POWER
                            }).build(),
                            this
                        )
                    }
                )
            }

            override fun onScanResult(callbackType: Int, result: ScanResult) {
                onDeviceFound.invoke(
                    BleDeviceInfo(
                        result.device.address,
                        result.device.name ?: "",
                        result.rssi
                    )
                )
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("Ble", "Scan failed with code $errorCode.")
            }

            override fun isDisposed(): Boolean {
                return scanner == null
            }

            override fun dispose() {
                scanner?.stopScan(this)
                scanner = null
            }
        }
    }

    fun connect(viewDependency: ViewDependency, deviceId: String): Observable<BleDevice> = TODO()

    fun stayConnected(viewDependency: ViewDependency, deviceId: String): Observable<BleDevice> = TODO()

    fun connectBackground(deviceId: String): Observable<BleDevice> = TODO()

    fun stayConnectedBackground(deviceId: String): Observable<BleDevice> = TODO()
}
