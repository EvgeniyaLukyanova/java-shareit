package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.reference.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
//@SpringBootTest(
//        properties = "db.name=test",
//        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {
    private final EntityManager em;
    private BookingService service;
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        service = new BookingServiceImpl(repository,
                userRepository,
                itemRepository);
    }

    @Test
    void requestApprovReject() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        em.persist(user);

        User booker = new User();
        booker.setEmail("booker@user.com");
        booker.setName("booker");
        em.persist(booker);

        Item item = new Item();
        item.setName("Щётка для обуви");
        item.setDescription("Щётка для обуви");
        item.setAvailable(true);
        item.setOwner(user);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.of(2023,6,16,10,0,0));
        booking.setEndDate(LocalDateTime.of(2023,6,17,10,0,0));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        em.flush();

        BookingDtoResponse bookingDtoResponse1 = service.requestApprovReject(booking.getId(), user.getId(), true);

        assertEquals(bookingDtoResponse1.getStatus(), BookingStatus.APPROVED);

        BookingDtoResponse bookingDtoResponse2 = service.requestApprovReject(booking.getId(), user.getId(), false);

        assertEquals(bookingDtoResponse2.getStatus(), BookingStatus.REJECTED);

    }

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}