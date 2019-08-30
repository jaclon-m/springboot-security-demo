package com.jaclon.bootsecurity.service.impl;

import com.jaclon.bootsecurity.dao.FileInfoDao;
import com.jaclon.bootsecurity.model.FileInfo;
import com.jaclon.bootsecurity.service.FileService;
import com.jaclon.bootsecurity.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author jaclon
 * @date 2019/8/27
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Value("${files.path}")
    private String filesPath;
    @Autowired
    private FileInfoDao fileInfoDao;

    @Override
    public FileInfo save(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if(!originalFilename.contains(".")){
            throw new IllegalArgumentException("缺少后缀名");
        }

        String md5 = FileUtil.fileMd5(file.getInputStream());
        FileInfo fileInfo = fileInfoDao.getById(md5);
        if(fileInfo != null){
            fileInfoDao.update(file);
            return fileInfo;
        }

        //获取文件后缀名
        originalFilename = originalFilename.substring(originalFilename.lastIndexOf("."));
        //自定义文件路径
        String pathname = FileUtil.getPath() + md5 + originalFilename;
        String fullPath = filesPath + pathname;
        FileUtil.saveFile(file, fullPath);

        long size = file.getSize();
        String contentType = file.getContentType();

        fileInfo = new FileInfo();
        fileInfo.setId(md5);
        fileInfo.setContentType(contentType);
        fileInfo.setPath(fullPath);
        fileInfo.setSize(size);
        fileInfo.setType(contentType.startsWith("image/")?1:0);
        fileInfo.setUrl(pathname);

        fileInfoDao.save(fileInfo);

        log.debug("上传文件{}" ,fullPath);

        return fileInfo;
    }

    @Override
    public void delete(String id) {
        FileInfo fileInfo = fileInfoDao.getById(id);
        if(fileInfo != null){
            String fullPath = fileInfo.getPath();
            FileUtil.deleteFile(fullPath);

            fileInfoDao.delete(id);
            log.debug("删除文件{}" ,fileInfo.getPath());
        }
    }
}
