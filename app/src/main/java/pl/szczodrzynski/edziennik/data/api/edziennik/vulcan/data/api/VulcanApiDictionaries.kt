/*
 * Copyright (c) Kacper Ziubryniewicz 2019-10-20
 */

package pl.szczodrzynski.edziennik.data.api.edziennik.vulcan.data.api

import com.google.gson.JsonObject
import pl.szczodrzynski.edziennik.*
import pl.szczodrzynski.edziennik.data.api.VULCAN_API_ENDPOINT_DICTIONARIES
import pl.szczodrzynski.edziennik.data.api.edziennik.vulcan.DataVulcan
import pl.szczodrzynski.edziennik.data.api.edziennik.vulcan.ENDPOINT_VULCAN_API_DICTIONARIES
import pl.szczodrzynski.edziennik.data.api.edziennik.vulcan.data.VulcanApi
import pl.szczodrzynski.edziennik.data.db.entity.*
import pl.szczodrzynski.edziennik.utils.models.Time

class VulcanApiDictionaries(override val data: DataVulcan,
                            override val lastSync: Long?,
                            val onSuccess: (endpointId: Int) -> Unit
) : VulcanApi(data, lastSync) {
    companion object {
        const val TAG = "VulcanApiDictionaries"
    }

    init {
        apiGet(TAG, VULCAN_API_ENDPOINT_DICTIONARIES) { json, _ ->
            val elements = json.getJsonObject("Data")

            elements?.getJsonArray("Pracownicy")?.forEach { saveTeacher(it.asJsonObject) }
            elements?.getJsonArray("Przedmioty")?.forEach { saveSubject(it.asJsonObject) }
            elements?.getJsonArray("PoryLekcji")?.forEach { saveLessonRange(it.asJsonObject) }
            elements?.getJsonArray("KategorieOcen")?.forEach { saveGradeCategory(it.asJsonObject) }
            elements?.getJsonArray("KategorieUwag")?.forEach { saveNoticeType(it.asJsonObject) }
            elements?.getJsonArray("KategorieFrekwencji")?.forEach { saveAttendanceType(it.asJsonObject) }

            data.setSyncNext(ENDPOINT_VULCAN_API_DICTIONARIES, 4 * DAY)
            onSuccess(ENDPOINT_VULCAN_API_DICTIONARIES)
        }
    }

    private fun saveTeacher(teacher: JsonObject) {
        val id = teacher.getLong("Id") ?: return
        val name = teacher.getString("Imie") ?: ""
        val surname = teacher.getString("Nazwisko") ?: ""
        val loginId = teacher.getString("LoginId") ?: "-1"

        val teacherObject = Teacher(
                profileId,
                id,
                name,
                surname,
                loginId
        )

        data.teacherList.put(id, teacherObject)
    }

    private fun saveSubject(subject: JsonObject) {
        val id = subject.getLong("Id") ?: return
        val longName = subject.getString("Nazwa") ?: ""
        val shortName = subject.getString("Kod") ?: ""

        val subjectObject = Subject(
                profileId,
                id,
                longName,
                shortName
        )

        data.subjectList.put(id, subjectObject)
    }

    private fun saveLessonRange(lessonRange: JsonObject) {
        val lessonNumber = lessonRange.getInt("Numer") ?: return
        val startTime = lessonRange.getString("PoczatekTekst")?.let { Time.fromH_m(it) } ?: return
        val endTime = lessonRange.getString("KoniecTekst")?.let { Time.fromH_m(it) } ?: return

        val lessonRangeObject = LessonRange(
                profileId,
                lessonNumber,
                startTime,
                endTime
        )

        data.lessonRanges.put(lessonNumber, lessonRangeObject)
    }

    private fun saveGradeCategory(gradeCategory: JsonObject) {
        val id = gradeCategory.getLong("Id") ?: return
        val name = gradeCategory.getString("Nazwa") ?: ""

        val gradeCategoryObject = GradeCategory(
                profileId,
                id,
                0.0f,
                -1,
                name
        )

        data.gradeCategories.put(id, gradeCategoryObject)
    }

    private fun saveNoticeType(noticeType: JsonObject) {
        val id = noticeType.getLong("Id") ?: return
        val name = noticeType.getString("Nazwa") ?: ""

        val noticeTypeObject = NoticeType(
                profileId,
                id,
                name
        )

        data.noticeTypes.put(id, noticeTypeObject)
    }

    private fun saveAttendanceType(attendanceType: JsonObject) {
        val id = attendanceType.getLong("Id") ?: return
        val typeName = attendanceType.getString("Nazwa") ?: ""

        val absent = attendanceType.getBoolean("Nieobecnosc") ?: false
        val excused = attendanceType.getBoolean("Usprawiedliwione") ?: false
        val baseType = if (absent) {
            if (excused)
                Attendance.TYPE_ABSENT_EXCUSED
            else
                Attendance.TYPE_ABSENT
        } else {
            val belated = attendanceType.getBoolean("Spoznienie") ?: false
            val released = attendanceType.getBoolean("Zwolnienie") ?: false
            val present = attendanceType.getBoolean("Obecnosc") ?: true
            if (belated)
                if (excused)
                    Attendance.TYPE_BELATED_EXCUSED
                else
                    Attendance.TYPE_BELATED
            else if (released)
                Attendance.TYPE_RELEASED
            else if (present)
                Attendance.TYPE_PRESENT
            else
                Attendance.TYPE_UNKNOWN
        }

        val (typeColor, typeSymbol) = when (id.toInt()) {
            1 -> 0xffffffff to "●"  // obecność
            2 -> 0xffffa687 to "—"  // nieobecność
            3 -> 0xfffcc150 to "u"  // nieobecność usprawiedliwiona
            4 -> 0xffede049 to "s"  // spóźnienie
            5 -> 0xffbbdd5f to "su" // spóźnienie usprawiedliwione
            6 -> 0xffa9c9fd to "ns" // nieobecny z przyczyn szkolnych
            7 -> 0xffddbbe5 to "z"  // zwolniony
            8 -> 0xffffffff to ""   // usunięty wpis
            else -> null to "?"
        }

        val typeShort = when (id.toInt()) {
            6 -> "ns" // nieobecny z przyczyn szkolnych
            8 -> ""   // usunięty wpis
            else -> data.app.attendanceManager.getTypeShort(baseType)
        }

        val attendanceTypeObject = AttendanceType(
                profileId,
                id,
                baseType,
                typeName,
                typeShort,
                typeSymbol,
                typeColor?.toInt()
        )

        data.attendanceTypes.put(id, attendanceTypeObject)
    }
}