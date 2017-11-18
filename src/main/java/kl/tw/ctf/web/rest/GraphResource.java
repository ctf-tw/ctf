package kl.tw.ctf.web.rest;

import kl.tw.ctf.service.DataRetrieveService;
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
    DataRetrieveService dataRetrieveService;

    @RequestMapping(value = "/graphs", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<?> getDataForGraph(@RequestParam("id") String id) {
        log.debug("Graph requested!");
        if (id == null) {
            log.debug("No id provided!");
            return new ResponseEntity("please select a file!", HttpStatus.FORBIDDEN);
        }

        String jsonResponse = null;
        try {
            jsonResponse = dataRetrieveService.getJsonGraphFor(id, DataFileParserServiceImpl.currentlyOpenFile);
            log.info("Graph build response: " + jsonResponse);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(jsonResponse, new HttpHeaders(), HttpStatus.OK);

    }

}
