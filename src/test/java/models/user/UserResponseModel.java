package models.user;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UserResponseModel {

    String name, job;

    @JsonIgnore
    String id, createdAt;
}