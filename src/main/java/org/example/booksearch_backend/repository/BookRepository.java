package org.example.booksearch_backend.repository;
import org.example.booksearch_backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}

