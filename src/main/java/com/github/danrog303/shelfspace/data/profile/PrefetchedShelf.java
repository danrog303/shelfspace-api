package com.github.danrog303.shelfspace.data.profile;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import com.github.danrog303.shelfspace.data.shelf.Shelf;
import com.github.danrog303.shelfspace.data.shelf.ShelfType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * Prefetched version of {@link Shelf} class. Unlike a regular instance, a prefetched instance
 * does not contain any references to the items placed on a particular shelf.
 */
@DynamoDBDocument
@Data @NoArgsConstructor @AllArgsConstructor
public class PrefetchedShelf {
    /**
     * Unique identifier of the shelf.
     */
    @DynamoDBAttribute
    private String shelfId;

    /**
     * Name of the shelf, chosen by the user. For example "Games" or "Sci-fi movies".
     */
    @DynamoDBAttribute
    @NotNull @Length(min=3, max=64)
    private String shelfName;

    /**
     * Type of the shelf.
     */
    @DynamoDBAttribute
    @DynamoDBTypeConvertedEnum
    @NotNull
    private ShelfType shelfType;
}
