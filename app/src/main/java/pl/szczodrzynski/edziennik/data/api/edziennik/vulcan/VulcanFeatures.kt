/*
 * Copyright (c) Kuba Szczodrzyński 2019-10-6. 
 */

package pl.szczodrzynski.edziennik.data.api.edziennik.vulcan

import pl.szczodrzynski.edziennik.data.api.*
import pl.szczodrzynski.edziennik.data.api.models.Feature

const val ENDPOINT_VULCAN_API_UPDATE_SEMESTER     = 1000
const val ENDPOINT_VULCAN_API_PUSH_CONFIG         = 1005
const val ENDPOINT_VULCAN_API_DICTIONARIES        = 1010
const val ENDPOINT_VULCAN_API_TIMETABLE           = 1020
const val ENDPOINT_VULCAN_API_EVENTS = 1030
const val ENDPOINT_VULCAN_API_GRADES              = 1040
const val ENDPOINT_VULCAN_API_GRADES_SUMMARY      = 1050
const val ENDPOINT_VULCAN_API_HOMEWORK            = 1060
const val ENDPOINT_VULCAN_API_NOTICES             = 1070
const val ENDPOINT_VULCAN_API_ATTENDANCE          = 1080
const val ENDPOINT_VULCAN_API_MESSAGES_INBOX      = 1090
const val ENDPOINT_VULCAN_API_MESSAGES_SENT       = 1100
const val ENDPOINT_VULCAN_WEB_LUCKY_NUMBERS       = 2010
const val ENDPOINT_VULCAN_HEBE_MAIN               = 3000
const val ENDPOINT_VULCAN_HEBE_TIMETABLE          = 3020
const val ENDPOINT_VULCAN_HEBE_EXAMS              = 3030
const val ENDPOINT_VULCAN_HEBE_GRADES             = 3040
const val ENDPOINT_VULCAN_HEBE_HOMEWORK           = 3060

