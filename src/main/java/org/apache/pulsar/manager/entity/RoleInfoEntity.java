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
package org.apache.pulsar.manager.entity;


import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Role information entity.
 */
@Getter
@Setter
@NoArgsConstructor
@Data
public class RoleInfoEntity {

    private long roleId;

    @SerializedName("role_name")
    private String roleName;

    @SerializedName("role_source")
    private String roleSource;
    private String description;

    @SerializedName("resource_type")
    private String resourceType;

    @SerializedName("resource_name")
    private String resourceName;

    @SerializedName("resource_id")
    private long resourceId;

    @SerializedName("resource_verbs")
    private String resourceVerbs;
    private int flag;
}
