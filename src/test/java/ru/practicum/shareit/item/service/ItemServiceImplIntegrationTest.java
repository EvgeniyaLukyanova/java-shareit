package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.reference.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
//@SpringBootTest(
//        properties = "db.name=test",
//        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private final EntityManager em;
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private ItemService service;

    @BeforeEach
    void setUp() {
        service = new ItemServiceImpl(repository,
                userRepository,
                bookingRepository,
                commentRepository,
                requestRepository);
    }

    @Test
    void getItems() {
        Integer from = 1;
        Integer size = 1;

        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        em.persist(user);

        User requestor = new User();
        requestor.setEmail("requestor@user.com");
        requestor.setName("requestor");
        em.persist(requestor);

        Request request = new Request();
        request.setDescription("Хотел бы воспользоваться щёткой для обуви");
        request.setCreated(LocalDateTime.now());
        request.setRequestor(requestor);
        em.persist(request);

        Item item = new Item();
        item.setName("Щётка для обуви");
        item.setDescription("Щётка для обуви");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(request);
        em.persist(item);

        Item item1 = new Item();
        item1.setName("Аккумуляторная дрель");
        item1.setDescription("Аккумуляторная дрель");
        item1.setAvailable(true);
        item1.setOwner(user);
        em.persist(item1);

        User booker = new User();
        booker.setEmail("booker@user.com");
        booker.setName("booker");
        em.persist(booker);

        Booking lastBooking = new Booking();
        lastBooking.setStartDate(LocalDateTime.of(2023,6,16,10,0,0));
        lastBooking.setEndDate(LocalDateTime.of(2023,6,17,10,0,0));
        lastBooking.setBooker(booker);
        lastBooking.setItem(item);
        lastBooking.setStatus(BookingStatus.APPROVED);
        em.persist(lastBooking);

        Booking nextBooking = new Booking();
        nextBooking.setStartDate(LocalDateTime.now().plusDays(1));
        nextBooking.setEndDate(LocalDateTime.now().plusDays(2));
        nextBooking.setBooker(booker);
        nextBooking.setItem(item);
        nextBooking.setStatus(BookingStatus.APPROVED);
        em.persist(nextBooking);

        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.of(2023,6,21,11,0,0));
        comment.setAuthor(booker);
        comment.setText("Add comment");
        comment.setItem(item);
        em.persist(comment);

        em.flush();

        List<ItemDtoResponse> itemDtoResponseList = service.getItems(user.getId(), from, size);

        assertEquals(1, itemDtoResponseList.size());
        assertNotNull(itemDtoResponseList.get(0).getId());
        assertEquals(itemDtoResponseList.get(0).getName(), item.getName());
        assertEquals(itemDtoResponseList.get(0).getDescription(), item.getDescription());
        assertEquals(itemDtoResponseList.get(0).getAvailable(), item.getAvailable());
        assertNotNull(itemDtoResponseList.get(0).getOwner());
        assertEquals(itemDtoResponseList.get(0).getOwner().getName(), user.getName());
        assertNotNull(itemDtoResponseList.get(0).getLastBooking());
        assertEquals(itemDtoResponseList.get(0).getLastBooking().getStart(), lastBooking.getStartDate());
        assertEquals(itemDtoResponseList.get(0).getLastBooking().getEnd(), lastBooking.getEndDate());
        assertEquals(itemDtoResponseList.get(0).getLastBooking().getBookerId(), lastBooking.getBooker().getId());
        assertNotNull(itemDtoResponseList.get(0).getNextBooking());
        assertEquals(itemDtoResponseList.get(0).getNextBooking().getStart(), nextBooking.getStartDate());
        assertEquals(itemDtoResponseList.get(0).getNextBooking().getEnd(), nextBooking.getEndDate());
        assertEquals(itemDtoResponseList.get(0).getNextBooking().getBookerId(), nextBooking.getBooker().getId());

        assertNotNull(itemDtoResponseList.get(0).getComments());
        assertEquals(itemDtoResponseList.get(0).getComments().get(0).getText(), comment.getText());
    }

    @AfterEach
    void deleteAll() {
        bookingRepository.deleteAll();
        repository.deleteAll();
        requestRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();
    }
}