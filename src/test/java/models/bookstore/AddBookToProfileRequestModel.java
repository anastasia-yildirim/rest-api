package models.bookstore;

import lombok.Data;

import java.util.List;

@Data
public class AddBookToProfileRequestModel {

    String userId;
    List<BookModel> collectionOfIsbns;
}
