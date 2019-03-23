package com.synet.server.logic.config;

import org.springframework.stereotype.Service;

@Service
public class R2dbcDatabase{

//    DBConfig dbConfig;
//
//    private R2dbc r2dbc;
//
//    @Autowired
//    public R2dbcDatabase(DBConfig dbConfig){
//        r2dbc = new R2dbc(connectionFactory(dbConfig));
//    }
//
//
//    public PostgresqlConnectionFactory connectionFactory(DBConfig dbConfig) {
//
//        PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder() //
//                .host(dbConfig.getHost())
//                .port(dbConfig.getPort())
//                .database(dbConfig.getDatabase())
//                .username(dbConfig.getUsername())
//                .password(dbConfig.getPassword())
//                .build();
//
//        return new PostgresqlConnectionFactory(config);
//    }
//
//    public R2dbc getR2dbc() {
//        return r2dbc;
//    }
}
