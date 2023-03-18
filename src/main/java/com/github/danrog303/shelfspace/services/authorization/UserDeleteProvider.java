package com.github.danrog303.shelfspace.services.authorization;

/**
 * Provides {@link #deleteUser(String)} method.
 */
public interface UserDeleteProvider {
    /**
     * Sends request to the authentication server to remove user with the specified id.
     */
    void deleteUser(String userId);
}