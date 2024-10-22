package models.bookstore.response;

import lombok.Data;
import models.bookstore.BookModel;

import java.util.List;

@Data
public class GetBooksFromStoreResponseModel {
    List<BookModel> books;
}