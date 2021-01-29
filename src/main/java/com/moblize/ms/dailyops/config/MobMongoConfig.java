package com.moblize.ms.dailyops.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
@Configuration
@EnableMongoRepositories(
    basePackages = "com.moblize.ms.dailyops.repository.mongo.mob",
    mongoTemplateRef = "mobMongoTemplate")
public class MobMongoConfig {
}
