package pl.szczodrzynski.edziennik.api.v2.vulcan.data.api

import androidx.core.util.isEmpty
import pl.szczodrzynski.edziennik.*
import pl.szczodrzynski.edziennik.api.v2.VULCAN_API_ENDPOINT_ATTENDANCE
import pl.szczodrzynski.edziennik.api.v2.vulcan.DataVulcan
import pl.szczodrzynski.edziennik.api.v2.vulcan.ENDPOINT_VULCAN_API_ATTENDANCE
import pl.szczodrzynski.edziennik.api.v2.vulcan.data.VulcanApi
import pl.szczodrzynski.edziennik.data.db.modules.api.SYNC_ALWAYS
import pl.szczodrzynski.edziennik.data.db.modules.attendance.Attendance
import pl.szczodrzynski.edziennik.data.db.modules.attendance.Attendance.TYPE_PRESENT
import pl.szczodrzynski.edziennik.data.db.modules.metadata.Metadata
import pl.szczodrzynski.edziennik.utils.models.Date

class VulcanApiAttendance(override val data: DataVulcan, val onSuccess: () -> Unit) : VulcanApi(data) {
    companion object {
        const val TAG = "VulcanApiAttendance"
    }

    init { data.profile?.also { profile ->
        if (data.attendanceTypes.isEmpty()) {
            data.db.attendanceTypeDao().getAllNow(profileId).toSparseArray(data.attendanceTypes) { it.id }
        }

        val startDate: String = profile.getSemesterStart(profile.currentSemester).stringY_m_d
        val endDate: String = profile.getSemesterEnd(profile.currentSemester).stringY_m_d

        apiGet(TAG, VULCAN_API_ENDPOINT_ATTENDANCE, parameters = mapOf(
                "DataPoczatkowa" to startDate,
                "DataKoncowa" to endDate,
                "IdOddzial" to data.studentClassId,
                "IdUczen" to data.studentId,
                "IdOkresKlasyfikacyjny" to data.studentSemesterId
        )) { json, _ ->
            json.getJsonObject("Data")?.getJsonArray("Frekwencje")?.forEach { attendanceEl ->
                val attendance = attendanceEl.asJsonObject

                val attendanceCategory = data.attendanceTypes.get(attendance.getLong("IdKategoria") ?: return@forEach)
                        ?: return@forEach

                val type = attendanceCategory.type

                val id = (attendance.getInt("Dzien") ?: 0) + (attendance.getInt("Numer") ?: 0)

                val lessonDateMillis = Date.fromY_m_d(attendance.getString("DzienTekst")).inMillis
                val lessonDate = Date.fromMillis(lessonDateMillis)

                val lessonSemester = profile.dateToSemester(lessonDate)

                val attendanceObject = Attendance(
                        profileId,
                        id.toLong(),
                        -1,
                        attendance.getLong("IdPrzedmiot") ?: -1,
                        lessonSemester,
                        attendance.getString("PrzedmiotNazwa") + attendanceCategory.name.let { " - $it" },
                        lessonDate,
                        data.lessonRanges.get(attendance.getInt("IdPoraLekcji") ?: 0)?.startTime,
                        type)

                data.attendanceList.add(attendanceObject)
                if (attendanceObject.type != TYPE_PRESENT) {
                    data.metadataList.add(Metadata(
                            profileId,
                            Metadata.TYPE_ATTENDANCE,
                            attendanceObject.id,
                            profile.empty,
                            profile.empty,
                            attendanceObject.lessonDate.combineWith(attendanceObject.startTime)
                    ))
                }
            }

            data.setSyncNext(ENDPOINT_VULCAN_API_ATTENDANCE, SYNC_ALWAYS)
            onSuccess()
        }
    } ?: onSuccess()}
}
