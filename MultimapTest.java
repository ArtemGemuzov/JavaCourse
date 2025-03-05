import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.ArrayList;

public class MultimapTest {

    @Test
    void testPutSingle() {
        Multimap<String, String> multimap = new Multimap<>();

        multimap.putSingle("fruit", "apple");
        multimap.putSingle("fruit", "banana");
        assertEquals(Arrays.asList("apple", "banana"), new ArrayList<>(multimap.get("fruit")));
    }

    @Test
    void testRemoveSingle() {
        Multimap<String, String> multimap = new Multimap<>();

        multimap.putSingle("fruit", "apple");
        multimap.putSingle("fruit", "banana");

        assertTrue(multimap.removeSingle("fruit", "apple"));
        assertEquals(Arrays.asList("banana"), new ArrayList<>(multimap.get("fruit")));
        assertFalse(multimap.removeSingle("fruit", "orange"));
    }

    @Test
    void testRemoveKey() {
        Multimap<String, String> multimap = new Multimap<>();
        multimap.putSingle("color", "red");
        multimap.putSingle("color", "blue");

        assertNotNull(multimap.remove("color"));
        assertTrue(multimap.get("color").isEmpty());
    }

    @Test
    void testContainsKeyAndValue() {
        Multimap<String, String> multimap = new Multimap<>();
        multimap.putSingle("animal", "dog");

        assertTrue(multimap.containsKey("animal"));
        assertTrue(multimap.containsValue("dog"));
        assertFalse(multimap.containsValue("cat"));
    }

    @Test
    void testSizeAndIsEmpty() {
        Multimap<String, String> multimap = new Multimap<>();
        assertTrue(multimap.isEmpty());

        multimap.putSingle("city", "Paris");
        assertEquals(1, multimap.size());

        multimap.putSingle("city", "London");
        assertEquals(2, multimap.size());
    }
}

