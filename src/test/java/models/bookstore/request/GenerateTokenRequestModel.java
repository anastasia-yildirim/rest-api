package models.bookstore.request;

import lombok.Data;

@Data
public class GenerateTokenRequestModel {
    String userName, password;
}
