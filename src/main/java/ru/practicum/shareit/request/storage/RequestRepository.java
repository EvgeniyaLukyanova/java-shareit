package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import org.springframework.data.domain.Page;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequestorIdOrderByCreatedDesc(Long id);

    Page<Request> findAllByRequestorIdNot(long userId, Pageable page);

    List<Request> findAllByRequestorIdNotOrderByCreatedDesc(long userId);
}
