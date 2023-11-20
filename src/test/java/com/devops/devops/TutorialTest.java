package com.devops.devops;

import com.devops.devops.model.Tutorial;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TutorialTest {

    @Test
    void testGettersAndSetters() {
        Tutorial tutorial = new Tutorial("Title", "Description", true);
        tutorial.setId(1L);

        assertEquals(1L, tutorial.getId());
        assertEquals("Title", tutorial.getTitle());
        assertEquals("Description", tutorial.getDescription());
        assertEquals(true, tutorial.isPublished());
    }
}
