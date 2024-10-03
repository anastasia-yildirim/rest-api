package models.bookstore;

import lombok.Data;

@Data
public class GenerateTokenRequestModel {
    String userName, password;
}
