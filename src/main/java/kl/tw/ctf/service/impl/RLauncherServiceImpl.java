package kl.tw.ctf.service.impl;

import com.opencsv.CSVReader;
import kl.tw.ctf.dao.DataFile;
import kl.tw.ctf.service.DataFileParserService;
import kl.tw.ctf.service.RLauncherService;
import kl.tw.ctf.service.util.StreamGobbler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;

@Service
@Transactional
public class RLauncherServiceImpl implements RLauncherService {

    private final Logger log = LoggerFactory.getLogger(RLauncherServiceImpl.class);
    private static final String R_SCRIPTS_DIR_PATH = "/Users/vadym.vasyliev/hack";
    private static final String GET_DATA_COMMAND = "sh ex.sh";

    @Override
    public String getDataFor(String id) {
        ProcessBuilder builder = new ProcessBuilder();
        String fullCommand = GET_DATA_COMMAND + " " + id;
        builder.command(fullCommand.split(" "));

        builder.directory(new File(R_SCRIPTS_DIR_PATH));
        Process process = null;
        StringBuilder sb = new StringBuilder();
        try {
            process = builder.start();
  /*          StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);*/


            BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                sb.append(line + "\n");
            }

            int exitCode = process.waitFor();
        } catch (Exception e) {
            log.error("Can't run R script " + e.getMessage());
            return null;
        }

        return sb.toString();
    }
}
