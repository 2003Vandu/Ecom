package com.eComm.eComm.Repository;

import com.eComm.eComm.Entity.OrderEnitity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderEntityRepository  extends JpaRepository<OrderEnitity, Long>
{

    Optional<OrderEnitity> findByOrderId(String orderId);

    List<OrderEnitity> findAllByOrderByCreatedAtDesc();

    @Query("SELECT SUM(o.grandTotal) FROM OrderEnitity o WHERE DATE(o.createdAt) = :date")
    Double sumSalesByDate(@Param("date")LocalDate date);

    @Query("SELECT COUNT(o) FROM OrderEnitity o WHERE DATE(o.createdAt)= :date")
    Long countByOrderDate(@Param("date")LocalDate date);

    @Query("Select o FROM OrderEnitity o ORDER BY o.createdAt DESC")
    List<OrderEnitity> findRecentOrders(Pageable pageable);
}
