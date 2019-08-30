package com.jaclon.bootsecurity.service;

import com.jaclon.bootsecurity.model.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author jaclon
 * @date 2019/8/27
 */
public interface FileService {

    FileInfo save(MultipartFile file) throws IOException;

    void delete(String id);

}
