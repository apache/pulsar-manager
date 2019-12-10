package org.apache.pulsar.manager.service;

public interface PermissionsService {

    boolean hasPermissions(String path);
}
