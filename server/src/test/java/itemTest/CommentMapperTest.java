package itemTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentMapperTest {

    @Test
    void toDto_shouldMapAllFields() {
        User author = new User();
        author.setName("Alice");

        Item item = new Item();

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great!");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2025, 7, 24, 12, 0));

        CommentDto dto = CommentMapper.toDto(comment);

        assertEquals(1L, dto.getId());
        assertEquals("Great!", dto.getText());
        assertEquals("Alice", dto.getAuthorName());
        assertEquals("2025-07-24T12:00", dto.getCreated().substring(0,16)); // проверяем начало строки времени
    }

    @Test
    void toComment_shouldMapAllFields() {
        CommentDto dto = new CommentDto();
        dto.setText("Nice item");

        User author = new User();
        author.setName("Bob");

        Item item = new Item();

        Comment comment = CommentMapper.toComment(dto, author, item);

        assertEquals("Nice item", comment.getText());
        assertEquals(author, comment.getAuthor());
        assertEquals(item, comment.getItem());
        assertNotNull(comment.getCreated());
    }
}
