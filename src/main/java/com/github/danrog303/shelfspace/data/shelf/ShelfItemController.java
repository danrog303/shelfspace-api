package com.github.danrog303.shelfspace.data.shelf;

import com.github.danrog303.shelfspace.services.authorization.AuthorizationProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Exposes REST endpoints with basic operations on shelf items.
 */
@RestController
@RequestMapping("/shelves/{shelfId}/items")
@RequiredArgsConstructor
@Tag(name="Operations on user's shelf items")
public class ShelfItemController {
    private final AuthorizationProvider authorizationProvider;
    private final ShelfItemService shelfItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary="Creates new item on the specified shelf", description="Requires to be authenticated")
    @ApiResponses({
            @ApiResponse(responseCode="201", description="Item successfully created"),
            @ApiResponse(responseCode="400", description="Sent request body was malformed"),
            @ApiResponse(responseCode="401", description="Access token was not specified"),
            @ApiResponse(responseCode="409", description="Tried to exceed the shelf item quota")
    })
    public ShelfItem createNewShelfItem(@PathVariable String shelfId, @Valid @RequestBody ShelfItem item) {
        String currentUserId = authorizationProvider.getAuthenticatedUserId();
        return shelfItemService.createShelfItem(currentUserId, shelfId, item);
    }

    @PutMapping("/{itemId}")
    @Operation(summary="Updates the specified shelf item", description="Requires to be authenticated")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="Shelf item successfully fetched"),
            @ApiResponse(responseCode="400", description="Sent request body was malformed"),
            @ApiResponse(responseCode="401", description="Access token was not specified"),
            @ApiResponse(responseCode="404", description="Requested shelf or shelf item was not found in the shelves collection of the authenticated user"),
    })
    public ShelfItem updateShelfItem(@PathVariable String shelfId, @PathVariable String itemId, @Valid @RequestBody ShelfItem item) {
        String currentUserId = authorizationProvider.getAuthenticatedUserId();
        return shelfItemService.updateShelfItem(currentUserId, shelfId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary="Deletes the specified shelf info", description="Requires to be authenticated")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="Shelf item successfully deleted"),
            @ApiResponse(responseCode="401", description="Access token was not specified"),
            @ApiResponse(responseCode="404", description="Shelf or shelf item was not found in the authenticated user shelves collection"),
    })
    public ShelfItem deleteShelfItem(@PathVariable String shelfId, @PathVariable String itemId) {
        String currentUserId = authorizationProvider.getAuthenticatedUserId();
        return shelfItemService.deleteShelfItem(currentUserId, shelfId, itemId);
    }
}
