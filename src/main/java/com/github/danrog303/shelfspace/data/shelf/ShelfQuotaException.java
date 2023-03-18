package com.github.danrog303.shelfspace.data.shelf;

import lombok.experimental.StandardException;

/**
 * Users are limited to having 20 shelves and 2000 items on each shelf. This exception is thrown,
 * when user tries to exceed this quota.
 */
@StandardException
public class ShelfQuotaException extends RuntimeException {
}
