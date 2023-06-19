package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id = ?1 " +
            "  and b.startDate <= ?3 " +
            "  and b.endDate >= ?2 " +
            "  and b.status in ('WAITING', 'APPROVED')")
    List<Booking> findByStartDateBeforeEndDateAfter(Long itemId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join b.booker as bk " +
            "join i.owner as u " +
            "where b.id = ?1 and (bk.id = ?2 or u.id = ?2)")
    Optional<Booking> findByIdAndBookerOwner(Long id, Long userId);

    @Query(value = "select b.* from bookings as b " +
                    "where b.booker_id = ?1 " +
                    "  and ((b.status = ?2) or " +
                    "       (COALESCE(?2,'ALL') = 'ALL') or" +
                    "       (?2 = 'CURRENT' and current_date+current_time between b.start_date and b.end_date) or " +
                    "       (?2 = 'PAST' and current_date+current_time > b.end_date) or " +
                    "       (?2 = 'FUTURE' and current_date+current_time < b.start_date))" +
                    "order by b.start_date desc", nativeQuery = true)
    List<Booking> findByBookerAndState(Long userId, String state);

    @Query(value = "select b.* from bookings as b " +
            "inner join items as i on (i.id = b.item_id) " +
            "where i.user_id = ?1 " +
            "  and ((b.status = ?2) or " +
            "       (COALESCE(?2,'ALL') = 'ALL') or" +
            "       (?2 = 'CURRENT' and current_date+current_time between b.start_date and b.end_date) or " +
            "       (?2 = 'PAST' and current_date+current_time > b.end_date) or " +
            "       (?2 = 'FUTURE' and current_date+current_time < b.start_date))" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> findByBookerAndStateOwner(Long userId, String state);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner o " +
            "where i.id = ?1 " +
            "  and o.id = ?2 " +
            "  and b.status <> 'REJECTED' " +
            "  and b.startDate = (select max(b.startDate) " +
            "                     from Booking as b " +
            "                     join b.item as i " +
            "                     where i.id = ?1 " +
            "                       and b.startDate <= current_date+current_time " +
            "                       and b.status <> 'REJECTED') ")
    Booking findByLastBooker(Long itemId, Long userId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner o " +
            "where i.id = ?1 " +
            "  and o.id = ?2 " +
            "  and b.status <> 'REJECTED' " +
            "  and b.startDate = (select min(b.startDate) " +
            "                     from Booking as b " +
            "                     join b.item as i " +
            "                     where i.id = ?1 " +
            "                       and b.startDate > current_date+current_time " +
            "                       and b.status <> 'REJECTED') ")
    Booking findByNextBooker(Long itemId, Long userId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner o " +
            "where o.id = ?1 " +
            "  and b.status <> 'REJECTED' " +
            "  and b.startDate = (select max(bb.startDate) " +
            "                     from Booking as bb " +
            "                     join b.item as ii " +
            "                     where ii.id = i.id " +
            "                       and bb.startDate <= current_date+current_time " +
            "                       and bb.status <> 'REJECTED') ")
    List<Booking> findByListLastBooker(Long userId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner o " +
            "where o.id = ?1 " +
            "  and b.status <> 'REJECTED' " +
            "  and b.startDate = (select min(bb.startDate) " +
            "                     from Booking as bb " +
            "                     join b.item as ii " +
            "                     where ii.id = i.id " +
            "                       and bb.startDate > current_date+current_time " +
            "                       and bb.status <> 'REJECTED') ")
    List<Booking> findByListNextBooker(Long userId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join b.booker as u " +
            "where i.id = ?1 " +
            "  and u.id = ?2 " +
            "  and b.endDate < current_date+current_time")
    List<Booking> findByListOfBookings(Long itemId, Long bookerId);
 }
