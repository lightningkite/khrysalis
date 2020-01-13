package com.lightningkite.kwift.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.lightningkite.kwift.Failable
import com.lightningkite.kwift.PlatformSpecific
import com.lightningkite.kwift.async.DelayedResultFunction
import com.lightningkite.kwift.net.HttpClient
import com.lightningkite.kwift.views.ViewDependency
import com.lightningkite.kwift.views.android.startIntent
import java.io.Closeable

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
object Ble {
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

    fun serve(
        viewDependency: ViewDependency,
        services: List<BleServiceServer>
    ): Closeable = TODO()

    fun scan(
        viewDependency: ViewDependency,
        withServices: Collection<String>,
        onDeviceFound: (info: BleDeviceInfo) -> Unit
    ): Closeable {
        return object : Closeable, ScanCallback() {

            var scanner: BluetoothLeScanner? = null

            init {
                activateBluetoothDialog(
                    dependency = viewDependency,
                    onPermissionRejected = { },
                    onBluetooth = {
                        scanner = it.bluetoothLeScanner
                        it.bluetoothLeScanner.startScan(
                            withServices.map {
                                ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(it)).build()
                            },
                            ScanSettings.Builder().build(),
                            this
                        )
                    }
                )
            }

            override fun onScanResult(callbackType: Int, result: ScanResult) {
                onDeviceFound.invoke(
                    BleDeviceInfo(
                        result.device.address,
                        result.device.name ?: ""
                    )
                )
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("Ble", "Scan failed with code $errorCode.")
            }

            override fun close() {
                scanner?.stopScan(this)
                scanner = null
            }
        }
    }

    fun connect(viewDependency: ViewDependency, deviceId: String): DelayedResultFunction<BleDevice> =
        DelayedResultFunction<BleDevice> { callback ->
            activateBluetoothDialog(
                dependency = viewDependency,
                onPermissionRejected = { callback(Failable.failure("Permission rejected.")) },
                onBluetooth = {
                    val context = viewDependency.context.applicationContext
                    val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceId)
                    val bleDevice = BleDeviceImpl(context, device, false)
                    bleDevice.connected.onChange.add { connected ->
                        if (connected) {
                            callback.invoke(Failable.success(bleDevice))
                            return@add true
                        }
                        return@add false
                    }
                }
            )
        }

    fun stayConnected(viewDependency: ViewDependency, deviceId: String): DelayedResultFunction<BleDevice> =
        DelayedResultFunction<BleDevice> { callback ->
            activateBluetoothDialog(
                dependency = viewDependency,
                onPermissionRejected = { callback(Failable.failure("Permission rejected.")) },
                onBluetooth = {
                    val context = viewDependency.context.applicationContext
                    val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceId)
                    val bleDevice = BleDeviceImpl(context, device, true)
                    bleDevice.connected.onChange.add { connected ->
                        if (connected) {
                            callback.invoke(Failable.success(bleDevice))
                            return@add true
                        }
                        return@add false
                    }
                }
            )
        }

    fun connectBackground(deviceId: String): DelayedResultFunction<BleDevice> =
        DelayedResultFunction<BleDevice> { callback ->
            val context = HttpClient.appContext
            val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceId)
            val bleDevice = BleDeviceImpl(context, device, false)
            bleDevice.connected.onChange.add { connected ->
                if (connected) {
                    callback.invoke(Failable.success(bleDevice))
                    return@add true
                }
                return@add false
            }
        }

    fun stayConnectedBackground(deviceId: String): DelayedResultFunction<BleDevice> =
        DelayedResultFunction<BleDevice> { callback ->
            val context = HttpClient.appContext
            val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceId)
            val bleDevice = BleDeviceImpl(context, device, true)
            bleDevice.connected.onChange.add { connected ->
                if (connected) {
                    callback.invoke(Failable.success(bleDevice))
                    return@add true
                }
                return@add false
            }
        }
}
