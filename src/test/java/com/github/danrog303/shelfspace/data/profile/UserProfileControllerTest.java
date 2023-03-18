package com.github.danrog303.shelfspace.data.profile;

import com.github.danrog303.shelfspace.data.shelf.Shelf;
import com.github.danrog303.shelfspace.errors.ErrorResponseAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class UserProfileControllerTest {
    private @Mock UserProfileService userProfileService;
    private @InjectMocks UserProfileController userProfileController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userProfileController)
                .setControllerAdvice(new ErrorResponseAdvice())
                .build();
    }

    @Test
    public void getUserProfile_shouldReturnUserProfile() throws Exception {
        String mockedUserId = "a8426064-b5ec-11ed-afa1-0242ac120002";
        when(userProfileService.getUserProfile(mockedUserId)).thenReturn(new UserProfile(
                mockedUserId, "John", new ArrayList<PrefetchedShelf>()
        ));

        MockHttpServletResponse response = mockMvc
                .perform(get("/users/" + mockedUserId)
                .with(jwt()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        verify(userProfileService, times(1)).getUserProfile(mockedUserId);
    }

    @Test
    public void deleteUserProfile_shouldReturnDeletedUserProfile() throws Exception {
        String mockedUserId = "a8426064-b5ec-11ed-afa1-0242ac120002";
        when(userProfileService.deleteUserProfile(mockedUserId)).thenReturn(new UserProfile(
                mockedUserId, "John", new ArrayList<PrefetchedShelf>()
        ));

        MockHttpServletResponse response = mockMvc
                .perform(delete("/users/" + mockedUserId)
                .with(jwt()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        verify(userProfileService, times(1)).deleteUserProfile(mockedUserId);
    }
}
