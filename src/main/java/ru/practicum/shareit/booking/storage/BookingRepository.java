package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id = :itemId " +
            "  and b.startDate <= :endDate " +
            "  and b.endDate >= :startDate " +
            "  and b.status in ('WAITING', 'APPROVED')")
    List<Booking> findByStartDateBeforeEndDateAfter(@Param("itemId") Long itemId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join b.booker as bk " +
            "join i.owner as u " +
            "where b.id = :id and (bk.id = :userId or u.id = :userId)")
    Optional<Booking> findByIdAndBookerOwner(@Param("id") Long id, @Param("userId") Long userId);

    @Query(value = "select b.* from bookings as b " +
                    "where b.booker_id = :userId " +
                    "  and ((b.status = :state) or " +
                    "       (COALESCE(:state,'ALL') = 'ALL') or" +
                    "       (:state = 'CURRENT' and current_date+current_time between b.start_date and b.end_date) or " +
                    "       (:state = 'PAST' and current_date+current_time > b.end_date) or " +
                    "       (:state = 'FUTURE' and current_date+current_time < b.start_date))" +
                    "order by b.start_date desc " +
                    "limit :limit offset :limit * (:pageNo - 1) ", nativeQuery = true)
    List<Booking> findByBookerAndState(@Param("userId") Long userId,
                                       @Param("state") String state,
                                       @Param("limit") Integer limit,
                                       @Param("pageNo") Long pageNo);

    @Query(value = "select b.* from bookings as b " +
            "where b.booker_id = :userId " +
            "  and ((b.status = :state) or " +
            "       (COALESCE(:state,'ALL') = 'ALL') or" +
            "       (:state = 'CURRENT' and current_date+current_time between b.start_date and b.end_date) or " +
            "       (:state = 'PAST' and current_date+current_time > b.end_date) or " +
            "       (:state = 'FUTURE' and current_date+current_time < b.start_date))" +
            "order by b.start_date desc ", nativeQuery = true)
    List<Booking> findByBookerAndState(@Param("userId") Long userId, @Param("state") String state);

    @Query(value = "select b.* from bookings as b " +
            "inner join items as i on (i.id = b.item_id) " +
            "where i.user_id = :userId " +
            "  and ((b.status = :state) or " +
            "       (COALESCE(:state,'ALL') = 'ALL') or" +
            "       (:state = 'CURRENT' and current_date+current_time between b.start_date and b.end_date) or " +
            "       (:state = 'PAST' and current_date+current_time > b.end_date) or " +
            "       (:state = 'FUTURE' and current_date+current_time < b.start_date))" +
            "order by b.start_date desc " +
            "limit :limit offset :limit * (:pageNo - 1) ", nativeQuery = true)
    List<Booking> findByBookerAndStateOwner(@Param("userId") Long userId,
                                            @Param("state") String state,
                                            @Param("limit") Integer limit,
                                            @Param("pageNo") Long pageNo);

    @Query(value = "select b.* from bookings as b " +
            "inner join items as i on (i.id = b.item_id) " +
            "where i.user_id = :userId " +
            "  and ((b.status = :state) or " +
            "       (COALESCE(:state,'ALL') = 'ALL') or" +
            "       (:state = 'CURRENT' and current_date+current_time between b.start_date and b.end_date) or " +
            "       (:state = 'PAST' and current_date+current_time > b.end_date) or " +
            "       (:state = 'FUTURE' and current_date+current_time < b.start_date))" +
            "order by b.start_date desc ", nativeQuery = true)
    List<Booking> findByBookerAndStateOwner(@Param("userId") Long userId, @Param("state") String state);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner o " +
            "where i.id = :itemId " +
            "  and o.id = :userId " +
            "  and b.status <> 'REJECTED' " +
            "  and b.startDate = (select max(b.startDate) " +
            "                     from Booking as b " +
            "                     join b.item as i " +
            "                     where i.id = :itemId " +
            "                       and b.startDate <= current_date+current_time " +
            "                       and b.status <> 'REJECTED') "
    )
    Booking findByLastBooker(@Param("itemId") Long itemId, @Param("userId") Long userId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner o " +
            "where i.id = :itemId " +
            "  and o.id = :userId " +
            "  and b.status <> 'REJECTED' " +
            "  and b.startDate = (select min(b.startDate) " +
            "                     from Booking as b " +
            "                     join b.item as i " +
            "                     where i.id = :itemId " +
            "                       and b.startDate > current_date+current_time " +
            "                       and b.status <> 'REJECTED') ")
    Booking findByNextBooker(@Param("itemId") Long itemId, @Param("userId") Long userId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner o " +
            "where o.id = :userId " +
            "  and b.status <> 'REJECTED' " +
            "  and b.startDate = (select max(bb.startDate) " +
            "                     from Booking as bb " +
            "                     join b.item as ii " +
            "                     where ii.id = i.id " +
            "                       and bb.startDate <= current_date+current_time " +
            "                       and bb.status <> 'REJECTED') ")
    List<Booking> findByListLastBooker(@Param("userId") Long userId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner o " +
            "where o.id = :userId " +
            "  and b.status <> 'REJECTED' " +
            "  and b.startDate = (select min(bb.startDate) " +
            "                     from Booking as bb " +
            "                     join b.item as ii " +
            "                     where ii.id = i.id " +
            "                       and bb.startDate > current_date+current_time " +
            "                       and bb.status <> 'REJECTED') ")
    List<Booking> findByListNextBooker(@Param("userId") Long userId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join b.booker as u " +
            "where i.id = :itemId " +
            "  and u.id = :bookerId " +
            "  and b.endDate < current_date+current_time")
    List<Booking> findByListOfBookings(@Param("itemId") Long itemId, @Param("bookerId") Long bookerId);
 }
