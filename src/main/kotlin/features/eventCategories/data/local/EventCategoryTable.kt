package com.haykor.features.eventCategories.data.local

import com.haykor.core.common.domain.Visibility
import com.haykor.features.user.data.local.UserTable
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.datetime
import kotlin.time.Clock

object EventCategoryTable : IntIdTable("event_categories") {
    val user = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 50)
    val defaultVisibility = enumeration("default_visibility", Visibility::class)
        .default(Visibility.Public)

    val colorHex = varchar("color_hex", 8)
    val iconName = varchar("icon_name", 50).nullable()
    val createdAt =
        datetime("created_at").clientDefault {
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }

    init {
        uniqueIndex(user, name)
    }
}
