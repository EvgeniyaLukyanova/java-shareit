package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplIntegrationTest {
    private final EntityManager em;
    private RequestService service;
    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        service = new RequestServiceImpl(repository,
                userRepository,
                itemRepository);
    }

    @Test
    void getAllRequests() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        em.persist(user);

        User requestor = new User();
        requestor.setEmail("requestor@user.com");
        requestor.setName("requestor");
        em.persist(requestor);

        Request request1 = new Request();
        request1.setDescription("Хотел бы воспользоваться щёткой для обуви");
        request1.setCreated(LocalDateTime.now().plusDays(1));
        request1.setRequestor(requestor);
        em.persist(request1);

        Request request2 = new Request();
        request2.setDescription("Ищу дрель");
        request2.setCreated(LocalDateTime.now());
        request2.setRequestor(requestor);
        em.persist(request2);

        Request request3 = new Request();
        request3.setDescription("Нужна отвертка");
        request3.setCreated(LocalDateTime.now().plusMinutes(5));
        request3.setRequestor(requestor);
        em.persist(request3);

        List<RequestDtoResponse> requestDtoResponses = service.getAllRequests(0, 2, user.getId());

        assertEquals(2, requestDtoResponses.size());
        assertEquals(requestDtoResponses.get(0).getDescription(), request1.getDescription());
        assertEquals(requestDtoResponses.get(1).getDescription(), request3.getDescription());
    }

    @Test
    void getAllRequestsFromSizeEmpty() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setName("user");
        em.persist(user);

        User requestor = new User();
        requestor.setEmail("requestor@user.com");
        requestor.setName("requestor");
        em.persist(requestor);

        Request request1 = new Request();
        request1.setDescription("Хотел бы воспользоваться щёткой для обуви");
        request1.setCreated(LocalDateTime.now().plusDays(1));
        request1.setRequestor(requestor);
        em.persist(request1);

        Request request2 = new Request();
        request2.setDescription("Ищу дрель");
        request2.setCreated(LocalDateTime.now());
        request2.setRequestor(requestor);
        em.persist(request2);

        Request request3 = new Request();
        request3.setDescription("Нужна отвертка");
        request3.setCreated(LocalDateTime.now().plusMinutes(5));
        request3.setRequestor(requestor);
        em.persist(request3);

        List<RequestDtoResponse> requestDtoResponses = service.getAllRequests(null, null, user.getId());

        assertEquals(3, requestDtoResponses.size());
        assertEquals(requestDtoResponses.get(0).getDescription(), request1.getDescription());
        assertEquals(requestDtoResponses.get(1).getDescription(), request3.getDescription());
        assertEquals(requestDtoResponses.get(2).getDescription(), request2.getDescription());
    }

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}