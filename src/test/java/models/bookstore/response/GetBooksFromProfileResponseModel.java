package models.bookstore.response;

import lombok.Data;
import models.bookstore.BookModel;

import java.util.List;

@Data
public class GetBooksFromProfileResponseModel {
    String userId, username;
    List<BookModel> books;
}
