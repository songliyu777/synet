package com.synet.net.tcp;

import lombok.Data;

@Data
public class TcpServiceConfig {
    private String host;
    private Integer port;
    private Long readIdleTime;
    private Long writeIdleTime;
}
