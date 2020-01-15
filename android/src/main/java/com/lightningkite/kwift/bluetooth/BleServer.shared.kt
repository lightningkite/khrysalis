package com.lightningkite.kwift.bluetooth

import com.lightningkite.kwift.observables.*
import java.util.*

//Client implementation

interface BleCharacteristicServer {
    val serviceUuid: UUID
    val uuid: UUID
    val properties: Properties

    data class Properties(
        val broadcast: Boolean = false,
        val read: Boolean = false,
        val writeWithoutResponse: Boolean = false,
        val write: Boolean = false,
        val notify: Boolean = false,
        val indicate: Boolean = false,
        val authenticatedSignedWrites: Boolean = false,
        val extendedProperties: Boolean = false,
        val notifyEncryptionRequired: Boolean = false,
        val indicateEncryptionRequired: Boolean = false,
        val writeEncryptionRequired: Boolean = false
    )

    fun onSubscribe(from: BleClient)
    fun onUnsubscribe(from: BleClient)
    fun onRead(from: BleClient, request: RequestId)
    fun onWrite(from: BleClient, request: RequestId, value: ByteArray)

    fun notify(client: BleClient, value: ByteArray) = client.notify(serviceUuid, uuid, value)
    fun indicate(client: BleClient, value: ByteArray) = client.indicate(serviceUuid, uuid, value)
}

class PropertyBleCharacteristicServer(
    override val serviceUuid: UUID,
    override val uuid: UUID,
    value: ByteArray,
    override val properties: BleCharacteristicServer.Properties = BleCharacteristicServer.Properties(read = true, write = true, notify = true)
): MutableObservableProperty<ByteArray>(), BleCharacteristicServer {

    var underlyingValue: ByteArray = value
    override var value: ByteArray
        get() = underlyingValue
        set(value) {
            underlyingValue = value
            update()
        }
    val subscribers: HashMap<String, BleClient> = HashMap()

    override fun onSubscribe(from: BleClient) {
        subscribers[from.info.id] = from
    }

    override fun onUnsubscribe(from: BleClient) {
        subscribers.remove(from.info.id)
    }

    override fun onRead(from: BleClient, request: RequestId) {
        from.respond(request, value, BleResponseStatus.success)
    }

    override fun onWrite(from: BleClient, request: RequestId, value: ByteArray) {
        this.value = value
        from.respond(request, value, BleResponseStatus.success)
    }

    override fun update() {
        val value = underlyingValue
        underlyingEvent.invokeAll(value)
        for(sub in subscribers) {
            indicate(sub.value, value)
        }
    }

    val underlyingEvent = ForceMainThreadEvent<ByteArray>()
    override val onChange: Event<ByteArray> get() = underlyingEvent
}

//My implementation

interface BleServer: Closeable {
    val clients: ObservableProperty<Map<String, BleClient>>
    val characteristics: Map<UUID, Map<UUID, BleCharacteristicServer>>
}

interface BleClient {
    val info: BleDeviceInfo
    val connected: Boolean
    fun respond(request: RequestId, data: ByteArray, status: BleResponseStatus)
    /** Does not require a confirmation from the device. **/
    fun notify(service: UUID, characteristic: UUID, value: ByteArray)
    /** Requires a confirmation from the device. **/
    fun indicate(service: UUID, characteristic: UUID, value: ByteArray)
}

//Actual
typealias RequestId = Int
@Suppress("EnumEntryName")
enum class BleResponseStatus(val value: Int) { //CBATTError.Code in iOS
    success(0),
    invalidHandle(1),
    readNotPermitted(2),
    writeNotPermitted(3),
    invalidPdu(4),
    insufficientAuthentication(5),
    requestNotSupported(6),
    invalidOffset(7),
    insufficientAuthorization(8),
    prepareQueueFull(9),
    attributeNotFound(10),
    attributeNotLong(11),
    insufficientEncryptionKeySize(12),
    invalidAttributeValueLength(13),
    unlikelyError(14),
    insufficientEncryption(15),
    unsupportedGroupType(16),
    insufficientResources(17)
}
