package kl.tw.ctf.service.impl;

import kl.tw.ctf.service.DataFileParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DataFileParserServiceImpl implements DataFileParserService {

    private final Logger log = LoggerFactory.getLogger(DataFileParserServiceImpl.class);

}
