package kl.tw.ctf.web.rest;

import kl.tw.ctf.service.RLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class BadUserResource {
    private final Logger log = LoggerFactory.getLogger(BadUserResource.class);

    @Autowired
    RLauncherService rLauncherService;

    @PostMapping("/badUser")
    public ResponseEntity<?> showBadUsers(@RequestParam("id") String id) {
        log.debug("Graph requested!");
        String result = null;

        if (id == null) {
            log.debug("No id provided!");
            return new ResponseEntity("please select a file!", HttpStatus.FORBIDDEN);
        }

        try {
            result = rLauncherService.getDataFor(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(result, new HttpHeaders(), HttpStatus.OK);

    }

}
