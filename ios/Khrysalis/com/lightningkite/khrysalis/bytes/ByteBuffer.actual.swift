import Foundation

//--- Data.buffer()
public extension Data {
    func buffer() -> ByteBuffer {
        return ByteBuffer.wrap(self)
    }
}


//--- ByteBuffer.{
public class ByteBuffer {

    private var backingArray: [UInt8]
    private var currentIndex: Int = 0
    private var currentLimit: Int = 0
    private var currentMark: Int = 0

    private var currentEndianness: ByteOrder = .BIG_ENDIAN
    private let hostEndianness: ByteOrder = OSHostByteOrder() == OSLittleEndian ? .LITTLE_ENDIAN : .BIG_ENDIAN

    private init(array: [UInt8]){
        self.backingArray = array
    }
    private init(array: Data){
        self.backingArray = [UInt8](array)
    }
    private init(){
        self.backingArray = [UInt8]()
    }
    
    //--- ByteBuffer.put(ByteBuffer)
    public func put(_ src: ByteBuffer) -> ByteBuffer {
        self.append(contentsOf: src.backingArray)
        return self
    }
    
    //--- ByteBuffer.put(Data, Int, Int)
    public func put(_ src: Data, _ offset: Int32, _ length: Int32) -> ByteBuffer {
        self.append(contentsOf: [UInt8](src[Int(offset)..<Int(offset + length)]))
        return self
    }

    //--- ByteBuffer.put(Data)
    public func put(_ src: Data) -> ByteBuffer {
        self.append(contentsOf: [UInt8](src))
        return self
    }

    //--- ByteBuffer.hasArray()
    public func hasArray() -> Bool {
        return true
    }
    
    //--- ByteBuffer.array()
    public func array() -> [UInt8] {
        return self.backingArray
    }
    
    //--- ByteBuffer.data()
    public func data() -> Data {
        return Data(self.backingArray)
    }

    //--- ByteBuffer.getUtf8()
    func getUtf8() -> String {
        let len = Int(getShort())
        let result = String(bytes: self.backingArray[self.currentIndex..<(self.currentIndex + len)], encoding: .utf8)!
        self.currentIndex += len
        return result
    }

    //--- ByteBuffer.getUtf8(Int)
    func getUtf8(_ index: Int32) -> String {
        let startPosition = self.currentIndex
        self.currentIndex = Int(index)
        let result = getUtf8()
        self.currentIndex = startPosition
        return result
    }
    func getUtf8(index: Int32) -> String {
        return getUtf8(index)
    }

    //--- ByteBuffer.putUtf8(String)
    func putUtf8(_ string: String) -> ByteBuffer {
        let _ = putShort(Int16(string.count))
        self.append(contentsOf: Array(string.utf8))
        return self
    }
    func putUtf8(string: String) -> ByteBuffer {
        return putUtf8(string)
    }

    //--- ByteBuffer.putUtf8(Int, String)
    func putUtf8(_ index: Int32, _ string: String) -> ByteBuffer {
        let startPosition = self.currentLimit
        self.currentLimit = Int(index)
        let _ = putUtf8(string)
        self.currentLimit = startPosition
        return self
    }
    func putUtf8(index: Int32, string: String) -> ByteBuffer {
        return putUtf8(index, string)
    }

    //--- ByteBuffer.getSetSizeUtf8(Int)
    func getSetSizeUtf8(_ length: Int32) -> String {
        let result = String(bytes: self.backingArray[self.currentIndex..<(self.currentIndex + Int(length))], encoding: .utf8)!
        self.currentIndex += Int(length)
        return result
    }
    func getSetSizeUtf8(length: Int32) -> String {
        return getSetSizeUtf8(length)
    }


    //--- ByteBuffer.getSetSizeUtf8(Int, Int)
    func getSetSizeUtf8(_ length: Int32, _ index: Int32) -> String {
        let result = String(bytes: self.backingArray[Int(index)..<Int(index+length)], encoding: .utf8)!
        return result
    }
    func getSetSizeUtf8(length: Int32, index: Int32) -> String {
        return getSetSizeUtf8(length, index)
    }


    //--- ByteBuffer.putSetSizeUtf8(Int, String)
    func putSetSizeUtf8(_ length: Int32, _ string: String) -> ByteBuffer {
        let resized = string.prefix(Int(length)).padding(toLength: Int(length), withPad: "\0", startingAt: 0)
        self.append(contentsOf: Array(resized.utf8))
        return self
    }
    func putSetSizeUtf8(length: Int32, string: String) -> ByteBuffer {
        return putSetSizeUtf8(length, string)
    }


