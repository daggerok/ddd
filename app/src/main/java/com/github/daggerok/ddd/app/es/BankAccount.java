package com.github.daggerok.ddd.app.es;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public class BankAccount {

  private UUID id;
  private BigDecimal balance;

  public BankAccount(UUID id) {
    this.id = id;
    balance = BigDecimal.ZERO;
  }

  public void deposit(BigDecimal amount) {
    balance = balance.add(amount);
  }

  public void withdraw(BigDecimal amount) {
    if (canNotWithdrawAmount(amount)) {
      throw new IllegalArgumentException("cannot withdraw " + amount);
    }
    balance = balance.subtract(amount);
  }

  private boolean canNotWithdrawAmount(BigDecimal amount) {
    return amount == null || balance.compareTo(amount) < 0;
  }
}
