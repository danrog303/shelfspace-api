package com.github.danrog303.shelfspace.data.shelf;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Exposes CRUD operations on {@link Shelf} DynamoDB table.
 */
@Repository
public interface ShelfRepository extends CrudRepository<Shelf, String> {
}
