package kl.tw.ctf.service.impl;

import com.opencsv.CSVReader;
import javax.xml.crypto.Data;
import kl.tw.ctf.dao.DataFile;
import kl.tw.ctf.service.DataFileParserService;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class DataFileParserServiceImpl implements DataFileParserService {

    private final Logger log = LoggerFactory.getLogger(DataFileParserServiceImpl.class);

    public static DataFile currentlyOpenFile;

    @Override
    public DataFile parse(String fileName, byte[] content) {
        Reader rdr = null;
        List<String> columnNames = null;
        List<List<String>> columnValues = new ArrayList<>();

        try {
            rdr = new InputStreamReader(new ByteArrayInputStream(content), "UTF-8");
            List<String[]> lines = new CSVReader(rdr, ',').readAll();

            if (lines.size() < 1) {
                return null;
            }

            boolean headerRead = false;
            for (String[] line: lines) {
                if (!headerRead) {
                    columnNames = Arrays.asList(line);
                    headerRead = true;
                    continue;
                }

                columnValues.add(Arrays.asList(line));
            }


        } catch (Exception e) {
            log.error("Exception during file parse: " + e.getMessage());
            return null;
        }

        currentlyOpenFile = new DataFile(fileName, columnNames, columnValues);
        return currentlyOpenFile;
/*        return DataFile.builder()
            .name(fileName)
            .columnNames(columnNames)
            .columnValues(columnValues)
            .build();*/

    }

}
