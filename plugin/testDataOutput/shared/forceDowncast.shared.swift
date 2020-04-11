//Package: com.test
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class APIOnline: APIInterface {
    
    public var baseUrl: String
    
    
    public func testWeakRef() -> Void {
        var x: Number = 3
        var downcasted = x as! Int32
        var downcasted2 = x as? Int32
        var downcastsInLambda = doThing{ () in 
            var downcasted = x as! Int32
            var downcasted2 = x as? Int32
            var downcastsInLambda = doThing{ () in 
                var downcasted = x as! Int32
                var downcasted2 = x as? Int32
            }
        }
        inLambda({ () in 
            var menteeSession = session as! MenteeSession
        })
    }
    
    override public func registerMentee(inviteToken: Token, mentee: Mentee, password: String, onResult: (Int32, MenteeSession?, String?) -> Void) -> Void {
        inLambda({ () in 
            var menteeSession = session as! MenteeSession
        })
    }
    override public func registerMentee(_ inviteToken: Token, _ mentee: Mentee, _ password: String, _ onResult: (Int32, MenteeSession?, String?) -> Void) -> Void {
        return registerMentee(inviteToken: inviteToken, mentee: mentee, password: password, onResult: onResult)
    }
    
    public init(baseUrl: String = "https://liftinggenerationsstaging.lightningkite.com/api") {
        self.baseUrl = baseUrl
    }
    convenience public init(_ baseUrl: String) {
        self.init(baseUrl: baseUrl)
    }
}
 
