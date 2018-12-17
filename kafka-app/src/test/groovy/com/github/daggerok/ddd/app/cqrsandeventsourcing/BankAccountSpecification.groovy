package com.github.daggerok.ddd.app.cqrsandeventsourcing

import spock.lang.Specification

class BankAccountSpecification extends Specification {

  UUID aggregateId
  BankAccount account

  void setup() {
    account = new BankAccount(aggregateId = UUID.randomUUID())
  }

  def 'should create account'() {
    given:
    aggregateId = UUID.randomUUID()
    when:
    account = new BankAccount(aggregateId)
    then:
    account.aggregateId == aggregateId
  }

  def 'should deposit'() {
    when:
    account.deposit 100 as BigDecimal
    then:
    account.balance == 100
  }

  def 'should withdraw'() {
    given:
    account.deposit 100
    when:
    account.withdraw 10
    then:
    account.balance == 90
  }

  def 'should not withdraw when not enough money'() {
    given:
    account.deposit 100
    when:
    account.withdraw 101
    then:
    thrown IllegalArgumentException
  }

  void cleanup() {
    account = null
    aggregateId = null
  }
}
