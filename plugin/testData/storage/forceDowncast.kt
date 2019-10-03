package com.test

class APIOnline(val baseUrl: String = "https://liftinggenerationsstaging.lightningkite.com/api") : APIInterface {
    fun testWeakRef() {
        val x: Number = 3
        val downcasted = x as Int
        val downcasted2 = x as? Int

        val downcastsInLambda = doThing {

            val downcasted = x as Int
            val downcasted2 = x as? Int

            val downcastsInLambda = doThing {

                val downcasted = x as Int
                val downcasted2 = x as? Int
            }
        }

        inLambda({
            var menteeSession = session as MenteeSession
        })
    }

    override fun registerMentee(
        inviteToken: Token,
        mentee: Mentee,
        password: String, @escaping onResult: (Int, MenteeSession?, String?) -> Unit
    ) {

        inLambda({
            var menteeSession = session as MenteeSession
        })
    }
}