    //--- ByteBuffer.putSetSizeUtf8(Int, Int, String)
    func putSetSizeUtf8(_ length: Int32, _ index: Int32, _ string: String) -> ByteBuffer {
        let resized = string.prefix(Int(length)).padding(toLength: Int(length), withPad: "\0", startingAt: 0)
        self.overwrite(at: Int(index), array: Array(resized.utf8))
        return self
    }
    func putSetSizeUtf8(length: Int32, index: Int32, string: String) -> ByteBuffer {
        return putSetSizeUtf8(length, index, string)
    }

    //--- ByteBuffer.arrayOffset()
    public func arrayOffset() -> Int32 {
        return 0
    }
    
    //--- ByteBuffer.position(Int)
    public func position(_ newPosition: Int32) -> ByteBuffer {
        self.currentIndex = Int(newPosition)
        return self
    }

    //--- ByteBuffer.limit(Int)
    public func limit(_ newLimit: Int32) -> ByteBuffer {
        self.currentLimit = Int(newLimit)
        return self
    }

    //--- ByteBuffer.mark()
    public func mark() -> ByteBuffer {
        self.currentMark = self.currentIndex
        return self
    }

    //--- ByteBuffer.reset()
    public func reset() -> ByteBuffer {
        self.currentIndex = self.currentMark
        return self
    }

    //--- ByteBuffer.clear()
    public func clear() -> ByteBuffer {
        self.currentLimit = 0
        self.currentIndex = 0
        self.currentMark = -1
        return self
    }

    //--- ByteBuffer.flip()
    public func flip() -> ByteBuffer {
        self.currentLimit = self.currentIndex
        self.currentIndex = 0
        self.currentMark = -1
        return self
    }

    //--- ByteBuffer.rewind()
    public func rewind() -> ByteBuffer {
        self.currentMark = 0
        self.currentIndex = 0
        return self
    }

    //--- ByteBuffer.compact()
    public func compact() -> ByteBuffer {
        return self
    }

    //--- ByteBuffer.order()
    public func order() -> ByteOrder {
        return self.currentEndianness
    }

    //--- ByteBuffer.order(ByteOrder?)
    //--- ByteBuffer.order(ByteOrder)
    public func order(_ bo: ByteOrder) -> ByteBuffer {
        self.currentEndianness = bo
        return self
    }

    //--- ByteBuffer.get()
    public func get() -> Int8 {
        let result = self.backingArray[currentIndex]
        currentIndex += 1
        return Int8(result)
    }

    //--- ByteBuffer.put(Byte)
    public func put(_ b: Int8) -> ByteBuffer {
        if self.currentLimit < self.backingArray.count {
            self.backingArray[self.currentLimit] = UInt8(b)
            self.currentLimit += 1
        } else {
            self.backingArray.append(UInt8(b))
            self.currentLimit += 1
        }
        return self
    }

    //--- ByteBuffer.get(Int)
    public func get(_ i: Int32) -> Int8 {
        return Int8(self.backingArray[Int(i)])
    }

    //--- ByteBuffer.put(Int, Byte)
    public func put(_ i: Int32, _ b: Int8) -> ByteBuffer {
        self.backingArray[Int(i)] = UInt8(b)
        return self
    }

    //--- ByteBuffer.getShort()
    public func getShort() -> Int16 {
        return grab()
    }

    //--- ByteBuffer.putShort(Short)
    public func putShort(_ i: Int16) -> ByteBuffer {
        return append(value: i)
    }

    //--- ByteBuffer.getShort(Int)
    public func getShort(_ i: Int32) -> Int16 {
        return grab(at: Int(i))
    }

    //--- ByteBuffer.putShort(Int, Short)
    public func putShort(_ i: Int32, _ i1: Int16) -> ByteBuffer {
        return overwrite(at: Int(i), value: i1)
    }

    //--- ByteBuffer.getInt()
    public func getInt() -> Int32 {
        return grab()
    }

    //--- ByteBuffer.putInt(Int)
    public func putInt(_ i: Int32) -> ByteBuffer {
        return append(value: i)
    }

    //--- ByteBuffer.getInt(Int)
    public func getInt(_ i: Int32) -> Int32 {
        return grab(at: Int(i))
    }

    //--- ByteBuffer.putInt(Int, Int)
    public func putInt(_ i: Int32, _ i1: Int32) -> ByteBuffer {
        return overwrite(at: Int(i), value: i1)
    }

    //--- ByteBuffer.getLong()
    public func getLong() -> Int64 {
        return grab()
    }

    //--- ByteBuffer.putLong(Long)
    public func putLong(_ l: Int64) -> ByteBuffer {
        return append(value: l)
    }

    //--- ByteBuffer.getLong(Int)
    public func getLong(_ i: Int32) -> Int64 {
        return grab(at: Int(i))
    }

    //--- ByteBuffer.putLong(Int, Long)
    public func putLong(_ i: Int32, _ l: Int64) -> ByteBuffer {
        return overwrite(at: Int(i), value: l)
    }

    //--- ByteBuffer.getFloat()
    public func getFloat() -> Float {
        return Float(bitPattern: grab())
    }

