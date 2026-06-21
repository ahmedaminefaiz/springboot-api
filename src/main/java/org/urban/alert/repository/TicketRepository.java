package org.urban.alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.urban.alert.entity.Ticket;
import org.urban.alert.entity.enums.TicketStatusEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByReference(String reference);

    List<Ticket> findByStatus(TicketStatusEnum status);

    Long countByStatus(TicketStatusEnum status);
}