package com.github.danrog303.shelfspace.integration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.github.danrog303.shelfspace.data.profile.UserProfile;
import com.github.danrog303.shelfspace.data.profile.UserProfileRepository;
import com.github.danrog303.shelfspace.data.profile.PrefetchedShelf;
import com.github.danrog303.shelfspace.data.shelf.ShelfType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

/**
 * This test ensures that integration between the application, DynamoDBMapper and DynamoDBRepositories
 * works correctly in the {@link UserProfile} model context.
 */
@SpringBootTest
public class DynamoDBUserProfileIT {
    private @Autowired UserProfileRepository userProfileRepo;
    private @Autowired DynamoDBMapper dynamoDBMapper;

    private UserProfile createExampleUserProfile(String shelfName1, String shelfName2) {
        PrefetchedShelf shelf1 = new PrefetchedShelf(UUID.randomUUID().toString(), shelfName1, ShelfType.BOOK);
        PrefetchedShelf shelf2 = new PrefetchedShelf(UUID.randomUUID().toString(), shelfName2, ShelfType.GAME);
        return new UserProfile(UUID.randomUUID().toString(), "Some user", List.of(shelf1, shelf2));
    }

    @Test
    public void userProfile_checkIfItemCreationIsSuccessful() {
        UserProfile exampleProfile = createExampleUserProfile("Books", "PC Games");
        userProfileRepo.save(exampleProfile);

        UserProfile fetchedProfile = dynamoDBMapper.load(UserProfile.class, exampleProfile.getUserId());
        assertThat(fetchedProfile).isEqualTo(exampleProfile);
        dynamoDBMapper.delete(fetchedProfile);
    }

    @Test
    public void userProfile_checkIfItemFetchingIsSuccessful() {
        UserProfile exampleProfile = createExampleUserProfile("Anime", "Manga");
        dynamoDBMapper.save(exampleProfile);

        UserProfile fetchedProfile = userProfileRepo.findById(exampleProfile.getUserId()).orElseThrow();
        assertThat(fetchedProfile).isEqualTo(exampleProfile);
        dynamoDBMapper.delete(exampleProfile);
    }

    @Test
    public void userProfile_checkIfItemDeleteIsSuccessful() {
        UserProfile exampleProfile = createExampleUserProfile("Anime", "Manga");
        dynamoDBMapper.save(exampleProfile);

        userProfileRepo.delete(exampleProfile);
        UserProfile fetchedProfile = dynamoDBMapper.load(UserProfile.class, exampleProfile.getUserId());
        assertThat(fetchedProfile).isNull();
    }
}
