package com.github.daggerok.ddd.app.cqrsandeventsourcing

import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

interface DomainEvent

data class BankAccountCreated(val aggregateId: UUID,
                              val balance: BigDecimal,
                              val occurredAt: ZonedDateTime) : DomainEvent

data class Deposited(val aggregateId: UUID,
                     val amount: BigDecimal,
                     val occurredAt: ZonedDateTime) : DomainEvent

data class Withdrawn(val aggregateId: UUID,
                     val amount: BigDecimal,
                     val occurredAt: ZonedDateTime) : DomainEvent
