package ru.practicum.shareit.comment.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.comment.Comment;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CommentRepository {
    private final Map<Long, Comment> comments = new HashMap<>();
    private long nextId = 1;

    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(nextId++);
        }
        comments.put(comment.getId(), comment);
        return comment;
    }

    public List<Comment> findAllByItemId(Long itemId) {
        return comments.values().stream()
                .filter(c -> c.getItem().getId().equals(itemId))
                .collect(Collectors.toList());
    }
}