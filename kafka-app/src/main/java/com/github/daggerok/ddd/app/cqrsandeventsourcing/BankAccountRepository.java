package com.github.daggerok.ddd.app.cqrsandeventsourcing;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.daggerok.ddd.app.cqrsandeventsourcing.KafkaBrokerAppConfig.KAFKA_TOPIC;
import static java.util.Objects.isNull;

@Repository
public class BankAccountRepository {

  private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
  private final Map<UUID, List<DomainEvent>> eventStream;

  public BankAccountRepository(KafkaTemplate<String, DomainEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
    eventStream = new ConcurrentHashMap<>();
  }

  public void save(BankAccount aggregate) {
    assertNotNull(aggregate, "aggregate may not be null");

    final UUID aggregateId = aggregate.getAggregateId();
    assertNotNull(aggregateId, "aggregate ID may not be null");

    final List<DomainEvent> dirtyDomainEvents = aggregate.getDirtyEvents();
    assertNotNull(dirtyDomainEvents, "dirty domain events may not be null");
    if (dirtyDomainEvents.isEmpty()) return;

    eventStream.put(aggregateId, Stream.concat(domainEvents(aggregateId).stream(),
                                               dirtyDomainEvents.stream())
                                       .collect(Collectors.toList()));
    dirtyDomainEvents.forEach(domainEvent -> kafkaTemplate.send(KAFKA_TOPIC, domainEvent));
    //kafkaTemplate.flush();
    aggregate.flushDirtyEvents();
  }

  public BankAccount load(UUID aggregateId) {
    assertNotNull(aggregateId, "aggregate ID may not be null");
    return BankAccount.loadFromHistory(aggregateId, domainEvents(aggregateId));
  }

  private void assertNotNull(Object mayNotBeNull, String errorMessage) {
    if (isNull(mayNotBeNull)) throw new IllegalArgumentException(errorMessage);
  }

  private List<DomainEvent> domainEvents(UUID aggregateId) {
    return eventStream.getOrDefault(aggregateId, new ArrayList<>());
  }
}
