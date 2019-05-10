///Kotlin Only
@file:Suppress("ConvertToStringTemplate", "RemoveExplicitTypeArguments")
///End Kotlin Only

package org.liftinggenerations.shared.api

import com.lightningkite.kwift.actuals.*
import org.liftinggenerations.shared.models.*
import org.liftinggenerations.shared.models.Currency
import java.util.*


//Fix
class APIOnline(val baseUrl: String = "https://liftinggenerationsstaging.lightningkite.com/api") : APIInterface {

    override fun registerMentee(inviteToken: Token, mentee: Mentee, password: String, @escaping onResult: (Int, MenteeSession?, String?) -> Unit) {
        val menteeSession = session as MenteeSession
        inLambda({

            val menteeSession = session as MenteeSession
        })
        HttpClient.call(
            url = baseUrl + "/mentee",
            method = HttpClient.POST,
            headers = inviteToken.headers(),
            body = APIMentee.fromNormal(mentee, password),
            onResult = { code, result: Empty?, error ->
                if (error != null) {
                    onResult(code, null, error)
                } else if (result != null) {
                    this.login(mentee.email, password) { code, session, password ->
                        val menteeSession = session as MenteeSession
                        onResult(code, menteeSession, password)
                    }
                }
            }
        )
    }

}
