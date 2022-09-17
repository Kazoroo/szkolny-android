/*
 * Copyright (c) Kuba Szczodrzyński 2019-9-20.
 */

package pl.szczodrzynski.edziennik.data.api

import pl.szczodrzynski.edziennik.data.api.edziennik.librus.login.LibrusLoginApi
import pl.szczodrzynski.edziennik.data.api.edziennik.librus.login.LibrusLoginMessages
import pl.szczodrzynski.edziennik.data.api.edziennik.librus.login.LibrusLoginPortal
import pl.szczodrzynski.edziennik.data.api.edziennik.librus.login.LibrusLoginSynergia
import pl.szczodrzynski.edziennik.data.api.edziennik.mobidziennik.login.MobidziennikLoginApi2
import pl.szczodrzynski.edziennik.data.api.edziennik.mobidziennik.login.MobidziennikLoginWeb
import pl.szczodrzynski.edziennik.data.api.edziennik.podlasie.login.PodlasieLoginApi
import pl.szczodrzynski.edziennik.data.api.edziennik.template.login.TemplateLoginApi
import pl.szczodrzynski.edziennik.data.api.edziennik.template.login.TemplateLoginWeb
import pl.szczodrzynski.edziennik.data.api.edziennik.vulcan.login.VulcanLoginHebe
import pl.szczodrzynski.edziennik.data.api.edziennik.vulcan.login.VulcanLoginWebMain
import pl.szczodrzynski.edziennik.data.api.models.LoginMethod

// librus
// mobidziennik
// idziennik [*]
// vulcan
// mobireg

const val SYNERGIA_API_ENABLED = false

// the graveyard
const val LOGIN_TYPE_IDZIENNIK = 3
const val LOGIN_TYPE_EDUDZIENNIK = 5

const val LOGIN_TYPE_TEMPLATE = 21

// LOGIN MODES
const val LOGIN_MODE_TEMPLATE_WEB = 0

// LOGIN METHODS
const val LOGIN_METHOD_NOT_NEEDED = -1
const val LOGIN_METHOD_TEMPLATE_WEB = 100
const val LOGIN_METHOD_TEMPLATE_API = 200

const val LOGIN_TYPE_LIBRUS = 2
const val LOGIN_MODE_LIBRUS_EMAIL = 0
const val LOGIN_MODE_LIBRUS_SYNERGIA = 1
const val LOGIN_MODE_LIBRUS_JST = 2
const val LOGIN_METHOD_LIBRUS_PORTAL = 100
const val LOGIN_METHOD_LIBRUS_API = 200
const val LOGIN_METHOD_LIBRUS_SYNERGIA = 300
const val LOGIN_METHOD_LIBRUS_MESSAGES = 400
val librusLoginMethods = listOf(
        LoginMethod(LOGIN_TYPE_LIBRUS, LOGIN_METHOD_LIBRUS_PORTAL, LibrusLoginPortal::class.java)
                .withIsPossible { _, loginStore ->
                    loginStore.mode == LOGIN_MODE_LIBRUS_EMAIL
                }
                .withRequiredLoginMethod { _, _ -> LOGIN_METHOD_NOT_NEEDED },

        LoginMethod(LOGIN_TYPE_LIBRUS, LOGIN_METHOD_LIBRUS_API, LibrusLoginApi::class.java)
                .withIsPossible { _, loginStore ->
                    loginStore.mode != LOGIN_MODE_LIBRUS_SYNERGIA || SYNERGIA_API_ENABLED
                }
                .withRequiredLoginMethod { _, loginStore ->
                    if (loginStore.mode == LOGIN_MODE_LIBRUS_EMAIL) LOGIN_METHOD_LIBRUS_PORTAL else LOGIN_METHOD_NOT_NEEDED
                },

        LoginMethod(LOGIN_TYPE_LIBRUS, LOGIN_METHOD_LIBRUS_SYNERGIA, LibrusLoginSynergia::class.java)
                .withIsPossible { _, loginStore -> !loginStore.hasLoginData("fakeLogin") }
                .withRequiredLoginMethod { profile, _ ->
                    if (profile?.hasStudentData("accountPassword") == false || true) LOGIN_METHOD_LIBRUS_API else LOGIN_METHOD_NOT_NEEDED
                },

        LoginMethod(LOGIN_TYPE_LIBRUS, LOGIN_METHOD_LIBRUS_MESSAGES, LibrusLoginMessages::class.java)
                .withIsPossible { _, loginStore -> !loginStore.hasLoginData("fakeLogin") }
                .withRequiredLoginMethod { profile, _ ->
                    if (profile?.hasStudentData("accountPassword") == false || true) LOGIN_METHOD_LIBRUS_SYNERGIA else LOGIN_METHOD_NOT_NEEDED
                }
)

