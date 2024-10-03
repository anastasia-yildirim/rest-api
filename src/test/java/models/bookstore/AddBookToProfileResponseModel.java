package models.bookstore;

import lombok.Data;

import java.util.List;

@Data
public class AddBookToProfileResponseModel {

    List<BookModel> books;
}
