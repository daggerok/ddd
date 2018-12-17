package com.github.daggerok.ddd.app.cqrsandeventsourcing

import org.mockito.InjectMocks
import org.mockito.Mock
import spock.lang.Ignore
import spock.lang.Specification

@Ignore // TODO: Intermediate Kafka implementation...
class BankAccountRepositorySpecification extends Specification {

  BankAccountRepository repository = new BankAccountRepository(null)

  def 'should save and load aggregate'() {
    given:
    UUID aggregateId = UUID.randomUUID()
    and: // BankAccountCreated event
    BankAccount account = new BankAccount(aggregateId)
    and: // Deposited event
    account.deposit 100
    and: // Withdrawn event
    account.withdraw 50
    and:
    repository.save account
    when:
    BankAccount loaded = repository.load aggregateId
    then:
    loaded.balance == 50
    and:
    loaded.dirtyEvents.size() == 3
  }
}
