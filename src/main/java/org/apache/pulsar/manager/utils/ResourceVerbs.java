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
package org.apache.pulsar.manager.utils;

public enum ResourceVerbs {
    /** SUPER_USER can operate all resources
     * such as cluster, broker, broker-stats, functions-worker, resource-quotas,
     * ns-isolation-policy, tenants, functions, sources, sinks.
     * If the SUPER_USER wants to operate the namespace, topic and schema,
     * that needs to add the role to the role list of all tenants.
     */
    SUPER_USER,
    /** Topic, namespace, schema can be managed */
    ADMIN,
    /** Permission to produce/publish messages. Corresponding to the resource type of TOPIC and NAMESPACE. */
    PRODUCE,
    /** Permission to consume messages. Corresponding to the resource type of TOPIC and NAMESPACE */
    CONSUME,
    /** Permissions for functions ops. Corresponding to the resource type of TOPIC and NAMESPACE **/
    FUNCTION
}
