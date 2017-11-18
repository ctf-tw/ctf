package kl.tw.ctf.service;

import kl.tw.ctf.dao.DataFile;

public interface DataFileParserService {
    public DataFile parse(String fileName, byte[] content);

}
