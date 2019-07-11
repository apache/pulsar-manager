package com.manager.pulsar.mapper;

import com.github.pagehelper.Page;
import com.manager.pulsar.entity.BundlesEntity;
import org.apache.ibatis.annotations.*;

/**
 * Insert, delete, udpate and query for Bundle.
 */
@Mapper
public interface BundlesMapper {

    @Insert("INSERT INTO bundles(broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold) " +
            "VALUES(#{broker},#{tenant},#{namespace},#{bundle},#{msgRateIn},#{msgThroughputIn},#{msgRateOut}," +
            "#{msgThroughputOut},#{consumerCount},#{producerCount},#{topics},#{cacheSize}," +
            "#{throughputDifferenceThreshold},#{msgRateDifferenceThreshold},#{topicConnectionDifferenceThreshold}," +
            "#{cacheSizeDifferenceThreshold})")
    @Options(useGeneratedKeys = true, keyProperty = "brokerId", keyColumn = "brokerId")
    void insert(BundlesEntity bundlesEntity);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold FROM bundles WHERE bundleId=#{bundleId}")
    BundlesEntity findById(int bundleId);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold FROM bundles WHERE broker=#{broker} and " +
            "tenant=#{tenant} and namespace=#{namespace} and bundle=#{bundle}")
    BundlesEntity findByBrokerTenantNamespaceBundle(String broker, String tenant, String namespace, String bundle);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold FROM bundles WHERE broker=#{btnb} or " +
            "tenant=#{btnb} or namespace=#{btnb} or bundle=#{btnb}")
    Page<BundlesEntity> findByBrokerOrTenantOrNamespaceOrBundle(String btnb);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold FROM bundles WHERE bundle=#{bundle}")
    Page<BundlesEntity> findByBundle(String bundle);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold FROM bundles")
    Page<BundlesEntity> getBundlesList();

    @Update("UPDATE bundles set msgRateIn=#{msgRateIn},msgThroughputIn=#{msgThroughputIn},msgRateOut=#{msgRateOut}," +
            "msgThroughputOut=#{msgThroughputOut},consumerCount=#{consumerCount},producerCount=#{producerCount}," +
            "topics=#{topics},cacheSize=#{cacheSize},throughputDifferenceThreshold=#{throughputDifferenceThreshold}," +
            "msgRateDifferenceThreshold=#{msgRateDifferenceThreshold}," +
            "topicConnectionDifferenceThreshold=#{topicConnectionDifferenceThreshold}," +
            "cacheSizeDifferenceThreshold=#{cacheSizeDifferenceThreshold} WHERE broker=#{broker} and tenant=#{tenant}" +
            " and namespace=#{namespace} and bundle={bundle}")
    void update(BundlesEntity bundlesEntity);

    @Delete("DELETE FROM bundles WHERE bundleId=#{bundleId}")
    void deleteByBundleId(Integer bundleId);

    @Delete("DELETE FROM bundles WHERE broker=#{broker} and tenant=#{tenant} " +
            "and namespace=#{namespace} and bundle=#{bundle}")
    void delete(@Param("broker") String broker, @Param("tenant") String tenant,
                @Param("namespace") String namespace, @Param("bundle") String bundle);

}
