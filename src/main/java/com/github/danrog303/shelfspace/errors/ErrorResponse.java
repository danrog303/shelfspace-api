package com.github.danrog303.shelfspace.errors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A model that is serialized to JSON and sent to the user when an error has occurred.
 */
@Data @AllArgsConstructor @NoArgsConstructor
@Schema(description="Used for HTTP4XX and HTTP5XX responses.")
public class ErrorResponse {
    /**
     * Name of the error, for example "NOT_FOUND" or "SHELF_QUOTA_EXCEEDED".
     */
    private String error;

    /**
     * More detailed information about the error.
     */
    private String message;
}
