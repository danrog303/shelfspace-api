package com.github.danrog303.shelfspace.data.profile;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Stores metadata of the user and their collection of shelves.
 */
@DynamoDBTable(tableName="shelf-space-user-profiles")
@Data @NoArgsConstructor @AllArgsConstructor
public class UserProfile {
    /**
     * Unique id of the user.
     */
    @DynamoDBHashKey
    private String userId;

    /**
     * Nickname of the user, downloaded from authentication server on user's first visit.
     */
    @DynamoDBAttribute
    private String nickname;

    /**
     * Shelves created by this user.
     */
    @DynamoDBAttribute
    private List<PrefetchedShelf> shelves;
}
