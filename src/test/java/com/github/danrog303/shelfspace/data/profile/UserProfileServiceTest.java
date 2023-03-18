package com.github.danrog303.shelfspace.data.profile;

import com.github.danrog303.shelfspace.data.shelf.ShelfRepository;
import com.github.danrog303.shelfspace.data.shelf.ShelfType;
import com.github.danrog303.shelfspace.services.authorization.UserDeleteProvider;
import com.github.danrog303.shelfspace.services.authorization.UserInfo;
import com.github.danrog303.shelfspace.services.authorization.UserInfoProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {
    private @Mock UserProfileRepository userProfileRepository;
    private @Mock UserInfoProvider userInfoProvider;
    private @Mock UserDeleteProvider userDeleteProvider;
    private @Mock ShelfRepository shelfRepository;
    private @InjectMocks UserProfileService userProfileService;

    @Test
    void getUserProfile_shouldCreateNewProfileIfNotFoundInTheDatabase() {
        when(userProfileRepository.findById(any())).thenReturn(Optional.empty());
        when(userInfoProvider.getUserInfo(any())).thenReturn(new UserInfo("a", "b", "c@d.ef"));

        UserProfile profile = userProfileService.getUserProfile("abcdefg");
        assertThat(profile).isNotNull();
        assertThat(profile.getNickname()).isEqualTo("b");
    }

    @Test
    void getUserProfile_shouldReturnProfileIfPresentInTheDatabase() {
        UserProfile mockedProfile = new UserProfile("abc", "bcd", new ArrayList<>());
        when(userProfileRepository.findById("abc")).thenReturn(Optional.of(mockedProfile));

        UserProfile profile = userProfileService.getUserProfile("abc");
        assertThat(profile).isNotNull();
        assertThat(profile.getNickname()).isEqualTo("bcd");
    }

    @Test
    void deleteUser_shouldRemoveAllObjectsRelatedToUser() {
        PrefetchedShelf shelf1 = new PrefetchedShelf("123", "Movies", ShelfType.MOVIE);
        PrefetchedShelf shelf2 = new PrefetchedShelf("456", "Games", ShelfType.OTHER);
        UserProfile mockedProfile = new UserProfile("abc", "bcd", List.of(shelf1, shelf2));
        when(userProfileRepository.findById("abc")).thenReturn(Optional.of(mockedProfile));

        UserProfile profile = userProfileService.deleteUserProfile("abc");
        assertThat(profile).isEqualTo(mockedProfile);

        verify(shelfRepository, times(2)).deleteById(any());
        verify(userDeleteProvider, times(1)).deleteUser(any());
    }
}
