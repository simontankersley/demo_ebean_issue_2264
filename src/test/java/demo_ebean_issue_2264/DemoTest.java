package demo_ebean_issue_2264;

import demo_ebean_issue_2264.model.Item;
import io.ebean.DB;
import io.ebean.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DemoTest {

    private static UUID id = UUID.fromString("ca3cd714-d391-46d9-ae61-8c49d55650a1");
    private static OffsetDateTime dateTime = OffsetDateTime.parse("2021-01-01T00:00:00+11:00");

    private Database db;

    @BeforeEach
    void setup() {
        db = DB.getDefault();
        db.truncate(Item.class);

        Item item = new Item();
        item.setId(id);
        item.setName("foo");
        item.setCreated(dateTime);

        db.save(item);
    }

    @Test
    void testWithUTC() {
        // this simulates running a test on a system configured with the UTC timezone
        TimeZone previous = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Item item = db.find(Item.class, id);
        assertEquals(ZoneOffset.UTC, item.getCreated().getOffset());

        TimeZone.setDefault(previous);
    }

    @Test
    void timeWithEST() {
        // this simulates running a test on a system configured with the EST timezone
        // this is the same test as testWithUTC but we get a different result because of the timezone setting
        TimeZone previous = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("EST"));

        Item item = db.find(Item.class, id);
        // setting the default timezone shouldn't impact this but it does
        assertEquals(ZoneOffset.UTC, item.getCreated().getOffset());

        TimeZone.setDefault(previous);
    }

}
