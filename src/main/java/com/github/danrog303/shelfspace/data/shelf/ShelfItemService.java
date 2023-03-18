package com.github.danrog303.shelfspace.data.shelf;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Provides basic CRUD operations on {@link ShelfItem} instances.
 */
@Service
@RequiredArgsConstructor
public class ShelfItemService {
    private final ShelfService shelfService;
    private final ShelfRepository shelfRepository;
    private final ShelfItemIntegrityValidator shelfItemIntegrityValidator;

    /**
     * Enforces integrity constraints on the item ({@link ShelfItemIntegrityValidator}) and updates the item in the database.
     * This method only looks for the following fields: {@link ShelfItem#getTitle()}, {@link ShelfItem#getStatus()},
     * {@link ShelfItem#getRating()} and {@link ShelfItem#getFinishedCount()} fields. Other fields are ignored.
     * @throws NoSuchElementException When shelf or shelf item with the specified id does not exist.
     * @return {@link ShelfItem} instance after modification.
     */
    @PreAuthorize("@authorizationProvider.authenticatedUserId == #userId")
    public ShelfItem updateShelfItem(String userId, String shelfId, String itemId, ShelfItem item) {
        Shelf authorizedShelf = shelfService.getShelf(userId, shelfId);
        ShelfItem itemToModify = authorizedShelf
                .getItems().stream()
                .filter(it -> it.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow();

        itemToModify.setRating(item.getRating());
        itemToModify.setFinishedCount(item.getFinishedCount());
        itemToModify.setStatus(item.getStatus());
        itemToModify.setTitle(item.getTitle());
        shelfItemIntegrityValidator.enforceIntegrityConstraints(itemToModify);

        shelfRepository.save(authorizedShelf);
        return itemToModify;
    }

    /**
     * <p>Enforces integrity constraints on the item ({@link ShelfItemIntegrityValidator}) and creates new item
     * in the database. </p>
     * <p>This method only looks for the following fields: {@link ShelfItem#getTitle()}, {@link ShelfItem#getStatus()},
     * {@link ShelfItem#getRating()} and {@link ShelfItem#getFinishedCount()} fields. Other fields are
     * generated automatically and overwritten.</p>
     * @throws NoSuchElementException When shelf or shelf item with the specified id does not exist
     * @throws ShelfQuotaException When tried to exceed the 2000 shelf items quota
     * @return Created {@link ShelfItem} instance
     */
    @PreAuthorize("@authorizationProvider.authenticatedUserId == #userId")
    public ShelfItem createShelfItem(String userId, String shelfId, ShelfItem item) {
        Shelf authorizedShelf = shelfService.getShelf(userId, shelfId);
        validateShelfQuota(authorizedShelf);
        item.setItemId(UUID.randomUUID().toString());
        item.setCreationDate(new Date());
        shelfItemIntegrityValidator.enforceIntegrityConstraints(item);

        // Persist new item in the database
        authorizedShelf.getItems().add(item);
        shelfRepository.save(authorizedShelf);
        return item;
    }

    /**
     * Deletes item from the specified shelf of the specified user.
     * @throws NoSuchElementException When shelf or shelf item with the specified id does not exist
     * @return Deleted {@link ShelfItem} instance
     */
    @PreAuthorize("@authorizationProvider.authenticatedUserId == #userId")
    public ShelfItem deleteShelfItem(String userId, String shelfId, String itemId) {
        Shelf authorizedShelf = shelfService.getShelf(userId, shelfId);
        ShelfItem itemToDelete = authorizedShelf.getItems()
                .stream().filter(item -> item.getItemId().equals(itemId))
                .findFirst().orElseThrow();

        authorizedShelf.getItems().remove(itemToDelete);
        shelfRepository.save(authorizedShelf);
        return itemToDelete;
    }

    /**
     * Checks if user did not exceed his shelf item quota.
     * See {@link ShelfQuotaException} for explanation.
     */
    private void validateShelfQuota(Shelf shelf) throws ShelfQuotaException {
        if (shelf.getItems().size() >= 2000) {
            throw new ShelfQuotaException("User exceeded his shelf item quota (2000 items on a single shelf).");
        }
    }
}
