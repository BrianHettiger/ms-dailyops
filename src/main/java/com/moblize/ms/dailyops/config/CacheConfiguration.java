package com.moblize.ms.dailyops.config;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.dto.TrueRopCache;
import com.moblize.ms.dailyops.dto.WellCoordinatesResponseV2;
import io.github.jhipster.config.cache.PrefixedKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.MarshallerUtil;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.Collection;

import static org.infinispan.query.remote.client.ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfiguration {
    private GitProperties gitProperties;
    private BuildProperties buildProperties;

    private final Environment env;

    private final ServerProperties serverProperties;

    private final DiscoveryClient discoveryClient;

    private Registration registration;
    public CacheConfiguration(Environment env, ServerProperties serverProperties, DiscoveryClient discoveryClient) {
        this.env = env;
        this.serverProperties = serverProperties;
        this.discoveryClient = discoveryClient;
    }

    @Autowired(required = false)
    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    @Bean
    public RemoteCacheManager remoteCacheManager() throws IOException {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer().host("10.200.0.36").port(11222);
        builder.security().authentication().saslMechanism("DIGEST-MD5").username("development1").password("moblize-development");
        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());
        SerializationContext ctx = MarshallerUtil.getSerializationContext(cacheManager);
        ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
        String fileName = "infinispan-scheme.proto";
        String protoFile = protoSchemaBuilder
            .fileName(fileName)
            .addClass(TrueRopCache.class)
            .addClass(MongoWell.class)
            .addClass(WellCoordinatesResponseV2.class)
            .packageName("moblize")
            .build(ctx);
        RemoteCache<String, String> metadataCache =
            cacheManager.getCache(PROTOBUF_METADATA_CACHE_NAME);

        // Define the new schema on the server too
        metadataCache.put(fileName, protoFile);
        return cacheManager;
    }


    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