const val LOGIN_TYPE_MOBIDZIENNIK = 1
const val LOGIN_MODE_MOBIDZIENNIK_WEB = 0
const val LOGIN_METHOD_MOBIDZIENNIK_WEB = 100
const val LOGIN_METHOD_MOBIDZIENNIK_API2 = 300
val mobidziennikLoginMethods = listOf(
        LoginMethod(LOGIN_TYPE_MOBIDZIENNIK, LOGIN_METHOD_MOBIDZIENNIK_WEB, MobidziennikLoginWeb::class.java)
                .withIsPossible { _, _ -> true }
                .withRequiredLoginMethod { _, _ -> LOGIN_METHOD_NOT_NEEDED },

        LoginMethod(LOGIN_TYPE_MOBIDZIENNIK, LOGIN_METHOD_MOBIDZIENNIK_API2, MobidziennikLoginApi2::class.java)
                .withIsPossible { profile, _ -> profile?.getStudentData("email", null) != null }
                .withRequiredLoginMethod { _, _ -> LOGIN_METHOD_NOT_NEEDED }
)

const val LOGIN_TYPE_VULCAN = 4
const val LOGIN_MODE_VULCAN_API = 0
const val LOGIN_MODE_VULCAN_WEB = 1
const val LOGIN_MODE_VULCAN_HEBE = 2
const val LOGIN_METHOD_VULCAN_WEB_MAIN = 100
const val LOGIN_METHOD_VULCAN_WEB_NEW = 200
const val LOGIN_METHOD_VULCAN_WEB_OLD = 300
const val LOGIN_METHOD_VULCAN_WEB_MESSAGES = 400
const val LOGIN_METHOD_VULCAN_HEBE = 600
val vulcanLoginMethods = listOf(
        LoginMethod(LOGIN_TYPE_VULCAN, LOGIN_METHOD_VULCAN_WEB_MAIN, VulcanLoginWebMain::class.java)
                .withIsPossible { _, loginStore -> loginStore.hasLoginData("webHost") }
                .withRequiredLoginMethod { _, _ -> LOGIN_METHOD_NOT_NEEDED },

        /*LoginMethod(LOGIN_TYPE_VULCAN, LOGIN_METHOD_VULCAN_WEB_NEW, VulcanLoginWebNew::class.java)
                .withIsPossible { _, _ -> false }
                .withRequiredLoginMethod { _, _ -> LOGIN_METHOD_VULCAN_WEB_MAIN },

        LoginMethod(LOGIN_TYPE_VULCAN, LOGIN_METHOD_VULCAN_WEB_OLD, VulcanLoginWebOld::class.java)
                .withIsPossible { _, _ -> false }
                .withRequiredLoginMethod { _, _ -> LOGIN_METHOD_VULCAN_WEB_MAIN },*/

        LoginMethod(LOGIN_TYPE_VULCAN, LOGIN_METHOD_VULCAN_HEBE, VulcanLoginHebe::class.java)
                .withIsPossible { _, loginStore ->
                        loginStore.mode != LOGIN_MODE_VULCAN_API
                }
                .withRequiredLoginMethod { _, _ -> LOGIN_METHOD_NOT_NEEDED }
)

const val LOGIN_TYPE_PODLASIE = 6
const val LOGIN_MODE_PODLASIE_API = 0
const val LOGIN_METHOD_PODLASIE_API = 100
val podlasieLoginMethods = listOf(
        LoginMethod(LOGIN_TYPE_PODLASIE, LOGIN_METHOD_PODLASIE_API, PodlasieLoginApi::class.java)
                .withIsPossible { _, _ -> true }
                .withRequiredLoginMethod { _, _ -> LOGIN_METHOD_NOT_NEEDED }
)

val templateLoginMethods = listOf(
        LoginMethod(LOGIN_TYPE_TEMPLATE, LOGIN_METHOD_TEMPLATE_WEB, TemplateLoginWeb::class.java)
                .withIsPossible { _, _ -> true }
                .withRequiredLoginMethod { _, _ -> LOGIN_METHOD_NOT_NEEDED },

        LoginMethod(LOGIN_TYPE_TEMPLATE, LOGIN_METHOD_TEMPLATE_API, TemplateLoginApi::class.java)
                .withIsPossible { _, _ -> true }
                .withRequiredLoginMethod { _, _ -> LOGIN_METHOD_TEMPLATE_WEB }
)
