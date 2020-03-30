package org.liftinggenerations.shared.views

import com.lightningkite.khrysalis.actuals.Preferences
import com.lightningkite.khrysalis.actuals.SecurePreferences
import com.lightningkite.khrysalis.shared.ViewDataStack
import org.liftinggenerations.shared.models.*

val isMentorKey = "org.liftinggenerations.isMentor"

fun sessionStart(stack: ViewDataStack, session: Session) {
    if (session is MenteeSession) {
        stack.reset(viewData = SessionViewData(session = session, parentStack = stack))
    } else if (session is MentorSession) {
        stack.reset(viewData = MentorSessionViewData(session = session, parentStack = stack))
    }
}

fun setSession(session: Session?) {
    Preferences.clear()
    if (session is MentorSession) {
        SecurePreferences.set(isMentorKey, true)
        SecurePreferences.set(Mentor.key, session.mentor)
        val auth = session.authorization
        if(auth is Token){
            SecurePreferences.set(Token.key, auth)
        }
    } else if (session is MenteeSession) {
        SecurePreferences.set(isMentorKey, false)
        SecurePreferences.set(Mentee.key, session.mentee)
        SecurePreferences.set(Mentor.key, session.myMentor)
        val auth = session.authorization
        if(auth is Token){
            SecurePreferences.set(Token.key, auth)
        }
    } else if (session == null) {
        SecurePreferences.remove(isMentorKey)
        SecurePreferences.remove(Mentor.key)
        SecurePreferences.remove(Mentee.key)
        SecurePreferences.remove(Token.key)
    }
}

fun getSession(): Session? {
    val isMentor: Boolean? = SecurePreferences.get(isMentorKey) ?: false
    val loadedToken: Token? = SecurePreferences.get(Token.key)
    val loadedMentor: Mentor? = SecurePreferences.get(Mentor.key)
    val loadedMentee: Mentee? = SecurePreferences.get(Mentee.key)
    if (loadedToken != null) {
        if (loadedMentor != null && isMentor == true) {
            return MentorSession(loadedToken, loadedMentor)
        } else if (loadedMentee != null) {
            return MenteeSession(loadedToken, loadedMentee, loadedMentor ?: Mentor())
        } else {
            return null
        }
    } else {
        return null
    }
}