val VulcanFeatures = listOf(
        // timetable
        Feature(LOGIN_TYPE_VULCAN, FEATURE_TIMETABLE, listOf(
                ENDPOINT_VULCAN_API_TIMETABLE to LOGIN_METHOD_VULCAN_API
        ), listOf(LOGIN_METHOD_VULCAN_API)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_TIMETABLE, listOf(
                ENDPOINT_VULCAN_HEBE_TIMETABLE to LOGIN_METHOD_VULCAN_HEBE
        ), listOf(LOGIN_METHOD_VULCAN_HEBE)),
        // agenda
        Feature(LOGIN_TYPE_VULCAN, FEATURE_AGENDA, listOf(
                ENDPOINT_VULCAN_API_EVENTS to LOGIN_METHOD_VULCAN_API
        ), listOf(LOGIN_METHOD_VULCAN_API)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_AGENDA, listOf(
                ENDPOINT_VULCAN_HEBE_EXAMS to LOGIN_METHOD_VULCAN_HEBE
        ), listOf(LOGIN_METHOD_VULCAN_HEBE)),
        // grades
        Feature(LOGIN_TYPE_VULCAN, FEATURE_GRADES, listOf(
                ENDPOINT_VULCAN_API_GRADES to LOGIN_METHOD_VULCAN_API,
                ENDPOINT_VULCAN_API_GRADES_SUMMARY to LOGIN_METHOD_VULCAN_API
        ), listOf(LOGIN_METHOD_VULCAN_API)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_GRADES, listOf(
                ENDPOINT_VULCAN_HEBE_GRADES to LOGIN_METHOD_VULCAN_HEBE
        ), listOf(LOGIN_METHOD_VULCAN_HEBE)),
        // homework
        Feature(LOGIN_TYPE_VULCAN, FEATURE_HOMEWORK, listOf(
                ENDPOINT_VULCAN_API_HOMEWORK to LOGIN_METHOD_VULCAN_API
        ), listOf(LOGIN_METHOD_VULCAN_API)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_HOMEWORK, listOf(
                ENDPOINT_VULCAN_HEBE_HOMEWORK to LOGIN_METHOD_VULCAN_HEBE
        ), listOf(LOGIN_METHOD_VULCAN_HEBE)),
        // behaviour
        Feature(LOGIN_TYPE_VULCAN, FEATURE_BEHAVIOUR, listOf(
                ENDPOINT_VULCAN_API_NOTICES to LOGIN_METHOD_VULCAN_API
        ), listOf(LOGIN_METHOD_VULCAN_API)),
        // attendance
        Feature(LOGIN_TYPE_VULCAN, FEATURE_ATTENDANCE, listOf(
                ENDPOINT_VULCAN_API_ATTENDANCE to LOGIN_METHOD_VULCAN_API
        ), listOf(LOGIN_METHOD_VULCAN_API)),
        // messages
        Feature(LOGIN_TYPE_VULCAN, FEATURE_MESSAGES_INBOX, listOf(
                ENDPOINT_VULCAN_API_MESSAGES_INBOX to LOGIN_METHOD_VULCAN_API
        ), listOf(LOGIN_METHOD_VULCAN_API)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_MESSAGES_SENT, listOf(
                ENDPOINT_VULCAN_API_MESSAGES_SENT to LOGIN_METHOD_VULCAN_API
        ), listOf(LOGIN_METHOD_VULCAN_API)),

        // push config
        Feature(LOGIN_TYPE_VULCAN, FEATURE_PUSH_CONFIG, listOf(
                ENDPOINT_VULCAN_API_PUSH_CONFIG to LOGIN_METHOD_VULCAN_API
        ), listOf(LOGIN_METHOD_VULCAN_API)).withShouldSync { data ->
                !data.app.config.sync.tokenVulcanList.contains(data.profileId)
        },

        /**
         * Lucky number - using WEB Main.
         */
        Feature(LOGIN_TYPE_VULCAN, FEATURE_LUCKY_NUMBER, listOf(
                ENDPOINT_VULCAN_WEB_LUCKY_NUMBERS to LOGIN_METHOD_VULCAN_WEB_MAIN
        ), listOf(LOGIN_METHOD_VULCAN_WEB_MAIN)).withShouldSync { data -> data.shouldSyncLuckyNumber() },

        Feature(LOGIN_TYPE_VULCAN, FEATURE_ALWAYS_NEEDED, listOf(
                ENDPOINT_VULCAN_API_UPDATE_SEMESTER to LOGIN_METHOD_VULCAN_API,
                ENDPOINT_VULCAN_API_DICTIONARIES to LOGIN_METHOD_VULCAN_API
        ), listOf(LOGIN_METHOD_VULCAN_API))
        /*Feature(LOGIN_TYPE_VULCAN, FEATURE_STUDENT_INFO, listOf(
                ENDPOINT_VULCAN_API to LOGIN_METHOD_VULCAN_WEB
        ), listOf(LOGIN_METHOD_VULCAN_WEB)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_STUDENT_NUMBER, listOf(
                ENDPOINT_VULCAN_API to LOGIN_METHOD_VULCAN_WEB
        ), listOf(LOGIN_METHOD_VULCAN_WEB)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_SCHOOL_INFO, listOf(
                ENDPOINT_VULCAN_API to LOGIN_METHOD_VULCAN_WEB
        ), listOf(LOGIN_METHOD_VULCAN_WEB)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_CLASS_INFO, listOf(
                ENDPOINT_VULCAN_API to LOGIN_METHOD_VULCAN_WEB
        ), listOf(LOGIN_METHOD_VULCAN_WEB)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_TEAM_INFO, listOf(
                ENDPOINT_VULCAN_API to LOGIN_METHOD_VULCAN_WEB
        ), listOf(LOGIN_METHOD_VULCAN_WEB)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_TEACHERS, listOf(
                ENDPOINT_VULCAN_API to LOGIN_METHOD_VULCAN_WEB
        ), listOf(LOGIN_METHOD_VULCAN_WEB)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_SUBJECTS, listOf(
                ENDPOINT_VULCAN_API to LOGIN_METHOD_VULCAN_WEB
        ), listOf(LOGIN_METHOD_VULCAN_WEB)),
        Feature(LOGIN_TYPE_VULCAN, FEATURE_CLASSROOMS, listOf(
                ENDPOINT_VULCAN_API to LOGIN_METHOD_VULCAN_WEB
        ), listOf(LOGIN_METHOD_VULCAN_WEB)),*/

)
