package com.github.danrog303.shelfspace.data.profile;

import com.github.danrog303.shelfspace.data.shelf.ShelfRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;

/**
 * Exposes REST endpoints with basic operations on user's profile.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name="Operations on user's profile")
public class UserProfileController {
    private final AwsCredentialsProvider awsCredentials;
    private final Region awsRegion;
    private final UserProfileService userProfileService;
    private final ShelfRepository repo;

    @GetMapping("/{userId}")
    @Operation(summary="Fetches user profile information", description="Requires to be authenticated as the specified user")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="User info successfully fetched"),
            @ApiResponse(responseCode="401", description="Access token was not specified"),
            @ApiResponse(responseCode="403", description="Specified user id was different from the authenticated user id")
    })
    public UserProfile getUserProfile(@PathVariable String userId) {
        return userProfileService.getUserProfile(userId);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary="Deletes the user account", description="Requires to be authenticated as the specified user")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="User was successfully deleted. Returns details of the deleted user."),
            @ApiResponse(responseCode="401", description="Access token was not specified"),
            @ApiResponse(responseCode="403", description="Specified user id was different from the authenticated user id")
    })
    public UserProfile deleteUserProfile(@PathVariable String userId) {
        return userProfileService.deleteUserProfile(userId);
    }
}
