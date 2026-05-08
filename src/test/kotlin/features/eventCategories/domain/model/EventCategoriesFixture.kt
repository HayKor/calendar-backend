package features.eventCategories.domain.model

import com.haykor.core.common.domain.Visibility
import com.haykor.features.eventCategories.domain.model.CreateEventCategoryParams
import com.haykor.features.eventCategories.domain.model.EventCategory

object EventCategoryFixtures {
    fun category(
        id: Int = 1,
        userId: Int = 10,
        name: String = "Work",
        defaultVisibility: Visibility = Visibility.Public,
        iconName: String? = null,
        colorHex: String = "#FFFFFF",
    ) = EventCategory(
        id = id,
        userId = userId,
        name = name,
        visibility = defaultVisibility,
        iconName = iconName,
        colorHex = colorHex,
    )

    fun createParams(
        userId: Int = 10,
        name: String = "Work",
        defaultVisibility: Visibility = Visibility.Public,
        iconName: String? = null,
        colorHex: String = "#FFFFFF",
    ) = CreateEventCategoryParams(
        userId = userId,
        name = name,
        visibility = defaultVisibility,
        iconName = iconName,
        colorHex = colorHex,
    )
}
