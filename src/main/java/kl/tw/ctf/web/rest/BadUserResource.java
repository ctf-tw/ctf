package kl.tw.ctf.web.rest;

import kl.tw.ctf.domain.SuspectedReason;
import kl.tw.ctf.repository.SuspectedReasonRepository;
import kl.tw.ctf.service.RLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/management")
public class BadUserResource {
    private final Logger log = LoggerFactory.getLogger(BadUserResource.class);

    @Autowired
    SuspectedReasonRepository suspectedReasonRepository;

    @GetMapping("/suspiciousList")
    public @ResponseBody List<SuspectedReason> showBadUsers() {
        log.debug("Graph requested!");
        List<SuspectedReason> result;

        try {
            result = suspectedReasonRepository.findAll();

        } catch (Exception e) {
            log.error("Can't load reasons" + e.getMessage());
            return null;
        }

        return result   ;

    }

}
