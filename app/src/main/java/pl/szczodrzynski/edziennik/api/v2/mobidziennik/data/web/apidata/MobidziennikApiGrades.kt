/*
 * Copyright (c) Kuba Szczodrzyński 2019-10-8.
 */

package pl.szczodrzynski.edziennik.api.v2.mobidziennik.data.web.apidata

import pl.szczodrzynski.edziennik.api.v2.mobidziennik.DataMobidziennik
import pl.szczodrzynski.edziennik.data.db.modules.grades.Grade
import pl.szczodrzynski.edziennik.data.db.modules.grades.Grade.*
import pl.szczodrzynski.edziennik.data.db.modules.metadata.Metadata

class MobidziennikApiGrades(val data: DataMobidziennik, rows: List<String>) {
    init { run {
        data.db.gradeDao().getDetails(
                data.profileId,
                data.gradeAddedDates,
                data.gradeAverages,
                data.gradeColors
        )
        var addedDate = System.currentTimeMillis()
        for (row in rows) {
            if (row.isEmpty())
                continue
            val cols = row.split("|")

            val studentId = cols[2].toInt()
            if (studentId != data.studentId)
                return@run

            val id = cols[0].toLong()
            val categoryId = cols[6].toLongOrNull() ?: -1
            val categoryColumn = cols[10].toIntOrNull() ?: 1
            val name = cols[7]
            val value = cols[11].toFloat()
            val semester = cols[5].toInt()
            val teacherId = cols[2].toLong()
            val subjectId = cols[3].toLong()
            var type = when (cols[8]) {
                "3" -> if (semester == 1) TYPE_SEMESTER1_PROPOSED else TYPE_SEMESTER2_PROPOSED
                "1" -> if (semester == 1) TYPE_SEMESTER1_FINAL else TYPE_SEMESTER2_FINAL
                "4" -> TYPE_YEAR_PROPOSED
                "2" -> TYPE_YEAR_FINAL
                else -> TYPE_NORMAL
            }

            var weight = 0.0f
            var category = ""
            var description = ""
            var color = -1
            data.gradeCategories.get(categoryId)?.let { gradeCategory ->
                weight = gradeCategory.weight
                category = gradeCategory.text
                description = gradeCategory.columns[categoryColumn-1]
                color = gradeCategory.color
            }

            // fix for "0" value grades, so they're not counted in the average
            if (value == 0.0f/* && data.app.appConfig.dontCountZeroToAverage*/) {
                weight = 0.0f
            }

            val gradeObject = Grade(
                    data.profileId,
                    id,
                    category,
                    color,
                    description,
                    name,
                    value,
                    weight,
                    semester,
                    teacherId,
                    subjectId)

            data.gradeList.add(gradeObject)
            data.metadataList.add(
                    Metadata(
                            data.profileId,
                            Metadata.TYPE_GRADE,
                            id,
                            data.profile?.empty ?: false,
                            data.profile?.empty ?: false,
                            addedDate
                    ))
            addedDate++
        }
    }}
}