package models.bookstore;

import lombok.Data;

import java.util.List;

@Data
public class GetBooksFromStoreResponseModel {
    List<BookModel> books;
}