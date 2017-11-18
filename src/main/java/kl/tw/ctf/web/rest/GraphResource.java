package kl.tw.ctf.web.rest;

import kl.tw.ctf.dao.DataFile;
import kl.tw.ctf.service.DataFileParserService;
import kl.tw.ctf.service.RLauncherService;
import kl.tw.ctf.service.impl.RLauncherServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/management")
public class GraphResource {
    private final Logger log = LoggerFactory.getLogger(GraphResource.class);

    @Autowired
    RLauncherService rLauncherService;

    @PostMapping("/graphs")
    public ResponseEntity<?> getDataForGraph(@RequestParam("id") String id) {
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