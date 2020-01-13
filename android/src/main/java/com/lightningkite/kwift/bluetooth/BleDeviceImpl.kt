package com.lightningkite.kwift.bluetooth

import android.annotation.TargetApi
import android.bluetooth.*
import android.content.Context
import android.os.Build
import com.lightningkite.kwift.Failable
import com.lightningkite.kwift.async.DRF
import com.lightningkite.kwift.observables.EnablingEvent
import com.lightningkite.kwift.observables.Event
import com.lightningkite.kwift.observables.ObservableProperty
import com.lightningkite.kwift.observables.StandardObservableProperty
import com.lightningkite.kwift.post
import java.util.*

class BleDeviceImpl(val context: Context, val device: BluetoothDevice, val stayConnected: Boolean = false): BleDevice {
    private val privateConnected = StandardObservableProperty<Boolean>(false)
    override val connected: ObservableProperty<Boolean> get() = privateConnected
    private val privateRssi = StandardObservableProperty<Int>(-999)
    override val rssi: ObservableProperty<Int> get() = privateRssi
    private val privateMtu = StandardObservableProperty<Int>(23)
    override val mtu: ObservableProperty<Int> get() = privateMtu

    private var servicesDiscovered = false

    private val androidListener = BluetoothGattListener().apply {
        onConnectionStateChange += { gatt, _, newState ->
            post {
                when (newState) {
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        privateConnected.value = false
                    }
                    BluetoothProfile.STATE_CONNECTED -> {
                        if (servicesDiscovered) {
                            privateConnected.value = true
                        } else {
                            gatt!!.discoverServices()
                        }
                    }
                }
            }
        }
        onServicesDiscovered += { _, _ ->
            post {
                privateConnected.value = true
            }
        }
        onReadRemoteRssi += { _, rssi, _ ->
            post {
                privateRssi.value = rssi
            }
        }
        onMtuChanged += { _, mtu, _ ->
            post {
                privateMtu.value = mtu
            }
        }
    }
    private val gatt = device.connectGatt(context, stayConnected, androidListener)
    private val queue = DRFQueue()

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun requestMtu(mtu: Int): DRF<Unit> = DRF { resultCallback ->
        queue.enqueue("request mtu $mtu", DRF<Unit> { complete ->
            val listenerObject = object : (BluetoothGatt?, Int, Int) -> Unit {
                init {
                    androidListener.onMtuChanged.add(this)
                }

                override fun invoke(p1: BluetoothGatt?, p2: Int, p3: Int) {
                    post {
                        complete.invoke(Failable.success(Unit))
                        resultCallback.invoke(Failable.success(Unit))
                        androidListener.onMtuChanged.remove(this)
                    }
                }
            }
            if (!gatt.requestMtu(mtu)) {
                androidListener.onMtuChanged.remove(listenerObject)
                val exception = Exception("requestMtu() failed.")
                complete.invoke(
                    Failable.failure(
                        exception.message ?: "${exception::class.java.simpleName} occurred without message"
                    )
                )
                resultCallback.invoke(
                    Failable.failure(
                        exception.message ?: "${exception::class.java.simpleName} occurred without message"
                    )
                )
            }
        })
    }

    override fun read(serviceId: UUID, characteristicId: UUID): DRF<ByteArray> = DRF { resultCallback ->
        queue.enqueue("read $serviceId/$characteristicId", DRF<ByteArray> { complete ->
            gatt.getService(serviceId)?.getCharacteristic(characteristicId)?.let { actualChar ->
                val listenerObject = object : (BluetoothGatt?, BluetoothGattCharacteristic?, Int) -> Unit {
                    init {
                        androidListener.onCharacteristicRead.add(this)
                    }

                    override fun invoke(p1: BluetoothGatt?, p2: BluetoothGattCharacteristic?, p3: Int) {
                        if (p2 == null || p2.uuid != actualChar.uuid) return
                        post {
                            complete.invoke(Failable.success(p2.value))
                            resultCallback.invoke(Failable.success(p2.value))
                            androidListener.onCharacteristicRead.remove(this)
                        }
                    }
                }
                if (!gatt.readCharacteristic(actualChar)) {
                    androidListener.onCharacteristicRead.remove(listenerObject)
                    val exception = Exception("readCharacteristic() failed.")
                    complete.invoke(
                        Failable.failure(
                            exception.message ?: "${exception::class.java.simpleName} occurred without message"
                        )
                    )
                    resultCallback.invoke(
                        Failable.failure(
                            exception.message ?: "${exception::class.java.simpleName} occurred without message"
                        )
                    )
                }
            } ?: run {
                val exception = Exception("Characteristic $serviceId-$characteristicId not found.")
                complete.invoke(
                    Failable.failure(
                        exception.message ?: "${exception::class.java.simpleName} occurred without message"
                    )
                )
                resultCallback.invoke(
                    Failable.failure(
                        exception.message ?: "${exception::class.java.simpleName} occurred without message"
                    )
                )
            }
        })
    }

    override fun write(serviceId: UUID, characteristicId: UUID, value: ByteArray): DRF<Unit> = DRF { resultCallback ->
        queue.enqueue("write $serviceId/$characteristicId", DRF<Unit> { complete ->
            gatt.getService(serviceId)?.getCharacteristic(characteristicId)?.let { actualChar ->
                actualChar.value = value
                val listenerObject = object : (BluetoothGatt?, BluetoothGattCharacteristic?, Int) -> Unit {
                    init {
                        androidListener.onCharacteristicWrite.add(this)
                    }

                    override fun invoke(p1: BluetoothGatt?, p2: BluetoothGattCharacteristic?, p3: Int) {
                        if (p2 == null || p2.uuid != actualChar.uuid) return
                        post {
                            complete.invoke(Failable.success(Unit))
                            resultCallback.invoke(Failable.success(Unit))
                            androidListener.onCharacteristicWrite.remove(this)
                        }
                    }
                }
                if (!gatt.writeCharacteristic(actualChar)) {
                    androidListener.onCharacteristicWrite.remove(listenerObject)
                    val exception = Exception("writeCharacteristic() failed.")
                    complete.invoke(
                        Failable.failure(
                            exception.message ?: "${exception::class.java.simpleName} occurred without message"
                        )
                    )
                    resultCallback.invoke(
                        Failable.failure(
                            exception.message ?: "${exception::class.java.simpleName} occurred without message"
                        )
                    )
                }
            } ?: run {
                val exception = Exception("Characteristic $serviceId-$characteristicId not found.")
                complete.invoke(
                    Failable.failure(
                        exception.message ?: "${exception::class.java.simpleName} occurred without message"
                    )
                )
                resultCallback.invoke(
                    Failable.failure(
                        exception.message ?: "${exception::class.java.simpleName} occurred without message"
                    )
                )
            }
        })
    }

    override fun readDescriptor(serviceId: UUID, characteristicId: UUID, descriptorId: UUID): DRF<ByteArray> =
        DRF { resultCallback ->
            queue.enqueue("readDescriptor $serviceId/$characteristicId/$descriptorId", DRF<ByteArray> { complete ->
                gatt.getService(serviceId)
                    ?.getCharacteristic(characteristicId)
                    ?.getDescriptor(descriptorId)
                    ?.let { actualDesc ->
                        val listenerObject = object : (BluetoothGatt?, BluetoothGattDescriptor?, Int) -> Unit {
                            init {
                                androidListener.onDescriptorRead.add(this)
                            }

                            override fun invoke(p1: BluetoothGatt?, p2: BluetoothGattDescriptor?, p3: Int) {
                                if (p2 == null || p2.uuid != actualDesc.uuid) return
                                post {
                                    complete.invoke(Failable.success(p2.value))
                                    resultCallback.invoke(Failable.success(p2.value))
                                    androidListener.onDescriptorRead.remove(this)
                                }
                            }
                        }
                        if (!gatt.readDescriptor(actualDesc)) {
                            androidListener.onDescriptorRead.remove(listenerObject)
                            val exception = Exception("readDescriptor() failed.")
                            complete.invoke(
                                Failable.failure(
                                    exception.message ?: "${exception::class.java.simpleName} occurred without message"
                                )
                            )
                            resultCallback.invoke(
                                Failable.failure(
                                    exception.message ?: "${exception::class.java.simpleName} occurred without message"
                                )
                            )
                        }
                    } ?: run {
                    val exception = Exception("Descriptor $serviceId-$characteristicId-$descriptorId not found.")
                    complete.invoke(
                        Failable.failure(
                            exception.message ?: "${exception::class.java.simpleName} occurred without message"
                        )
                    )
                    resultCallback.invoke(
                        Failable.failure(
                            exception.message ?: "${exception::class.java.simpleName} occurred without message"
                        )
                    )
                }
            })
        }

    override fun writeDescriptor(
        serviceId: UUID,
        characteristicId: UUID,
        descriptorId: UUID,
        value: ByteArray
    ): DRF<Unit> = DRF { resultCallback ->
        queue.enqueue("writeDescriptor $serviceId/$characteristicId/$descriptorId", DRF<Unit> { complete ->
            gatt.getService(serviceId)
                ?.getCharacteristic(characteristicId)
                ?.getDescriptor(descriptorId)
                ?.let { actualDesc ->
                    actualDesc.value = value
                    val listenerObject = object : (BluetoothGatt?, BluetoothGattDescriptor?, Int) -> Unit {
                        init {
                            androidListener.onDescriptorWrite.add(this)
                        }

                        override fun invoke(p1: BluetoothGatt?, p2: BluetoothGattDescriptor?, p3: Int) {
                            if (p2 == null || p2.uuid != actualDesc.uuid) return
                            post {
                                complete.invoke(Failable.success(Unit))
                                resultCallback.invoke(Failable.success(Unit))
                                androidListener.onDescriptorWrite.remove(this)
                            }
                        }
                    }
                    if (!gatt.writeDescriptor(actualDesc)) {
                        androidListener.onDescriptorWrite.remove(listenerObject)
                        val exception = Exception("writeDescriptor() failed.")
                        complete.invoke(
                            Failable.failure(
                                exception.message ?: "${exception::class.java.simpleName} occurred without message"
                            )
                        )
                        resultCallback.invoke(
                            Failable.failure(
                                exception.message ?: "${exception::class.java.simpleName} occurred without message"
                            )
                        )
                    }
                }
                ?: run {
                    val exception = Exception("Descriptor $serviceId-$characteristicId-$descriptorId not found.")
                    complete.invoke(
                        Failable.failure(
                            exception.message ?: "${exception::class.java.simpleName} occurred without message"
                        )
                    )
                    resultCallback.invoke(
                        Failable.failure(
                            exception.message ?: "${exception::class.java.simpleName} occurred without message"
                        )
                    )
                }
        })
    }

    private class CharacteristicSubEvent(
        val self: BleDeviceImpl,
        val serviceId: UUID,
        val characteristicId: UUID,
        val actualChar: BluetoothGattCharacteristic,
        val notificationDescriptorId: UUID?
    ) : EnablingEvent<ByteArray>() {
        val onCharChange = { gatt: BluetoothGatt?, char: BluetoothGattCharacteristic? ->
            char?.value?.let {
                this.invokeAll(it)
            }
            Unit
        }

        override fun enable() {
            self.gatt.setCharacteristicNotification(actualChar, true)
            if (notificationDescriptorId != null) {
                self.writeDescriptor(
                    serviceId,
                    characteristicId,
                    notificationDescriptorId,
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                ).invoke {
                    println("Started listening to $characteristicId successfully.")
                }
            }
            self.androidListener.onCharacteristicChanged.add(this.onCharChange)
        }

        override fun disable() {
            self.gatt.setCharacteristicNotification(actualChar, false)
            if (notificationDescriptorId != null) {
                self.writeDescriptor(
                    serviceId,
                    characteristicId,
                    notificationDescriptorId,
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                ).invoke {
                    println("Stopped listening to $characteristicId successfully.")
                }
            }
            self.androidListener.onCharacteristicChanged.remove(onCharChange)
        }

    }

    override fun subscribe(
        serviceId: UUID,
        characteristicId: UUID,
        notificationDescriptorId: UUID?
    ): Event<ByteArray> {
        gatt.getService(serviceId)?.getCharacteristic(characteristicId)?.let { actualChar ->
            return CharacteristicSubEvent(
                self = this,
                actualChar = actualChar,
                serviceId = serviceId,
                characteristicId = characteristicId,
                notificationDescriptorId = notificationDescriptorId
            )
        } ?: run {
            throw IllegalArgumentException("No such characteristic: $serviceId/$characteristicId")
        }
    }

    override fun close() {
        androidListener.close()
        gatt.close()
    }
}
