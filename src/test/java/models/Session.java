package models;

import lombok.Data;

@Data
public class Session {
    private String userId,
                    token,
                    expires;
}
