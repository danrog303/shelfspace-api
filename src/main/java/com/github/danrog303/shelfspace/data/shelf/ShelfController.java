package com.github.danrog303.shelfspace.data.shelf;

import com.github.danrog303.shelfspace.data.profile.PrefetchedShelf;
import com.github.danrog303.shelfspace.data.profile.UserProfileService;
import com.github.danrog303.shelfspace.services.authorization.AuthorizationProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Exposes REST endpoints with basic operations on user's profile.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shelves")
@Tag(name="Operations on user's shelves")
public class ShelfController {
    private final AuthorizationProvider authorizationProvider;
    private final UserProfileService userProfileService;
    private final ShelfService shelfService;

    @GetMapping
    @Operation(summary="Fetches user's shelves", description="Requires to be authenticated")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="Shelf data successfully fetched"),
            @ApiResponse(responseCode="401", description="JWT access token was not specified"),
    })
    public List<PrefetchedShelf> getUserPrefetchedShelves() {
        String currentUserId = authorizationProvider.getAuthenticatedUserId();
        return userProfileService.getUserProfile(currentUserId).getShelves();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary="Creates a new shelf in the user's shelves collection", description="Requires to be authenticated with JWT access token")
    @ApiResponses({
            @ApiResponse(responseCode="201", description="Shelf successfully created"),
            @ApiResponse(responseCode="400", description="Sent request body was malformed"),
            @ApiResponse(responseCode="401", description="Access token was not specified"),
            @ApiResponse(responseCode="409", description="Tried to exceed the shelf quota")
    })
    public PrefetchedShelf createNewShelf(@RequestBody @Valid PrefetchedShelf shelfToCreate) {
        String currentUserId = authorizationProvider.getAuthenticatedUserId();
        return shelfService.createNewShelf(currentUserId, shelfToCreate);
    }

    @DeleteMapping("/{shelfId}")
    @Operation(summary="Deletes the specified shelf from the user's shelves collection", description="Requires to be authenticated with JWT access token")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="Shelf successfully deleted"),
            @ApiResponse(responseCode="401", description="Access token was not specified"),
            @ApiResponse(responseCode="404", description="Shelf was not found in the authenticated user shelves collection"),
    })
    public PrefetchedShelf deleteShelf(@PathVariable String shelfId) {
        String currentUserId = authorizationProvider.getAuthenticatedUserId();
        return shelfService.deleteShelf(currentUserId, shelfId);
    }

    @GetMapping("/{shelfId}")
    @Operation(summary="Fetches shelf information and all of the shelf's items", description="Requires to be authenticated with JWT access token")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="Shelf data successfully fetched"),
            @ApiResponse(responseCode="401", description="Access token was not specified"),
            @ApiResponse(responseCode="404", description="Shelf was not found in the authenticated user shelves collection"),
    })
    public Shelf getShelf(@PathVariable String shelfId) {
        String currentUserId = authorizationProvider.getAuthenticatedUserId();
        return shelfService.getShelf(currentUserId, shelfId);
    }

    @PutMapping("/{shelfId}")
    @Operation(summary="Updates metadata of the specified shelf", description="Requires to be authenticated with JWT access token")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="Shelf successfuly modified"),
            @ApiResponse(responseCode="400", description="Sent request body was malformed"),
            @ApiResponse(responseCode="401", description="Access token was not specified"),
            @ApiResponse(responseCode="404", description="Shelf was not found in the authenticated user shelves collection"),
    })
    public Shelf updateShelf(@RequestBody @Valid PrefetchedShelf shelfToUpdate, @PathVariable String shelfId) {
        String currentUserId = authorizationProvider.getAuthenticatedUserId();
        return shelfService.updateShelf(currentUserId, shelfId, shelfToUpdate);
    }
}
