package com.github.danrog303.shelfspace.data.shelf;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class ShelfItemIntegrityValidatorTest {
    @Test @Disabled
    public void enforceIntegrityConstraints_shouldNotModifyThePassedObjectWhenConstraintsAreNotViolated() {
        String itemId = "5c20b7b0-ff0f-4c05-9e5b-41de2e3ccff3";
        ShelfItem item = new ShelfItem(itemId, "Foo", new Date(), ShelfItemStatus.PLANNED, null, null);
        ShelfItem itemClone = new ShelfItem(itemId, "Foo", new Date(), ShelfItemStatus.PLANNED, null, null);

        ShelfItemIntegrityValidator validator = new ShelfItemIntegrityValidator();
        validator.enforceIntegrityConstraints(item);

        assertThat(item.equals(itemClone)).isTrue();
    }

    @Test
    public void enforceIntegrityConstraints_shouldSetRatingToNullWhenItemStatusIsPlanned() {
        String itemId = "5c20b7b0-ff0f-4c05-9e5b-41de2e3ccff3";
        ShelfItem item = new ShelfItem(itemId, "Foo", new Date(), ShelfItemStatus.PLANNED, 5, 10);

        ShelfItemIntegrityValidator validator = new ShelfItemIntegrityValidator();
        validator.enforceIntegrityConstraints(item);

        assertThat(item.getRating()).isNull();
    }

    @Test
    public void enforceIntegrityConstraints_shouldSetFinishedCountToPositiveIntegerWhenItemStatusIsFinished() {
        String itemId = "5c20b7b0-ff0f-4c05-9e5b-41de2e3ccff3";
        ShelfItem item = new ShelfItem(itemId, "Foo", new Date(), ShelfItemStatus.FINISHED, null, null);

        ShelfItemIntegrityValidator validator = new ShelfItemIntegrityValidator();
        validator.enforceIntegrityConstraints(item);

        assertThat(item.getFinishedCount()).isPositive();
    }

    @Test
    public void enforceIntegrityConstraints_shouldSetFinishedCountToNullWhenItemStatusIsNotFinished() {
        String itemId = "5c20b7b0-ff0f-4c05-9e5b-41de2e3ccff3";
        ShelfItem item = new ShelfItem(itemId, "Foo", new Date(), ShelfItemStatus.DROPPED, 10, 3);

        ShelfItemIntegrityValidator validator = new ShelfItemIntegrityValidator();
        validator.enforceIntegrityConstraints(item);

        assertThat(item.getFinishedCount()).isNull();
    }
}
