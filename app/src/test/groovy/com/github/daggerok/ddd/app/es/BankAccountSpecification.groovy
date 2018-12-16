package com.github.daggerok.ddd.app.es

import spock.lang.Specification

class BankAccountSpecification extends Specification {

  UUID id
  BankAccount account

  void setup() {
    id = UUID.randomUUID()
    account = new BankAccount(id)
  }

  def 'should create account'() {
    given:
      id = UUID.randomUUID()
    when:
      account = new BankAccount(id)
    then:
      account.id == id
  }

  def 'should deposit'() {
    when:
      account.deposit 100
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
    id = null
  }
}
