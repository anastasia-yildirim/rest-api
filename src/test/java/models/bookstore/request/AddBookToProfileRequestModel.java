package models.bookstore.request;

import lombok.Data;
import models.bookstore.BookModel;

import java.util.List;

@Data
public class AddBookToProfileRequestModel {

    String userId;
    List<BookModel> collectionOfIsbns;
}
