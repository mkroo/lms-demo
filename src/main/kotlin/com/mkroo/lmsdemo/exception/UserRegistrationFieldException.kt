package com.mkroo.lmsdemo.exception

import com.mkroo.lmsdemo.dto.RegisterUserRequest
import kotlin.reflect.KProperty1

class UserRegistrationFieldException(
    field: KProperty1<RegisterUserRequest, String>,
    message: String
) : IllegalArgumentException("$message (${field.name})")
