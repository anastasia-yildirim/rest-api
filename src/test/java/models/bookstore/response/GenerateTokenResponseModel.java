package models.bookstore.response;

import lombok.Data;

@Data
public class GenerateTokenResponseModel {
    String token, status, result, expires;
}
