package ru.practicum.shareit.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class FromSizePageable extends PageRequest {
    public FromSizePageable(Integer from, Integer size, Sort sort) {
        super(from > 0 ? from / size : 0, size, sort);
    }

    public static FromSizePageable of(Integer from, Integer size, Sort sort) {
        return new FromSizePageable(from, size, sort);
    }
}
