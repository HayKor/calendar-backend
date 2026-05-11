package com.haykor.features.event.domain.usecase

import com.haykor.core.visibility.domain.model.Visibility
import com.haykor.core.visibility.domain.service.VisibilityGate
import com.haykor.core.visibility.domain.usecase.ResolveViewerRelationUseCase
import com.haykor.features.event.domain.model.EventOccurrence
import com.haykor.features.event.domain.model.mapper.toOccurrence
import com.haykor.features.event.domain.repository.EventExceptionRepository
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.event.domain.service.RRuleExpander
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.OffsetDateTime

class GetEventsInRangeUseCase(
    private val eventRepository: EventRepository,
    private val eventExceptionRepository: EventExceptionRepository,
    private val eventCategoriesRepository: EventCategoriesRepository,
    private val resolveViewerRelation: ResolveViewerRelationUseCase,
    private val rruleExpander: RRuleExpander,
) {
    suspend operator fun invoke(
        requesterId: Int,
        targetUserId: Int,
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<EventOccurrence> = coroutineScope {
        val relationDeferred = async { resolveViewerRelation(requesterId, targetUserId) }
        val categoriesDeferred = async {
            eventCategoriesRepository.getAllByUser(targetUserId)
                .associateBy { it.id }
        }
        val eventsDeferred = async { eventRepository.getByUserIdAndRange(targetUserId, from, to) }

        val relation = relationDeferred.await()
        val categories = categoriesDeferred.await()
        val eventsRaw = eventsDeferred.await()

        val events = eventsRaw
            .filter { event ->
                VisibilityGate.canView(
                    relation = relation,
                    eventVisibility = event.visibility,
                    categoryVisibility = event.categoryId?.let { categories[it]?.visibility },
                    userGlobalVisibility = Visibility.Public, // TODO: change to settings
                )
            }

        val (recurringEvents, regularEvents) = events.partition { it.isRecurring }
        val exceptions = if (recurringEvents.isNotEmpty()) {
            eventExceptionRepository.getByEventIds(recurringEvents.map { it.id }, from, to)
                .groupBy { it.eventId }
        } else {
            emptyMap()
        }

        val regularOccurrences = regularEvents.map { it.toOccurrence() }
        val recurringOccurrences = recurringEvents.flatMap { event ->
            rruleExpander.expand(event, from, to, exceptions[event.id] ?: emptyList())
        }

        (regularOccurrences + recurringOccurrences).sortedBy { it.startAt }
    }
}
