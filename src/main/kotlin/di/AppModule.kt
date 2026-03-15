package com.haykor.di

import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val appModule = module {
    single { Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver") }

    includes(userModule)
}
