package org.apache.pulsar.manager.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Role binding information entity.
 */
@Getter
@Setter
@NoArgsConstructor
@Data
public class RoleBindingEntity {

    @SerializedName("role_binding_id")
    private long roleBindingId;

    private String name;

    private String description;

    @SerializedName("user_id")
    private long userId;

    @SerializedName("role_id")
    private long roleId;
}
