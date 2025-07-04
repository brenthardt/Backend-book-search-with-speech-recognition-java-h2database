package org.example.booksearch_backend.controller;

import org.example.booksearch_backend.entity.Book;
import org.example.booksearch_backend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.UrlResource;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/search")
    public ResponseEntity<String> searchBooks(@RequestParam String query) {
        String apiKey = "AIzaSyDEILlVNZATxuwvo6lYIc1iZMLy1Hy076w";
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(apiUrl, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching books: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null || book.getFilePath() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            Path path = Paths.get(book.getFilePath());
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + book.getTitle() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