    //--- ByteBuffer.putFloat(Float)
    public func putFloat(_ v: Float) -> ByteBuffer {
        return append(value: v.bitPattern)
    }

    //--- ByteBuffer.getFloat(Int)
    public func getFloat(_ i: Int32) -> Float {
        return Float(bitPattern: grab(at: Int(i)))
    }

    //--- ByteBuffer.putFloat(Int, Float)
    public func putFloat(_ i: Int32, _ v: Float) -> ByteBuffer {
        return overwrite(at: Int(i), value: v.bitPattern)
    }

    //--- ByteBuffer.getDouble()
    public func getDouble() -> Double {
        return Double(bitPattern: grab())
    }

    //--- ByteBuffer.putDouble(Double)
    public func putDouble(_ v: Double) -> ByteBuffer {
        return append(value: v.bitPattern)
    }

    //--- ByteBuffer.getDouble(Int)
    public func getDouble(_ i: Int32) -> Double {
        return Double(bitPattern: grab(at: Int(i)))
    }

    //--- ByteBuffer.putDouble(Int, Double)
    public func putDouble(_ i: Int32, _ v: Double) -> ByteBuffer {
        return overwrite(at: Int(i), value: v.bitPattern)
    }

    //--- ByteBuffer.Companion.{ (overwritten on flow generation)
    
    //--- ByteBuffer.Companion.allocateDirect(Int)
    public static func allocateDirect(_ capacity: Int32) -> ByteBuffer {
        return allocate(capacity)
    }

    //--- ByteBuffer.Companion.allocate(Int)
    public static func allocate(_ capacity: Int32) -> ByteBuffer {
        return ByteBuffer(array: [UInt8](repeating: 0, count: Int(capacity)))
    }

    //--- ByteBuffer.Companion.wrap(Data, Int, Int)
    public static func wrap(_ array: Data, _ offset: Int32, _ length: Int32) -> ByteBuffer {
        let buffer = ByteBuffer(array: array)
        buffer.currentIndex = Int(offset)
        buffer.currentLimit = Int(offset + length)
        return buffer
    }

    //--- ByteBuffer.Companion.wrap(Data)
    public static func wrap(_ array: Data) -> ByteBuffer {
        let buffer = ByteBuffer(array: array)
        buffer.currentIndex = 0
        buffer.currentLimit = Int(array.count)
        return buffer
    }

    //--- ByteBuffer.Companion.} (overwritten on flow generation)
    
    //--- Internal
    private func to<T>(_ value: T) -> [UInt8] {
        var value = value
        return withUnsafeBytes(of: &value, Array.init)
    }

    private func from<T>(_ value: [UInt8], _: T.Type) -> T {
        return value.withUnsafeBytes {
            $0.load(fromByteOffset: 0, as: T.self)
        }
    }

    private func grab<T: FixedWidthInteger>() -> T {
        let size = MemoryLayout<T>.size
        let result = from(Array(backingArray[currentIndex..<currentIndex + size]), T.self)
        currentIndex += size
        return currentEndianness == .LITTLE_ENDIAN ? result.littleEndian : result.bigEndian
    }
    private func grab<T: FixedWidthInteger>(at: Int) -> T {
        let result = from(Array(backingArray[at..<at + MemoryLayout<T>.size]), T.self)
        return currentEndianness == .LITTLE_ENDIAN ? result.littleEndian : result.bigEndian
    }
    private func append<T: FixedWidthInteger>(value: T) -> Self {
        if currentEndianness == .LITTLE_ENDIAN {
            append(contentsOf: to(value.littleEndian))
            return self
        }
        append(contentsOf: to(value.bigEndian))
        return self
    }
    private func append(contentsOf: [UInt8]) {
        let delta = (contentsOf.count + self.currentLimit) - self.backingArray.count
        if delta > 0 {
            self.backingArray.append(contentsOf: [UInt8](repeating: 0, count: delta))
        }
        let at = self.currentLimit
        backingArray[at ..< (at+contentsOf.count)] = contentsOf[0..<contentsOf.count]
        self.currentLimit += contentsOf.count
    }
    private func overwrite<T: FixedWidthInteger>(at: Int, value: T) -> Self {
        if currentEndianness == .LITTLE_ENDIAN {
            overwrite(at: at, array: to(value.littleEndian))
            return self
        }
        overwrite(at: at, array: to(value.bigEndian))
        return self
    }
    private func overwrite(at: Int, array: [UInt8]) {
        backingArray[at ..< (at+array.count)] = array[0..<array.count]
    }

    //--- ByteBuffer.} (overwritten on flow generation)
}

//--- ByteOrder.{
public enum ByteOrder {
    case BIG_ENDIAN
    case LITTLE_ENDIAN
    //--- ByteOrder.} (overwritten on flow generation)
}
