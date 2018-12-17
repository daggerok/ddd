package com.github.daggerok.ddd.app.cqrsandeventsourcing;

import io.vavr.collection.HashMap;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Map;

@EnableKafka
@Configuration
@EnableKafkaStreams
public class KafkaBrokerAppConfig {

  public static final String KAFKA_TOPIC = "bank-account-domain-events-kafka-template-topic";

  @Value("${bootstrap.servers:127.0.0.1:9092}")
  String bootstrapServers;

  @Bean
  @Qualifier("kafkaProperties")
  Map<String, Object> kafkaProperties() {
    return HashMap.<String, Object>of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                                      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                                      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)
        .toJavaMap();
  }

  @Bean
  ProducerFactory<String, DomainEvent> producerFactory(@Qualifier("kafkaProperties") Map<String, Object> kafkaProperties) {
    return new DefaultKafkaProducerFactory<>(kafkaProperties);
  }

  @Bean
  KafkaTemplate<String, DomainEvent> kafkaTemplate(ProducerFactory<String, DomainEvent> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  StreamsConfig streamsConfig() {
    return new StreamsConfig(
        HashMap.<String, Object>of(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                                   StreamsConfig.APPLICATION_ID_CONFIG, "bank-account-application-id")
               .toJavaMap()
    );
  }

// // TODO: kafka 2.1.0 is not 0.10...
//  @Bean
//  KTable<String, BankAccount> aggregate(StreamsBuilder builder) {
//    return builder.table(KAFKA_TOPIC)
//                  .groupBy((key, value) -> )
//                  .aggregate()
//  }
}
