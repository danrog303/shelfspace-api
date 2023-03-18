package com.github.danrog303.shelfspace.services.authorization;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents additional user information retrieved from the authentication server.
 */
@Data @AllArgsConstructor
public class UserInfo {
    private String userId;
    private String nickname;
    private String email;
}
