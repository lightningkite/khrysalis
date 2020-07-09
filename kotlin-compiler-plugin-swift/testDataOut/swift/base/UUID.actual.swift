
//--- UUID.{
public extension UUID {
    
    //--- UUID.Companion.{
    
    //--- UUID.Companion.fromString(String)
    static func fromString(_ string: String) -> UUID {
        return UUID(uuidString: string)!
    }
    static func fromString(string: String) -> UUID {
        return fromString(string)
    }
    
    //--- UUID.Companion.randomUUID()
    static func randomUUID() -> UUID {
        return UUID()
    }
    
    //--- UUID.Companion.}
    
    //--- UUID.}
}
