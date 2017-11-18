package kl.tw.ctf.web.rest;

import java.util.List;
import kl.tw.ctf.service.DataRetrieveService;
import kl.tw.ctf.service.RLauncherService;
import kl.tw.ctf.service.impl.DataFileParserServiceImpl;
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
public class GraphResource {
    private final Logger log = LoggerFactory.getLogger(GraphResource.class);

    @Autowired
    RLauncherService rLauncherService;

    @Autowired
    DataRetrieveService dataRetrieveService;

    @PostMapping("/graphs")
    public ResponseEntity<?> getDataForGraph(@RequestParam("id") String id) {
        log.debug("Graph requested!");
        if (id == null) {
            log.debug("No id provided!");
            return new ResponseEntity("please select a file!", HttpStatus.FORBIDDEN);
        }

        String jsonResponse = null;
        try {
            jsonResponse = dataRetrieveService.getJsonGraphFor(id, DataFileParserServiceImpl.currentlyOpenFile);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(jsonResponse, new HttpHeaders(), HttpStatus.OK);

    }

}
