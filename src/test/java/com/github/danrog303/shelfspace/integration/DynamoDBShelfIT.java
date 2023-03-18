package com.github.danrog303.shelfspace.integration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.github.danrog303.shelfspace.data.shelf.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * This test ensures that integration between the application, DynamoDBMapper and DynamoDBRepositories
 * works correctly in the {@link Shelf} model context.
 */
@SpringBootTest
public class DynamoDBShelfIT {
    private @Autowired ShelfRepository shelfRepo;
    private @Autowired DynamoDBMapper dynamoDBMapper;

    private Shelf createExampleShelf() {
        List<ShelfItem> shelfItems = new ArrayList<>();
        Shelf shelf = new Shelf();
        shelf.setShelfId(UUID.randomUUID().toString());
        shelf.setOwnerId(UUID.randomUUID().toString());
        shelf.setShelfType(ShelfType.MOVIE);
        shelf.setShelfName("Movies");
        shelf.setItems(shelfItems);

        shelfItems.add(new ShelfItem(UUID.randomUUID().toString(), "AB", new Date(), ShelfItemStatus.FINISHED, 10, 1));
        shelfItems.add(new ShelfItem(UUID.randomUUID().toString(), "BA", new Date(), ShelfItemStatus.STALLED, null, null));
        return shelf;
    }

    @Test
    public void shelf_checkIfItemCreationIsSuccessful() {
        Shelf exampleShelf = createExampleShelf();
        shelfRepo.save(exampleShelf);

        Shelf fetchedShelf = dynamoDBMapper.load(Shelf.class, exampleShelf.getShelfId());
        assertThat(fetchedShelf).isEqualTo(exampleShelf);
        dynamoDBMapper.delete(fetchedShelf);
    }

    @Test
    public void shelf_checkIfItemFetchingIsSuccessful() {
        Shelf exampleShelf = createExampleShelf();
        dynamoDBMapper.save(exampleShelf);

        Shelf fetchedshelf = shelfRepo.findById(exampleShelf.getShelfId()).orElseThrow();
        assertThat(fetchedshelf).isEqualTo(exampleShelf);
        dynamoDBMapper.delete(exampleShelf);
    }

    @Test
    public void shelf_checkIfItemDeleteIsSuccessful() {
        Shelf exampleShelf = createExampleShelf();
        dynamoDBMapper.save(exampleShelf);

        shelfRepo.delete(exampleShelf);
        Shelf fetchedshelf = dynamoDBMapper.load(Shelf.class, exampleShelf.getShelfId());
        assertThat(fetchedshelf).isNull();
    }

}
