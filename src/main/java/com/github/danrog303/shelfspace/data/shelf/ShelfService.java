package com.github.danrog303.shelfspace.data.shelf;

import com.github.danrog303.shelfspace.data.profile.PrefetchedShelf;
import com.github.danrog303.shelfspace.data.profile.UserProfile;
import com.github.danrog303.shelfspace.data.profile.UserProfileRepository;
import com.github.danrog303.shelfspace.data.profile.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Provides basic CRUD operations on {@link Shelf} instances.
 */
@Service
@RequiredArgsConstructor
public class ShelfService {
    private final UserProfileService userProfileService;
    private final UserProfileRepository userProfileRepository;
    private final ShelfRepository shelfRepository;

    /**
     * Creates new shelf for the specified user.
     * The ID of the passed {@link PrefetchedShelf} instance is ignored and regenerated.
     * @throws ShelfQuotaException When tried to exceed the 20 shelves quota
     */
    @PreAuthorize("@authorizationProvider.authenticatedUserId == #userId")
    public PrefetchedShelf createNewShelf(String userId, PrefetchedShelf shelfToCreate) {
        UserProfile profile = userProfileService.getUserProfile(userId);
        validateShelfQuota(profile);

        // Persist prefetched shelf instance in the user profiles table
        String shelfId = UUID.randomUUID().toString();
        shelfToCreate.setShelfId(shelfId);
        profile.getShelves().add(shelfToCreate);
        userProfileRepository.save(profile);

        // Persist actual shelf instance in the shelves tables
        List<ShelfItem> items = new ArrayList<>();
        Shelf shelf = new Shelf(shelfId, shelfToCreate.getShelfName(), userId, shelfToCreate.getShelfType(), items);
        shelfRepository.save(shelf);
        return shelfToCreate;
    }

    /**
     * Permanently deletes the specified shelf from user's shelf collection.
     * @throws NoSuchElementException When shelf with the specified id was not found
     */
    @PreAuthorize("@authorizationProvider.authenticatedUserId == #userId")
    public PrefetchedShelf deleteShelf(String userId, String shelfId) {
        UserProfile user = userProfileService.getUserProfile(userId);

        // Delete actual shelf instance from the shelves table
        Optional<Shelf> shelf = shelfRepository.findById(shelfId);
        if (shelf.isEmpty() || !shelf.get().getOwnerId().equals(userId)) {
            throw new NoSuchElementException("The authenticated user does not have a shelf with the specified id.");
        }
        shelfRepository.delete(shelf.get());

        // Delete prefetched shelf instance from the user profiles table
        PrefetchedShelf prefetchedShelf = user.getShelves().stream()
                .filter(sh -> sh.getShelfId().equals(shelf.get().getShelfId()))
                .findFirst().orElseThrow();
        user.getShelves().remove(prefetchedShelf);
        userProfileRepository.save(user);

        return prefetchedShelf;
    }

    /**
     * Tries to fetch the specified shelf instance from the user's shelf collection.
     * @throws NoSuchElementException When shelf was not found, or it did not belong to the specified user
     */
    @PreAuthorize("@authorizationProvider.authenticatedUserId == #userId")
    public Shelf getShelf(String userId, String shelfId) {
        Optional<Shelf> shelf = shelfRepository.findById(shelfId);

        if (shelf.isEmpty() || !shelf.get().getOwnerId().equals(userId)) {
            throw new NoSuchElementException("The specified user does not have a shelf with the specified id.");
        } else {
            return shelf.get();
        }
    }

    /**
     * Updates the specified shelf. The ID of the passed {@link PrefetchedShelf} instance is ignored, because
     * id value is read from the argument.
     * @throws NoSuchElementException When shelf was not found, or it did not belong to the specified user
     */
    @PreAuthorize("@authorizationProvider.authenticatedUserId == #userId")
    public Shelf updateShelf(String userId, String shelfId, PrefetchedShelf newShelf) {
        UserProfile profile = userProfileService.getUserProfile(userId);
        Optional<Shelf> shelf = shelfRepository.findById(shelfId);

        if (shelf.isEmpty() || !shelf.get().getOwnerId().equals(userId)) {
            throw new NoSuchElementException("The authenticated user does not have a shelf with the specified id.");
        }

        // Update the actual shelf instance
        Shelf actualShelf = shelf.get();
        actualShelf.setShelfName(newShelf.getShelfName());
        actualShelf.setShelfType(newShelf.getShelfType());
        shelfRepository.save(actualShelf);

        // Update the prefetched shelf instance
        newShelf.setShelfId(actualShelf.getShelfId());
        profile.getShelves().removeIf(sh -> sh.getShelfId().equals(shelfId));
        profile.getShelves().add(newShelf);
        userProfileRepository.save(profile);

        return actualShelf;
    }

    /**
     * Checks if user did not exceed his shelf quota.
     * See {@link ShelfQuotaException} for explanation.
     */
    private void validateShelfQuota(UserProfile userProfile) throws ShelfQuotaException {
        if (userProfile.getShelves().size() >= 20) {
            throw new ShelfQuotaException("User exceeded his shelf quota (20 shelves on a single account).");
        }
    }
}
