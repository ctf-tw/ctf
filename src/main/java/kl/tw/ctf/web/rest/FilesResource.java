package kl.tw.ctf.web.rest;

import kl.tw.ctf.dao.DataFile;
import kl.tw.ctf.service.CsvToDbConversionService;
import kl.tw.ctf.service.DataFileParserService;
import kl.tw.ctf.service.RLauncherService;
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

/**
 * DataUpload controller
 */
@RestController
@RequestMapping("/files")
public class FilesResource {
    private static final String UPLOADED_FOLDER = "/tmp";
    private final Logger log = LoggerFactory.getLogger(FilesResource.class);

    @Autowired
    DataFileParserService dataFileParserService;

    @Autowired
    CsvToDbConversionService dbConversionService;

    @Autowired
    RLauncherService rLauncherService;

/*    public FilesResource(@Autowired dataFileParserService) {
        this.dataFileParserService = dataFileParserService;
    }
    */


    /**
    * POST fileUpload
    */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadfile) {
        log.debug("Single file upload!");

        if (uploadfile.isEmpty()) {
            log.debug("Empty file!");
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }

        try {
//            saveUploadedFiles(Arrays.asList(uploadfile));
            DataFile dataFile = dataFileParserService.parse(uploadfile.getOriginalFilename().replace(".", "_"), uploadfile.getBytes());
            dbConversionService.createAndPopulateTable(dataFile);
            rLauncherService.notifyUpload();


        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity("Successfully uploaded - " +
            uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

    }

    private void saveUploadedFiles(List<MultipartFile> files) throws IOException {

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue; //next pls
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

        }

    }

    /**
    * GET fileDownload
    */
    @GetMapping("/files")
    public ResponseEntity<?> fileDownload() {
        return new ResponseEntity("Unsupported", HttpStatus.FORBIDDEN);
    }

}
