package com.github.danrog303.shelfspace.data.shelf;

import com.github.danrog303.shelfspace.data.profile.PrefetchedShelf;
import com.github.danrog303.shelfspace.data.profile.UserProfile;
import com.github.danrog303.shelfspace.data.profile.UserProfileRepository;
import com.github.danrog303.shelfspace.data.profile.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelfServiceTest {
    private @Mock UserProfileService userProfileService;
    private @Mock UserProfileRepository userProfileRepository;
    private @Mock ShelfRepository shelfRepository;
    private @InjectMocks ShelfService shelfService;

    @Test
    void createNewShelf_shouldCreateShelfInstancesInBothTables() {
        when(userProfileService.getUserProfile("abc")).thenReturn(
                new UserProfile("abc", "ABCDEF", new ArrayList<>()));

        PrefetchedShelf newShelf = new PrefetchedShelf(null, "abc", ShelfType.GAME);
        PrefetchedShelf createdShelf = shelfService.createNewShelf("abc", newShelf);

        assertThat(createdShelf).isNotNull();
        assertThat(createdShelf.getShelfId()).isNotNull();

        verify(shelfRepository, times(1)).save(any());
        verify(userProfileRepository, times(1)).save(any());
    }

    @Test
    void createNewShelf_shouldThrowWhenQuotaExceeded() {
        when(userProfileService.getUserProfile("abc")).thenReturn(
                new UserProfile("abc", "ABCDEF", createShelves(20)));

        assertThatThrownBy(() ->
                shelfService.createNewShelf("abc", new PrefetchedShelf("xxx", "Movies", ShelfType.GAME))
        ).isInstanceOf(ShelfQuotaException.class);

        verify(shelfRepository, never()).save(any());
    }

    @Test
    void deleteShelf_shouldThrowWhenShelfWasNotFound() {
        String mockedUserId = "38860c35-637c-4d10-9aad-fc1353ee3aa0";
        String mockedShelfId = "64e9e6e8-3950-4581-8d44-402e1c9e6064";
        UserProfile mockedUserProfile = new UserProfile(mockedUserId, "James", List.of());

        when(userProfileService.getUserProfile(mockedUserId)).thenReturn(mockedUserProfile);
        when(shelfRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shelfService.deleteShelf(mockedUserId, mockedShelfId)).isInstanceOf(NoSuchElementException.class);
        verify(shelfRepository, never()).delete(any());
        verify(shelfRepository, never()).deleteById(any());
    }

    @Test
    void deleteShelf_shouldDeleteFromBothTablesWhenShelfPresent() {
        String mockedUserId = "38860c35-637c-4d10-9aad-fc1353ee3aa0";
        String mockedShelfId = "64e9e6e8-3950-4581-8d44-402e1c9e6064";
        PrefetchedShelf mockedPrefetchedShelf = new PrefetchedShelf(mockedShelfId, "Movies", ShelfType.OTHER);
        Shelf mockedShelf = new Shelf(mockedShelfId, "Movies", mockedUserId, ShelfType.MOVIE, List.of());
        UserProfile mockedUserProfile = new UserProfile(mockedUserId, "James", new ArrayList<>(List.of(mockedPrefetchedShelf)));

        when(shelfRepository.findById(mockedShelfId)).thenReturn(Optional.of(mockedShelf));
        when(userProfileService.getUserProfile(mockedUserId)).thenReturn(mockedUserProfile);
        shelfService.deleteShelf(mockedUserId, mockedShelfId);

        assertThat(mockedUserProfile.getShelves()).isEmpty();

        verify(shelfRepository, times(1)).delete(mockedShelf);
        verify(userProfileRepository, times(1)).save(mockedUserProfile);
    }

    @Test
    void getShelf_shouldThrowWhenShelfNotFound() {
        String mockedUserId = "38860c35-637c-4d10-9aad-fc1353ee3aa0";
        String mockedShelfId = "64e9e6e8-3950-4581-8d44-402e1c9e6064";
        when(shelfRepository.findById(mockedShelfId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shelfService.getShelf(mockedUserId, mockedShelfId)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getShelf_shouldThrowWhenShelfDidNotBelongToTheRequestedUser() {
        String mockedShelfOwnerId = "38860c35-637c-4d10-9aad-fc1353ee3aa0";
        String mockedRequestedUserId = "093b9e02-7722-4eee-bdb0-8847fe04d05f";
        String mockedShelfId = "08dd20f6-8b99-43d0-85e6-9017f7ed0c9a";
        Shelf mockedShelf = new Shelf(mockedShelfId, "Games", mockedShelfOwnerId, ShelfType.GAME, List.of());

        when(shelfRepository.findById(mockedShelfId)).thenReturn(Optional.of(mockedShelf));

        assertThatThrownBy(() -> shelfService.getShelf(mockedRequestedUserId, mockedShelfId)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getShelf_shouldReturnShelfWhenPresent() {
        String mockedUserId = "38860c35-637c-4d10-9aad-fc1353ee3aa0";
        String mockedShelfId = "08dd20f6-8b99-43d0-85e6-9017f7ed0c9a";
        Shelf mockedShelf = new Shelf(mockedShelfId, "Games", mockedUserId, ShelfType.BOOK, List.of());
        when(shelfRepository.findById(mockedShelfId)).thenReturn(Optional.of(mockedShelf));

        assertThat(shelfService.getShelf(mockedUserId, mockedShelfId)).isEqualTo(mockedShelf);
        verify(shelfRepository, times(1)).findById(mockedShelfId);
    }

    @Test
    void updateShelf_shouldThrowWhenShelfNotFound() {
        String mockedUserId = "38860c35-637c-4d10-9aad-fc1353ee3aa0";
        String mockedShelfId = "428ddf23-2682-4d44-a5ec-052c0bb6d620";
        PrefetchedShelf mockedPrefetchedShelf = new PrefetchedShelf(mockedShelfId, "Games", ShelfType.MOVIE);
        UserProfile mockedUserProfile = new UserProfile(mockedUserId, "James", List.of());

        when(userProfileService.getUserProfile(mockedUserId)).thenReturn(mockedUserProfile);
        when(shelfRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shelfService.updateShelf(mockedUserId, mockedShelfId, mockedPrefetchedShelf))
                .isInstanceOf(NoSuchElementException.class);
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void updateShelf_shouldUpdateShelfIfPresent() {
        String mockedUserId = "38860c35-637c-4d10-9aad-fc1353ee3aa0";
        String mockedShelfId = "428ddf23-2682-4d44-a5ec-052c0bb6d620";
        PrefetchedShelf mockedPrefetchedShelf = new PrefetchedShelf(mockedShelfId, "Games", ShelfType.MOVIE);
        Shelf mockedShelf = new Shelf(mockedShelfId, "Games", mockedUserId, ShelfType.GAME, List.of());
        UserProfile mockedUser = new UserProfile(mockedUserId, "Ann", new ArrayList<>(List.of(mockedPrefetchedShelf)));

        when(userProfileService.getUserProfile(mockedUserId)).thenReturn(mockedUser);
        when(shelfRepository.findById(mockedShelfId)).thenReturn(Optional.of(mockedShelf));

        PrefetchedShelf changes = new PrefetchedShelf(null, "Sci-fi movies", ShelfType.GAME);
        Shelf modifiedShelf = shelfService.updateShelf(mockedUserId, mockedShelfId, changes);

        assertThat(modifiedShelf).isNotNull();
        assertThat(modifiedShelf.getShelfId()).isEqualTo(mockedShelfId);
        assertThat(modifiedShelf.getShelfName()).isEqualTo("Sci-fi movies");
        assertThat(modifiedShelf.getShelfType()).isEqualTo(ShelfType.GAME);

        verify(shelfRepository, times(1)).save(any());
        verify(userProfileRepository, times(1)).save(any());
    }

    private List<PrefetchedShelf> createShelves(int n) {
        ArrayList<PrefetchedShelf> shelves = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String shelfId = "d90ffb7c-2762-4252-8414-8bf2cbae9057-" + i;
            shelves.add(new PrefetchedShelf(shelfId, "Movies", ShelfType.MOVIE));
        }
        return shelves;
    }
}
