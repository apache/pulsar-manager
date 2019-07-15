package com.manager.pulsar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClusterEntity {
    private long clusterId;
    private String cluster;

    private String serviceUrl;

    private String serviceUrlTls;

    private String brokerServiceUrl;

    private String brokerServiceUrlTls;

}
