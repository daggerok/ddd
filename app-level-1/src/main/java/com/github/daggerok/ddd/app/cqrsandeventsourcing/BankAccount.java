package com.github.daggerok.ddd.app.cqrsandeventsourcing;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@ToString
@EqualsAndHashCode
public class BankAccount {

  @Getter
  private UUID aggregateId;
  @Getter
  private BigDecimal balance;

  public BankAccount(UUID aggregateId) {
    if (null == aggregateId) throw new IllegalArgumentException("cannot create bank account with null aggregateId");
    onBankAccountCreated(aggregateId, BigDecimal.ZERO);
  }

  private void onBankAccountCreated(UUID aggregateId, BigDecimal balance) {
    this.aggregateId = aggregateId;
    this.balance = balance;
  }

  public void deposit(BigDecimal amount) {
    if (canNotDeposit(amount)) throw new IllegalArgumentException("cannot deposit " + amount);
    onDeposited(amount);
  }

  private boolean canNotDeposit(BigDecimal amount) {
    return amount == null || amount.signum() <= 0;
  }

  private void onDeposited(BigDecimal amount) {
    balance = balance.add(amount);
  }

  public void withdraw(BigDecimal amount) {
    if (canNotWithdrawAmount(amount)) throw new IllegalArgumentException("cannot withdraw " + amount);
    onWithdrawn(amount);
  }

  private boolean canNotWithdrawAmount(BigDecimal amount) {
    return amount == null || balance.compareTo(amount) < 0;
  }

  private void onWithdrawn(BigDecimal amount) {
    balance = balance.subtract(amount);
  }
}
