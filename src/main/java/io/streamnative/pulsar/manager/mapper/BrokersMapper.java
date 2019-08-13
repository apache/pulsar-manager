/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamnative.pulsar.manager.mapper;

import com.github.pagehelper.Page;
import io.streamnative.pulsar.manager.entity.BrokerEntity;
import org.apache.ibatis.annotations.*;

/**
 * Insert, delete, udpate and query for Broker.
 */
@Mapper
public interface BrokersMapper {

    @Insert("INSERT INTO brokers(brokerId,broker,webServiceUrl,webServiceUrlTls,pulsarServiceUrl,pulsarServiceUrlTls," +
            "persistentTopicsEnabled,nonPersistentTopicsEnabled,brokerVersionString,loadReportType,maxResourceUsage) " +
            "VALUES(#{brokerId}, #{broker},#{webServiceUrl},#{webServiceUrlTls},#{pulsarServiceUrl}," +
            "#{pulsarServiceUrlTls},#{persistentTopicsEnabled},#{nonPersistentTopicsEnabled}," +
            "#{brokerVersionString},#{loadReportType},#{maxResourceUsage})")
    void insert(BrokerEntity brokersEntity);

    @Select("SELECT broker,webServiceUrl,webServiceUrlTls,pulsarServiceUrl,pulsarServiceUrlTls," +
            "persistentTopicsEnabled,nonPersistentTopicsEnabled,brokerVersionString,loadReportType,maxResourceUsage " +
            "FROM brokers WHERE brokerId=#{brokerId}")
    BrokerEntity findById(long brokerId);

    @Select("SELECT brokerId,broker,webServiceUrl,webServiceUrlTls,pulsarServiceUrl,pulsarServiceUrlTls," +
            "persistentTopicsEnabled,nonPersistentTopicsEnabled,brokerVersionString,loadReportType,maxResourceUsage " +
            "FROM brokers WHERE broker=#{broker}")
    BrokerEntity findByBroker(String broker);

    @Select("SELECT brokerId,broker,webServiceUrl,webServiceUrlTls,pulsarServiceUrl,pulsarServiceUrlTls," +
            "persistentTopicsEnabled,nonPersistentTopicsEnabled,brokerVersionString,loadReportType,maxResourceUsage " +
            "FROM brokers")
    Page<BrokerEntity> getBrokersList();

    @Update("UPDATE brokers set webServiceUrl=#{webServiceUrl},webServiceUrlTls=#{webServiceUrlTls}," +
            "pulsarServiceUrl=#{pulsarServiceUrl},pulsarServiceUrlTls=#{pulsarServiceUrlTls}," +
            "persistentTopicsEnabled=#{persistentTopicsEnabled}," +
            "nonPersistentTopicsEnabled=#{nonPersistentTopicsEnabled},brokerVersionString=#{brokerVersionString}," +
            "loadReportType=#{loadReportType},maxResourceUsage=#{maxResourceUsage} WHERE broker=#{broker}")
    void update(BrokerEntity brokersEntity);

    @Delete("DELETE FROM brokers WHERE brokerId=#{brokerId}")
    void deleteByBrokerId(Integer brokerId);

    @Delete("DELETE FROM brokers WHERE broker=#{broker}")
    void deleteByBroker(String broker);

}
