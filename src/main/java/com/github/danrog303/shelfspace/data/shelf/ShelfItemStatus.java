package com.github.danrog303.shelfspace.data.shelf;

/**
 * Represents the status of a single shelf item - for example it can be "finished" or "planned".
 */
public enum ShelfItemStatus {
    /**
     * This shelf item has been finished at least one time.
     */
    FINISHED,

    /**
     * This shelf item is planned to be finished in the future.
     */
    PLANNED,

    /**
     * The completion of this item is temporarily on hold.
     */
    STALLED,

    /**
     *  This item was started, but there is no intention to complete it.
     */
    DROPPED,

    /**
     * This item is currently in progress and will be completed in the future.
     */
    IN_PROGRESS,
}
