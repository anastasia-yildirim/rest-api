package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.ALL;

public class BookStoreSpec {

    public static RequestSpecification bookStoreRequestSpec = with()
            .filter(withCustomTemplates())
            .log().all()
            .contentType("application/json");

    private static ResponseSpecification bookStoreResponseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .log(ALL)
                .build();
    }

    public static ResponseSpecification bookStoreResponseSpec200 = bookStoreResponseSpec(200);
    public static ResponseSpecification bookStoreResponseSpec201 = bookStoreResponseSpec(201);
    public static ResponseSpecification bookStoreResponseSpec204 = bookStoreResponseSpec(204);

}