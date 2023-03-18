package com.github.danrog303.shelfspace.data.shelf;

import org.springframework.stereotype.Component;

/**
 * Enforces {@link ShelfItem} instances to match their integrity constraints.
 */
@Component
public class ShelfItemIntegrityValidator {
    /**
     * Makes sure, that the specified {@link ShelfItem} instance passes the following checks:
     *
     * <ul>
     * <li>when {@link ShelfItem#getStatus()} is equal to {@link ShelfItemStatus#PLANNED}, {@link ShelfItem#getRating()}
     * should return null</li>
     * <li>when {@link ShelfItem#getStatus()} is equal to {@link ShelfItemStatus#FINISHED}, {@link ShelfItem#getFinishedCount()}
     * must return a positive, non-null integer</li>
     * <li>when {@link ShelfItem#getStatus()} is not equal to {@link ShelfItemStatus#FINISHED}, {@link ShelfItem#getFinishedCount()}
     * must return null</li>
     * </ul>
     *
     * If the given instance does not pass listed checks, function assigns some default values to fields that are invalid.
     */
    public void enforceIntegrityConstraints(ShelfItem item) {
        if (item.getStatus() == ShelfItemStatus.PLANNED && item.getRating() != null) {
            item.setRating(null);
        }

        if (item.getStatus() == ShelfItemStatus.FINISHED && (item.getFinishedCount() == null || item.getFinishedCount() <= 0)) {
            item.setFinishedCount(1);
        }

        if (item.getStatus() != ShelfItemStatus.FINISHED) {
            item.setFinishedCount(null);
        }
    }
}
