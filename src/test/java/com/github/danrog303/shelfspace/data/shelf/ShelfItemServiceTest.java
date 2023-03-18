package com.github.danrog303.shelfspace.data.shelf;

import com.github.danrog303.shelfspace.services.authorization.AuthorizationProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelfItemServiceTest {
    private @Mock ShelfService shelfService;
    private @Mock ShelfRepository shelfRepository;
    private @Mock AuthorizationProvider authorizationProvider;
    private @Mock ShelfItemIntegrityValidator shelfItemIntegrityValidator;
    private @InjectMocks ShelfItemService shelfItemService;

    @Test
    public void updateShelfItem_shouldCorrectlyModifyTheShelfItemWhenItemWasFound() {
        String mockedShelfId = "35bb64a4-00a6-4ab1-ac29-27818490434a";
        String mockedUserId = "4c4bd402-8094-4191-b1f9-f76388af3cbe";
        String mockedItemId = "2e2b043c-e338-4b99-ab3e-2fc6279cce3a";
        ShelfItem mockedShelfItem = new ShelfItem(mockedItemId, "Movie 1", new Date(), ShelfItemStatus.PLANNED, null, null);
        Shelf mockedShelf = new Shelf(mockedShelfId, "Movies", mockedUserId, ShelfType.GAME, new ArrayList<>(List.of(mockedShelfItem)));

        when(shelfService.getShelf(mockedUserId, mockedShelfId)).thenReturn(mockedShelf);

        ShelfItem changes = new ShelfItem(null, "Movie 2", null, ShelfItemStatus.FINISHED, 1, 1);
        ShelfItem modifiedItem = shelfItemService.updateShelfItem(mockedUserId, mockedShelfId, mockedItemId, changes);

        assertThat(modifiedItem.getItemId()).isEqualTo(mockedItemId);
        assertThat(modifiedItem.getTitle()).isEqualTo("Movie 2");

        verify(shelfRepository, times(1)).save(any());
    }

    @Test
    public void updateShelfItem_shouldThrowWhenShelfOrShelfItemNotFound() {
        String mockedShelfId = "3eba990b-1a3d-4ff0-9d7c-908fb636c2f5";
        String mockedUserId = "317f17da-a8de-456c-b01b-b8e752f38568";
        Shelf mockedShelf = new Shelf(mockedShelfId, "Movies", mockedUserId, ShelfType.BOOK, List.of());

        when(shelfService.getShelf(mockedUserId, mockedShelfId)).thenReturn(mockedShelf);

        ShelfItem changes = new ShelfItem(null, "Movie 2", null, ShelfItemStatus.FINISHED, 1, 1);
        assertThatThrownBy(() -> shelfItemService.updateShelfItem(mockedUserId, mockedShelfId, "abc", changes))
                .isInstanceOf(NoSuchElementException.class);

        verify(shelfRepository, never()).save(any());
    }

    @Test
    public void createShelfItem_shouldSuccessfullyCreateNewItem() {
        String mockedShelfId = "e1430e5e-4c36-4e40-a98f-339c516203fd";
        String mockedUserId = "1c5a8d2f-10bc-4f72-89e0-3dab1bea56e3";
        Shelf mockedShelf = new Shelf(mockedShelfId, "Movies", mockedUserId, ShelfType.BOOK, new ArrayList<>());

        when(shelfService.getShelf(mockedUserId, mockedShelfId)).thenReturn(mockedShelf);

        ShelfItem newItem = new ShelfItem(null, "Title", new Date(), ShelfItemStatus.FINISHED, 3, null);
        ShelfItem createdItem = shelfItemService.createShelfItem(mockedUserId, mockedShelfId, newItem);

        assertThat(createdItem).isNotNull();
        assertThat(createdItem.getItemId()).isNotNull();
        verify(shelfRepository, times(1)).save(any());
    }

    @Test
    public void createShelfItem_shouldThrowWhenTriedToExceedQuota() {
        String mockedShelfId = "e082021c-5f9a-488b-9a54-8390bf99a897";
        String mockedUserId = "4bf6296b-44d2-4450-8e2e-d74cea590fa3";
        Shelf mockedShelf = new Shelf(mockedShelfId, "Movies", mockedUserId, ShelfType.OTHER, generateShelfItems(2000));
        when(shelfService.getShelf(mockedUserId, mockedShelfId)).thenReturn(mockedShelf);

        ShelfItem newShelfItem = new ShelfItem(null, "The title", new Date(), ShelfItemStatus.FINISHED, 1, 1);
        assertThatThrownBy(() -> shelfItemService.createShelfItem(mockedUserId, mockedShelfId, newShelfItem))
                .isInstanceOf(ShelfQuotaException.class);

        verify(shelfRepository, never()).save(any());
    }

    @Test
    public void createShelfItem_shouldThrowWhenShelfDoesNotExist() {
        String mockedShelfId = "e082021c-5f9a-488b-9a54-8390bf99a897";
        String mockedUserId = "4bf6296b-44d2-4450-8e2e-d74cea590fa3";
        when(shelfService.getShelf(any(), any())).thenThrow(new NoSuchElementException());

        ShelfItem newShelfItem = new ShelfItem(null, "The title", new Date(), ShelfItemStatus.FINISHED, 1, 1);
        assertThatThrownBy(() -> shelfItemService.createShelfItem(mockedUserId, mockedShelfId, newShelfItem))
                .isInstanceOf(NoSuchElementException.class);

        verify(shelfRepository, never()).save(any());
    }

    @Test
    public void deleteShelfItem_shouldThrowWhenShelfOfShelfItemDoesNotExist() {
        String mockedShelfId = "e082021c-5f9a-488b-9a54-8390bf99a897";
        String mockedUserId = "4bf6296b-44d2-4450-8e2e-d74cea590fa3";
        String mockedItemId = "8f5a9c18-da8e-4cea-952d-68de1f284360";
        Shelf mockedShelf = new Shelf(mockedShelfId, "Movies", mockedUserId, ShelfType.MOVIE, new ArrayList<>());

        when(shelfService.getShelf(mockedUserId, mockedShelfId)).thenReturn(mockedShelf);

        assertThatThrownBy(() -> shelfItemService.deleteShelfItem(mockedUserId, mockedShelfId, mockedItemId))
                .isInstanceOf(NoSuchElementException.class);

        verify(shelfRepository, never()).save(any());
    }

    @Test
    public void deleteShelfItem_shouldSuccessfullyDeleteItemWhenExists() {
        String mockedShelfId = "e082021c-5f9a-488b-9a54-8390bf99a897";
        String mockedUserId = "4bf6296b-44d2-4450-8e2e-d74cea590fa3";
        String mockedItemId = "8f5a9c18-da8e-4cea-952d-68de1f284360";
        ShelfItem mockedItem = new ShelfItem(mockedItemId, "Foobar", new Date(), ShelfItemStatus.PLANNED, null, null);
        Shelf mockedShelf = new Shelf(mockedShelfId, "Movies", mockedUserId, ShelfType.BOOK, new ArrayList<ShelfItem>(List.of(mockedItem)));

        when(shelfService.getShelf(mockedUserId, mockedShelfId)).thenReturn(mockedShelf);

        ShelfItem deletedItem = shelfItemService.deleteShelfItem(mockedUserId, mockedShelfId, mockedItemId);
        assertThat(deletedItem).isEqualTo(mockedItem);
        assertThat(mockedShelf.getItems()).isEmpty();

        verify(shelfRepository, times(1)).save(mockedShelf);
    }

    private List<ShelfItem> generateShelfItems(int n) {
        ArrayList<ShelfItem> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ShelfItem item = new ShelfItem("item"+i, "Some item", new Date(), ShelfItemStatus.PLANNED, null, null);
            result.add(item);
        }
        return result;
    }
}
