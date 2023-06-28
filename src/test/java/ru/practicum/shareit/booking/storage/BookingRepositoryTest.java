package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.AfterEach;
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

    @Test
    void findByStartDateBeforeEndDateAfter() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        userRepository.save(user);

        User booker = new User();
        booker.setEmail("booker@user.com");
        booker.setName("booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);
        Item itemResult = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.of(2023,6,16,10,0,0));
        booking.setEndDate(LocalDateTime.of(2023,6,18,10,0,0));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Booking booking1 = new Booking();
        booking1.setStartDate(LocalDateTime.of(2023,6,16,10,0,0));
        booking1.setEndDate(LocalDateTime.of(2023,6,18,10,0,0));
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStartDate(LocalDateTime.of(2023,6,18,11,0,0));
        booking2.setEndDate(LocalDateTime.of(2023,6,19,11,0,0));
        booking2.setBooker(booker);
        booking2.setItem(item);
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByStartDateBeforeEndDateAfter(itemResult.getId(),
                LocalDateTime.of(2023,6,15,11,0,0),
                LocalDateTime.of(2023,6,17,11,0,0));

        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.APPROVED, bookings.get(0).getStatus());
        assertEquals(LocalDateTime.of(2023,6,16,10,0,0), bookings.get(0).getStartDate());
        assertEquals(LocalDateTime.of(2023,6,18,10,0,0), bookings.get(0).getEndDate());
    }

    @Test
    void findByIdAndBookerOwner() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        User userResult = userRepository.save(user);

        User booker = new User();
        booker.setEmail("booker@user.com");
        booker.setName("booker");
        User bookerResult = userRepository.save(booker);

        Item item = new Item();
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(userResult);
        Item itemResult = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.of(2023,6,16,10,0,0));
        booking.setEndDate(LocalDateTime.of(2023,6,18,10,0,0));
        booking.setBooker(bookerResult);
        booking.setItem(itemResult);
        booking.setStatus(BookingStatus.APPROVED);
        Booking bookingResult = bookingRepository.save(booking);

        Optional<Booking> bookingOptional1 = bookingRepository.findByIdAndBookerOwner(bookingResult.getId(), userResult.getId());
        assertTrue(bookingOptional1.isPresent());
        Optional<Booking> bookingOptional2 = bookingRepository.findByIdAndBookerOwner(bookingResult.getId(), bookerResult.getId());
        assertTrue(bookingOptional2.isPresent());
        Optional<Booking> bookingOptional3 = bookingRepository.findByIdAndBookerOwner(bookingResult.getId(), 999L);
        assertTrue(bookingOptional3.isEmpty());
        Optional<Booking> bookingOptional4 = bookingRepository.findByIdAndBookerOwner(999L, userResult.getId());
        assertTrue(bookingOptional4.isEmpty());
    }

    @Test
    void findByBookerAndState() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        User userResult = userRepository.save(user);

        User booker = new User();
        booker.setEmail("booker@user.com");
        booker.setName("booker");
        User bookerResult = userRepository.save(booker);

        Item item = new Item();
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);
        Item itemResult = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setStartDate(LocalDateTime.now().plusHours(1));
        booking1.setEndDate(LocalDateTime.now().plusHours(2));
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStatus(BookingStatus.APPROVED);
        Booking booking1Result = bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStartDate(LocalDateTime.now().minusHours(3));
        booking2.setEndDate(LocalDateTime.now().minusHours(2));
        booking2.setBooker(booker);
        booking2.setItem(item);
        booking2.setStatus(BookingStatus.REJECTED);
        Booking booking2Result = bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setStartDate(LocalDateTime.now().minusHours(3));
        booking3.setEndDate(LocalDateTime.now().plusHours(2));
        booking3.setBooker(booker);
        booking3.setItem(item);
        booking3.setStatus(BookingStatus.WAITING);
        Booking booking3Result = bookingRepository.save(booking3);

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
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        User userResult = userRepository.save(user);

        User booker = new User();
        booker.setEmail("booker@user.com");
        booker.setName("booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);
        Item itemResult = itemRepository.save(item);

        Booking booking2 = new Booking();
        booking2.setStartDate(LocalDateTime.now().minusHours(3));
        booking2.setEndDate(LocalDateTime.now().minusHours(2));
        booking2.setBooker(booker);
        booking2.setItem(item);
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);

        Booking booking = bookingRepository.findByLastBooker(itemResult.getId(), userResult.getId());
        assertEquals(booking2.getStartDate(), booking.getStartDate());
    }

    @Test
    void findByNextBooker() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        User userResult = userRepository.save(user);

        User booker = new User();
        booker.setEmail("booker@user.com");
        booker.setName("booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);
        Item itemResult = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setStartDate(LocalDateTime.now().plusHours(1));
        booking1.setEndDate(LocalDateTime.now().plusHours(2));
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking1);

        Booking booking = bookingRepository.findByNextBooker(itemResult.getId(), userResult.getId());
        assertEquals(booking1.getStartDate(), booking.getStartDate());
    }


    @Test
    void findByListLastBooker() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        User userResult = userRepository.save(user);

        User booker = new User();
        booker.setEmail("booker@user.com");
        booker.setName("booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);
        Item itemResult = itemRepository.save(item);

        Booking booking2 = new Booking();
        booking2.setStartDate(LocalDateTime.now().minusHours(3));
        booking2.setEndDate(LocalDateTime.now().minusHours(2));
        booking2.setBooker(booker);
        booking2.setItem(item);
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByListLastBooker(userResult.getId());
        assertEquals(1, bookings.size());
        assertEquals(booking2.getStartDate(), bookings.get(0).getStartDate());
    }

    @Test
    void findByListNextBooker() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        User userResult = userRepository.save(user);

        User booker = new User();
        booker.setEmail("booker@user.com");
        booker.setName("booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);
        Item itemResult = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setStartDate(LocalDateTime.now().plusHours(1));
        booking1.setEndDate(LocalDateTime.now().plusHours(2));
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking1);

        List<Booking> bookings = bookingRepository.findByListNextBooker(userResult.getId());
        assertEquals(1, bookings.size());
        assertEquals(booking1.getStartDate(), bookings.get(0).getStartDate());
    }

    @Test
    void findByListOfBookings() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        User userResult = userRepository.save(user);

        User booker = new User();
        booker.setEmail("booker@user.com");
        booker.setName("booker");
        User bookerResult = userRepository.save(booker);

        Item item = new Item();
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);
        Item itemResult = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setStartDate(LocalDateTime.now().minusHours(3));
        booking1.setEndDate(LocalDateTime.now().minusHours(2));
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking1);

        List<Booking> bookings = bookingRepository.findByListOfBookings(itemResult.getId(), bookerResult.getId());
        assertEquals(1, bookings.size());
        assertEquals(booking1.getStartDate(), bookings.get(0).getStartDate());
    }

    @AfterEach
    void deleteAll() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}