//Package: org.liftinggenerations.shared.views
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay


public var isMentorKey = "org.liftinggenerations.isMentor"

public func sessionStart(stack: ViewDataStack, session: Session) -> Void {
    if let session = session as? MenteeSession {
        stack.reset(viewData: SessionViewData(session: session, parentStack: stack))
    } else if let session = session as? MentorSession {
        stack.reset(viewData: MentorSessionViewData(session: session, parentStack: stack))
    }
}
public func sessionStart(_ stack: ViewDataStack, _ session: Session) -> Void {
    return sessionStart(stack: stack, session: session)
}
 
 

public func setSession(session: Session?) -> Void {
    Preferences.clear()
    if let session = session as? MentorSession {
        SecurePreferences.set(isMentorKey, true)
        SecurePreferences.set(Mentor.key, session.mentor)
        var auth = session.authorization
        if let auth = auth as? Token {
            SecurePreferences.set(Token.key, auth)
        }
    } else if let session = session as? MenteeSession {
        SecurePreferences.set(isMentorKey, false)
        SecurePreferences.set(Mentee.key, session.mentee)
        SecurePreferences.set(Mentor.key, session.myMentor)
        var auth = session.authorization
        if let auth = auth as? Token {
            SecurePreferences.set(Token.key, auth)
        }
    } else if session == nil {
        SecurePreferences.remove(isMentorKey)
        SecurePreferences.remove(Mentor.key)
        SecurePreferences.remove(Mentee.key)
        SecurePreferences.remove(Token.key)
    }
}
public func setSession(_ session: Session?) -> Void {
    return setSession(session: session)
}
 
 

public func getSession() -> Session?  {
    var isMentor: Bool?  = SecurePreferences.get(isMentorKey) ?? false
    var loadedToken: Token?  = SecurePreferences.get(Token.key)
    var loadedMentor: Mentor?  = SecurePreferences.get(Mentor.key)
    var loadedMentee: Mentee?  = SecurePreferences.get(Mentee.key)
    if let loadedToken = loadedToken {
        if let loadedMentor = loadedMentor, isMentor == true {
            return MentorSession(loadedToken, loadedMentor)
        } else if let loadedMentee = loadedMentee {
            return MenteeSession(loadedToken, loadedMentee, loadedMentor ?? Mentor())
        } else {
            return nil
        }
    } else {
        return nil
    }
}
 
