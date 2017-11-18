package kl.tw.ctf.web.rest;

import com.codahale.metrics.annotation.Timed;
import kl.tw.ctf.domain.SuspectedReason;

import kl.tw.ctf.repository.SuspectedReasonRepository;
import kl.tw.ctf.web.rest.errors.BadRequestAlertException;
import kl.tw.ctf.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing SuspectedReason.
 */
@RestController
@RequestMapping("/api")
public class SuspectedReasonResource {

    private final Logger log = LoggerFactory.getLogger(SuspectedReasonResource.class);

    private static final String ENTITY_NAME = "suspectedReason";

    private final SuspectedReasonRepository suspectedReasonRepository;

    public SuspectedReasonResource(SuspectedReasonRepository suspectedReasonRepository) {
        this.suspectedReasonRepository = suspectedReasonRepository;
    }

    /**
     * POST  /suspected-reasons : Create a new suspectedReason.
     *
     * @param suspectedReason the suspectedReason to create
     * @return the ResponseEntity with status 201 (Created) and with body the new suspectedReason, or with status 400 (Bad Request) if the suspectedReason has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/suspected-reasons")
    @Timed
    public ResponseEntity<SuspectedReason> createSuspectedReason(@RequestBody SuspectedReason suspectedReason) throws URISyntaxException {
        log.debug("REST request to save SuspectedReason : {}", suspectedReason);
        if (suspectedReason.getId() != null) {
            throw new BadRequestAlertException("A new suspectedReason cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SuspectedReason result = suspectedReasonRepository.save(suspectedReason);
        return ResponseEntity.created(new URI("/api/suspected-reasons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /suspected-reasons : Updates an existing suspectedReason.
     *
     * @param suspectedReason the suspectedReason to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated suspectedReason,
     * or with status 400 (Bad Request) if the suspectedReason is not valid,
     * or with status 500 (Internal Server Error) if the suspectedReason couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/suspected-reasons")
    @Timed
    public ResponseEntity<SuspectedReason> updateSuspectedReason(@RequestBody SuspectedReason suspectedReason) throws URISyntaxException {
        log.debug("REST request to update SuspectedReason : {}", suspectedReason);
        if (suspectedReason.getId() == null) {
            return createSuspectedReason(suspectedReason);
        }
        SuspectedReason result = suspectedReasonRepository.save(suspectedReason);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, suspectedReason.getId().toString()))
            .body(result);
    }

    /**
     * GET  /suspected-reasons : get all the suspectedReasons.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of suspectedReasons in body
     */
    @GetMapping("/suspected-reasons")
    @Timed
    public List<SuspectedReason> getAllSuspectedReasons() {
        log.debug("REST request to get all SuspectedReasons");
        return suspectedReasonRepository.findAll();
        }

    /**
     * GET  /suspected-reasons/:id : get the "id" suspectedReason.
     *
     * @param id the id of the suspectedReason to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the suspectedReason, or with status 404 (Not Found)
     */
    @GetMapping("/suspected-reasons/{id}")
    @Timed
    public ResponseEntity<SuspectedReason> getSuspectedReason(@PathVariable Long id) {
        log.debug("REST request to get SuspectedReason : {}", id);
        SuspectedReason suspectedReason = suspectedReasonRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(suspectedReason));
    }

    /**
     * DELETE  /suspected-reasons/:id : delete the "id" suspectedReason.
     *
     * @param id the id of the suspectedReason to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/suspected-reasons/{id}")
    @Timed
    public ResponseEntity<Void> deleteSuspectedReason(@PathVariable Long id) {
        log.debug("REST request to delete SuspectedReason : {}", id);
        suspectedReasonRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
