package itemTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;

import static org.junit.jupiter.api.Assertions.*;

public class CommentDtoTest {

    @Test
    void testGetterAndSetter() {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setAuthorName("Alice");
        comment.setCreated("2025-07-22T10:00:00");

        assertEquals(1L, comment.getId());
        assertEquals("Great item!", comment.getText());
        assertEquals("Alice", comment.getAuthorName());
        assertEquals("2025-07-22T10:00:00", comment.getCreated());
    }

    @Test
    void testTextNotBlankConstraint() {
        CommentDto comment = new CommentDto();
        comment.setText("");
        assertEquals("", comment.getText());
    }
}
