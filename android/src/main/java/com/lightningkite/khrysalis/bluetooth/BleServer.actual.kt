package com.lightningkite.khrysalis.bluetooth


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
