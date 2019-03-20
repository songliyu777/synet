package com.synet.server.logic.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {

    @Autowired
    DBConfig dbConfig;

    @Bean
    @Override
    public PostgresqlConnectionFactory connectionFactory() {

        PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder() //
                .host(dbConfig.getHost())
                .port(dbConfig.getPort())
                .database(dbConfig.getDatabase())
                .username(dbConfig.getUsername())
                .password(dbConfig.getPassword())
                .build();

        return new PostgresqlConnectionFactory(config);
    }
}
