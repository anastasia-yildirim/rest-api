package models.bookstore.request;

import lombok.Data;

@Data
public class LoginRequestModel {
    String userName, password;
}
