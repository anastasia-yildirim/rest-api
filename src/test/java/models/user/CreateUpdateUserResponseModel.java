package models.user;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class CreateUpdateUserResponseModel {

    String name, job;

    @JsonIgnore
    String id;

    @JsonIgnore
    String createdAt;   //for create user response

    @JsonIgnore
    String updatedAt;   //for update user response
}