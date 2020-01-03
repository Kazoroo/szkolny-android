/*
 * Copyright (c) Kuba Szczodrzyński 2019-10-10.
 */

package pl.szczodrzynski.edziennik.data.api.edziennik.mobidziennik.data.web

import pl.szczodrzynski.edziennik.data.api.Regexes
import pl.szczodrzynski.edziennik.data.api.edziennik.mobidziennik.DataMobidziennik
import pl.szczodrzynski.edziennik.data.db.modules.luckynumber.LuckyNumber
import pl.szczodrzynski.edziennik.data.db.modules.metadata.Metadata
import pl.szczodrzynski.edziennik.get
import pl.szczodrzynski.edziennik.utils.models.Date

class MobidziennikLuckyNumberExtractor(val data: DataMobidziennik, text: String) {
    init {
        data.profile?.luckyNumber = -1
        data.profile?.luckyNumberDate = null

        Regexes.MOBIDZIENNIK_LUCKY_NUMBER.find(text)?.get(1)?.toIntOrNull()?.let {
            val luckyNumberObject = LuckyNumber(
                    data.profileId,
                    Date.getToday(),
                    it
            )

            data.luckyNumberList.add(luckyNumberObject)
            data.metadataList.add(
                    Metadata(
                            data.profileId,
                            Metadata.TYPE_LUCKY_NUMBER,
                            luckyNumberObject.date.value.toLong(),
                            data.profile?.empty ?: false,
                            data.profile?.empty ?: false,
                            System.currentTimeMillis()
                    ))
        }
    }
}
