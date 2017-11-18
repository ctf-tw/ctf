package kl.tw.ctf.repository;

import kl.tw.ctf.domain.SuspectedReason;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data JPA repository for the SuspectedReason entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SuspectedReasonRepository extends JpaRepository<SuspectedReason, Long> {

    @Query(value = "SELECT sr FROM SuspectedReason sr WHERE sr.id LIKE %:id%")
    List<SuspectedReason> finByIdLike(@Param(value="id") String id);

}
