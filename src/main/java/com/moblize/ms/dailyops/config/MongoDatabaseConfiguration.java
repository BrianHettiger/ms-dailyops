package com.moblize.ms.dailyops.config;

import com.mongodb.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
@Import(value = MongoAutoConfiguration.class)
@EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
@Slf4j
public class MongoDatabaseConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.mongodb.client")
    public MongoProperties clientProperties() {
        return new MongoProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.mongodb.mob")
    public MongoProperties mobProperties() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("clientProperties") MongoProperties properties) throws Exception {
        log.info("Configuring clientMongoTemplate datasource");
        return new MongoTemplate(clientFactory(properties));
    }

    @Bean(name = "mobMongoTemplate")
    public MongoTemplate mobMongoTemplate(@Qualifier("mobProperties") MongoProperties properties) throws Exception {
        log.info("Configuring mobMongoTemplate datasource");
        return new MongoTemplate(mobFactory(properties));
    }

    @Bean
    @Primary
    public MongoDbFactory clientFactory(final MongoProperties mongo) throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(mongo.getHost(), mongo.getPort()),
            mongo.getDatabase());
    }

    @Bean
    public MongoDbFactory mobFactory(final MongoProperties mongo) throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(mongo.getHost(), mongo.getPort()),
            mongo.getDatabase());
    }
}
