package com.github.daggerok.ddd.app.cqrsandeventsourcing;

import io.vavr.collection.HashMap;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaBrokerAppConfig {

  public static final String KAFKA_TOPIC = "bank-account-domain-events-kafka-template-topic";

  @Bean
  @Qualifier("kafkaProperties")
  Map<String, Object> config(@Value("${bootstrap.servers:127.0.0.1:9092}") final String bootstrapServers) {
    return HashMap.<String, Object>of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                                      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                                      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)
                  .toJavaMap();
  }

  @Bean
  ProducerFactory<String, DomainEvent> producerFactory(@Qualifier("kafkaProperties") Map<String, Object> config) {
    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean
  KafkaTemplate<String, DomainEvent> kafkaTemplate(ProducerFactory<String, DomainEvent> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }
}
