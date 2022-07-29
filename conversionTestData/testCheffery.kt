@file:SharedCode
package com.campchef

import com.lightningkite.khrysalis.*
import java.util.*

interface CampChefAnyCharacteristic {
    val wsIdentifier: Byte
    val uuid: UUID
}

sealed class CampChefCharacteristic<T>(
    final override val wsIdentifier: Byte
) : CampChefAnyCharacteristic {

    override val uuid: UUID = UUID.fromString(uuidPrefix + wsIdentifier.toString(16).padStart(2, '0'))

    companion object {
        val uuidPrefix = "7dbaefb0-6bc3-4b8c-9990-3509fb398a"
        val serviceUuid = UUID.fromString("7dbaefb0-6bc3-4b8c-9990-3509fb398a00")
        val characteristics: Map<Byte, CampChefAnyCharacteristic> by lazy {
            listOf<CampChefAnyCharacteristic>(
                Mode,
                DeviceInfoC,
                Transitioning,
                PelletLevel,
                LastFault,
                Chamber,
                Secret,
                WifiSsid,
                WifiPassword,
                WifiConnectionStatus,
                WifiIsScanning,
                WifiScanResultC,
                WifiRssi,
                StmFirmware,
                EspFirmware,
                UpdateEsp,
                UpdateStm,
                OtaProgress,
            ).plus((0..15).map { Probe(it.toByte()) })
                .associateBy { it.wsIdentifier }
        }
    }

    abstract class StringCharacteristic(wsIdentifier: Byte) : CampChefCharacteristic<String>(wsIdentifier) {
    }

    abstract class BooleanCharacteristic(wsIdentifier: Byte) : CampChefCharacteristic<Boolean>(wsIdentifier) {
    }

    object Unknown: CampChefCharacteristic<Unit>(wsIdentifier = 0x7f) {
    }

    object Mode : CampChefCharacteristic<String>(wsIdentifier = 0x00) {
    }

    object DeviceInfoC : CampChefCharacteristic<String>(wsIdentifier = 0x01) {
    }

    object Transitioning : BooleanCharacteristic(wsIdentifier = 0x02)

    object PelletLevel : CampChefCharacteristic<Byte>(wsIdentifier = 0x03) {
    }
    object LastFault : CampChefCharacteristic<String>(wsIdentifier = 0x04) {
    }

    object Chamber : CampChefCharacteristic<String>(wsIdentifier = 0x05) {
    }

    object Secret : CampChefCharacteristic<ByteArray>(wsIdentifier = 0x06) {
    }

    class Probe(val index: Byte) : CampChefCharacteristic<String>(wsIdentifier = (0x10 + index).toByte()) {
        init {
            if (index > 0xF) fatalError("Illegal probe index")
        }

    }

    object WifiSsid : StringCharacteristic(wsIdentifier = 0x20)
    object WifiPassword : StringCharacteristic(wsIdentifier = 0x21)
    object WifiConnectionStatus : BooleanCharacteristic(wsIdentifier = 0x22) {}
    object WifiIsScanning : BooleanCharacteristic(wsIdentifier = 0x23) {}
    object WifiScanResultC : CampChefCharacteristic<String>(wsIdentifier = 0x24) {
    }

    object WifiRssi : CampChefCharacteristic<Byte>(wsIdentifier = 0x25) {
    }

    object StmFirmware : StringCharacteristic(wsIdentifier = 0x30)
    object EspFirmware : StringCharacteristic(wsIdentifier = 0x31)
    object UpdateEsp : StringCharacteristic(wsIdentifier = 0x32)
    object UpdateStm : StringCharacteristic(wsIdentifier = 0x33)

    object OtaProgress : CampChefCharacteristic<Byte>(wsIdentifier = 0x34) {
    }
}

fun main() {
    CampChefCharacteristic.characteristics.keys.sorted().forEach {
        println(it)
    }
}
