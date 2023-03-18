package com.github.danrog303.shelfspace.data.profile;

import com.github.danrog303.shelfspace.data.shelf.ShelfRepository;
import com.github.danrog303.shelfspace.services.authorization.UserDeleteProvider;
import com.github.danrog303.shelfspace.services.authorization.UserInfo;
import com.github.danrog303.shelfspace.services.authorization.UserInfoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Provides basic CRUD operations on {@link UserProfile} instances.
 */
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final ShelfRepository shelfRepository;
    private final UserInfoProvider userInfoProvider;
    private final UserDeleteProvider userDeleteProvider;

    /**
     * Retrieves {@link UserProfile} instance from the database.
     * If there is no profile, creates new profile, saves it in the database and then returns.
     */
    @PreAuthorize("@authorizationProvider.authenticatedUserId == #userId")
    public UserProfile getUserProfile(String userId) {
        Optional<UserProfile> profile = userProfileRepository.findById(userId);

        if (profile.isPresent()) {
            return profile.get();
        } else {
            UserProfile newProfile = createUserProfile(userId);
            userProfileRepository.save(newProfile);
            return newProfile;
        }
    }

    /**
     * Deletes user profile, all corresponding shelves
     * and sends account deletion request to the authentication server.
     */
    @PreAuthorize("@authorizationProvider.authenticatedUserId == #userId")
    public UserProfile deleteUserProfile(String userId) {
        UserProfile profile = getUserProfile(userId);
        for (PrefetchedShelf shelf : profile.getShelves()) {
            shelfRepository.deleteById(shelf.getShelfId());
        }

        userProfileRepository.delete(profile);
        userDeleteProvider.deleteUser(userId);
        return profile;
    }

    /**
     * Retrieves information about the user from authentication server and
     * creates new user profile for the specified user.
     */
    private UserProfile createUserProfile(String userId) {
        UserInfo userInfo = userInfoProvider.getUserInfo(userId);
        List<PrefetchedShelf> shelves = new ArrayList<>();
        return new UserProfile(userInfo.getUserId(), userInfo.getNickname(), shelves);
    }
}
