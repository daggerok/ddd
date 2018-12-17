package com.github.daggerok.ddd.app.cqrsandeventsourcing;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;
import static java.math.BigDecimal.ZERO;
import static java.time.ZonedDateTime.now;

@ToString
@EqualsAndHashCode
public class BankAccount {

  @Getter
  private UUID aggregateId;
  @Getter
  private BigDecimal balance;
  private List<DomainEvent> dirtyEvents;

  public BankAccount(UUID aggregateId) {
    if (null == aggregateId) throw new IllegalArgumentException("cannot create bank account with null aggregateId");
    on(new BankAccountCreated(aggregateId, ZERO, now()));
  }

  public List<DomainEvent> getDirtyEvents() {
    return Collections.unmodifiableList(dirtyEvents);
  }

  public void flushDirtyEvents() {
    dirtyEvents.clear();
  }

  private BankAccount on(BankAccountCreated event) {
    aggregateId = event.getAggregateId();
    balance = event.getBalance();
    dirtyEvents = new ArrayList<>();
    dirtyEvents.add(event);
    return this;
  }

  public BankAccount deposit(BigDecimal amount) {
    if (canNotDeposit(amount)) throw new IllegalArgumentException("cannot deposit " + amount);
    on(new Deposited(aggregateId, amount, now()));
    return this;
  }

  private boolean canNotDeposit(BigDecimal amount) {
    return amount == null || amount.signum() <= 0;
  }

  private BankAccount on(Deposited event) {
    balance = balance.add(event.getAmount());
    dirtyEvents.add(event);
    return this;
  }

  public void withdraw(BigDecimal amount) {
    if (canNotWithdrawAmount(amount)) throw new IllegalArgumentException("cannot withdraw " + amount);
    on(new Withdrawn(aggregateId, amount, now()));
  }

  private boolean canNotWithdrawAmount(BigDecimal amount) {
    return amount == null || balance.compareTo(amount) < 0;
  }

  private BankAccount on(Withdrawn event) {
    balance = balance.subtract(event.getAmount());
    dirtyEvents.add(event);
    return this;
  }

  public static BankAccount loadFromHistory(UUID aggregateId, List<DomainEvent> domainEvents) {
    final BankAccount initialState = new BankAccount(aggregateId);
    return io.vavr.collection.List.ofAll(domainEvents)
                                  .foldLeft(initialState, BankAccount::handle);
  }

  private BankAccount handle(DomainEvent domainEvent) {
    return Match(domainEvent).of(
        Case($(instanceOf(BankAccountCreated.class)), this),
        Case($(instanceOf(Deposited.class)), this::on),
        Case($(instanceOf(Withdrawn.class)), this::on),
        Case($(), () -> {
          throw new RuntimeException("cannot parse Domain Event");
        })
    );
  }
}
