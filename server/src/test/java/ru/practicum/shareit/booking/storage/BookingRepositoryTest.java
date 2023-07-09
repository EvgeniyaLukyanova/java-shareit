package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.reference.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    private User userResult;
    private User bookerResult;
    private Item itemResult;
    Booking booking1Result;
    Booking booking2Result;
    Booking booking3Result;


    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        userResult = userRepository.save(user);

        User booker = new User();
        booker.setEmail("booker@user.com");
        booker.setName("booker");
        bookerResult = userRepository.save(booker);

        Item item = new Item();
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);
        itemResult = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setStartDate(LocalDateTime.now().plusHours(2));
        booking1.setEndDate(LocalDateTime.now().plusHours(3));
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1Result = bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStartDate(LocalDateTime.now().minusHours(3));
        booking2.setEndDate(LocalDateTime.now().minusHours(2));
        booking2.setBooker(booker);
        booking2.setItem(item);
        booking2.setStatus(BookingStatus.REJECTED);
        booking2Result = bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setStartDate(LocalDateTime.now().minusHours(1));
        booking3.setEndDate(LocalDateTime.now().plusHours(1));
        booking3.setBooker(booker);
        booking3.setItem(item);
        booking3.setStatus(BookingStatus.WAITING);
        booking3Result = bookingRepository.save(booking3);
    }

    @Test
    void findByStartDateBeforeEndDateAfter() {
        List<Booking> bookings = bookingRepository.findByStartDateBeforeEndDateAfter(itemResult.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1).plusMinutes(10)
        );

        assertEquals(1, bookings.size());
        assertEquals(booking3Result.getStatus(), bookings.get(0).getStatus());
        assertEquals(booking3Result.getStartDate(), bookings.get(0).getStartDate());
        assertEquals(booking3Result.getEndDate(), bookings.get(0).getEndDate());
    }

    @Test
    void findByIdAndBookerOwner() {
        Optional<Booking> bookingOptional1 = bookingRepository.findByIdAndBookerOwner(booking1Result.getId(), userResult.getId());
        assertTrue(bookingOptional1.isPresent());
        Optional<Booking> bookingOptional2 = bookingRepository.findByIdAndBookerOwner(booking1Result.getId(), bookerResult.getId());
        assertTrue(bookingOptional2.isPresent());
        Optional<Booking> bookingOptional3 = bookingRepository.findByIdAndBookerOwner(booking1Result.getId(), 999L);
        assertTrue(bookingOptional3.isEmpty());
        Optional<Booking> bookingOptional4 = bookingRepository.findByIdAndBookerOwner(999L, userResult.getId());
        assertTrue(bookingOptional4.isEmpty());
    }

    @Test
    void findByBookerAndState() {
        List<Booking> bookings1 = bookingRepository.findByBookerAndState(bookerResult.getId(), "ALL", 2, 1L);
        assertEquals(2, bookings1.size());
        List<Booking> bookings2 = bookingRepository.findByBookerAndState(bookerResult.getId(), "FUTURE", 2, 1L);
        assertEquals(1, bookings2.size());
        assertEquals(booking1Result.getStartDate(), bookings2.get(0).getStartDate());
        List<Booking> bookings3 = bookingRepository.findByBookerAndState(bookerResult.getId(), "PAST", 2, 1L);
        assertEquals(1, bookings3.size());
        assertEquals(booking2Result.getStartDate(), bookings3.get(0).getStartDate());
        List<Booking> bookings4 = bookingRepository.findByBookerAndState(bookerResult.getId(), "CURRENT", 2, 1L);
        assertEquals(1, bookings4.size());
        assertEquals(booking3Result.getStartDate(), bookings4.get(0).getStartDate());
        List<Booking> bookings5 = bookingRepository.findByBookerAndState(bookerResult.getId(), "APPROVED", 2, 1L);
        assertEquals(1, bookings5.size());
        assertEquals(booking1Result.getStartDate(), bookings5.get(0).getStartDate());
    }

    @Test
    void findByLastBooker() {
        Booking booking = bookingRepository.findByLastBooker(itemResult.getId(), userResult.getId());
        assertEquals(booking3Result.getStartDate(), booking.getStartDate());
    }

    @Test
    void findByNextBooker() {
        Booking booking = bookingRepository.findByNextBooker(itemResult.getId(), userResult.getId());
        assertEquals(booking1Result.getStartDate(), booking.getStartDate());
    }


    @Test
    void findByListLastBooker() {
        List<Booking> bookings = bookingRepository.findByListLastBooker(userResult.getId());
        assertEquals(1, bookings.size());
        assertEquals(booking3Result.getStartDate(), bookings.get(0).getStartDate());
    }

    @Test
    void findByListNextBooker() {
        List<Booking> bookings = bookingRepository.findByListNextBooker(userResult.getId());
        assertEquals(1, bookings.size());
        assertEquals(booking1Result.getStartDate(), bookings.get(0).getStartDate());
    }

    @Test
    void findByListOfBookings() {
        List<Booking> bookings = bookingRepository.findByListOfBookings(itemResult.getId(), bookerResult.getId());
        assertEquals(1, bookings.size());
        assertEquals(booking2Result.getStartDate(), bookings.get(0).getStartDate());
    }

    @AfterEach
    void deleteAll() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}