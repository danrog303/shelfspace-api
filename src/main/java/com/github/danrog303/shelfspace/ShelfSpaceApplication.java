package com.github.danrog303.shelfspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main entrypoint of the ShelfSpace REST API.
 */
@SpringBootApplication
public class ShelfSpaceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShelfSpaceApplication.class, args);
    }


}
