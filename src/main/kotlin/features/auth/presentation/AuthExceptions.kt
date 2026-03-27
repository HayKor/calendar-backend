package com.haykor.features.auth.presentation

import com.haykor.core.exception.UnauthorizedException

class UserUnauthorizedException : UnauthorizedException("User is not authorized")