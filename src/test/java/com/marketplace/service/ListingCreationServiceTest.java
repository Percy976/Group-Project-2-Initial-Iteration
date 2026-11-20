package com.marketplace.service;

import com.marketplace.dao.JdbcListingDAO;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListingCreationServiceTest {

    @Test
    void validListingPassesCleanedValuesToDao() throws SQLException {
        RecordingDao dao = new RecordingDao();
        ListingCreationService service = new ListingCreationService(dao);

        int id = service.create(
                7, " Lawn mowing ", " Yard cleanup ",
                " Gardening ", " 25.5 ", " FIXED ");

        assertEquals(42, id);
        assertEquals(1, dao.calls);
        assertEquals(7, dao.providerId);
        assertEquals("Lawn mowing", dao.title);
        assertEquals("Yard cleanup", dao.description);
        assertEquals("Gardening", dao.category);
        assertEquals(new BigDecimal("25.50"), dao.price);
        assertEquals("FIXED", dao.priceType);
    }

    @Test
    void negotiableListingCanBeSavedWithoutPrice() throws SQLException {
        RecordingDao dao = new RecordingDao();
        ListingCreationService service = new ListingCreationService(dao);

        service.create(
                7, "Lawn mowing", "Yard cleanup",
                "Gardening", "", "NEGOTIABLE");

        assertEquals(1, dao.calls);
        assertNull(dao.price);
        assertEquals("NEGOTIABLE", dao.priceType);
    }

    @Test
    void invalidFieldsNeverReachDao() {
        RecordingDao dao = new RecordingDao();
        ListingCreationService service = new ListingCreationService(dao);

        assertThrows(IllegalArgumentException.class,
                () -> service.create(
                        0, "Title", "Description", "Category",
                        "25", "FIXED"));

        assertThrows(IllegalArgumentException.class,
                () -> service.create(
                        7, " ", "Description", "Category",
                        "25", "FIXED"));

        assertThrows(IllegalArgumentException.class,
                () -> service.create(
                        7, "Title", null, "Category",
                        "25", "FIXED"));

        assertThrows(IllegalArgumentException.class,
                () -> service.create(
                        7, "Title", "Description", "",
                        "25", "FIXED"));

        assertThrows(IllegalArgumentException.class,
                () -> service.create(
                        7, "Title", "Description", "Category",
                        "", "HOURLY"));

        assertThrows(IllegalArgumentException.class,
                () -> service.create(
                        7, "Title", "Description", "Category",
                        "25", "OTHER"));

        assertEquals(0, dao.calls);
    }

    @Test
    void oversizedFieldsNeverReachDao() {
        RecordingDao dao = new RecordingDao();
        ListingCreationService service = new ListingCreationService(dao);

        assertThrows(IllegalArgumentException.class,
                () -> service.create(
                        7, "T".repeat(151), "Description", "Category",
                        "25", "FIXED"));

        assertThrows(IllegalArgumentException.class,
                () -> service.create(
                        7, "Title", "D".repeat(10001), "Category",
                        "25", "FIXED"));

        assertThrows(IllegalArgumentException.class,
                () -> service.create(
                        7, "Title", "Description", "C".repeat(101),
                        "25", "FIXED"));

        assertEquals(0, dao.calls);
    }

    @Test
    void databaseFailureIsPropagatedToCaller() {
        SQLException failure = new SQLException("Test database failure");

        JdbcListingDAO dao = new JdbcListingDAO() {
            @Override
            public int create(int providerId, String title,
                              String description, String category,
                              BigDecimal price, String priceType)
                    throws SQLException {
                throw failure;
            }
        };

        ListingCreationService service = new ListingCreationService(dao);

        SQLException actual = assertThrows(
                SQLException.class,
                () -> service.create(
                        7, "Title", "Description", "Category",
                        "25", "FIXED"));

        assertSame(failure, actual);
    }

    private static class RecordingDao extends JdbcListingDAO {
        int calls;
        int providerId;
        String title;
        String description;
        String category;
        BigDecimal price;
        String priceType;

        @Override
        public int create(int providerId, String title,
                          String description, String category,
                          BigDecimal price, String priceType) {
            calls++;
            this.providerId = providerId;
            this.title = title;
            this.description = description;
            this.category = category;
            this.price = price;
            this.priceType = priceType;
            return 42;
        }
    }
}