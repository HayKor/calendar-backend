package com.haykor.features.user.domain.model

import com.haykor.core.exception.ConflictException
import com.haykor.core.exception.ForbiddenException
import com.haykor.core.exception.NotFoundException

object UserRelationshipException {
    class RequesterEqualsAddressee : ConflictException("Can't befriend yourself")
    class SettingPendingNotAllowed : ConflictException("Can't change status to pending")
    class PendingRequestAlreadyExists : ConflictException("Pending request already exists")
    class Blocked : ForbiddenException("Request already declined")
    class Forbidden : ForbiddenException("You don't have permission to modify this relationship")
    class AlreadyFriends : ConflictException("Already friends with this user")
    class NotFound : NotFoundException("User relationship not found")
}
