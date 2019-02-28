package com.lusiwei.house.ds.service.fastdfs;

import com.lusiwei.house.ds.dao.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@Slf4j
public class FastDfsUtil {
    @Value("${house.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${house.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${house.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${house.fastdfs.charset}")
    String charset;
    @Autowired
    private UserMapper userMapper;
    private void initFdfsConfig(){
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        } catch (IOException | MyException e) {
            log.error("初始化fastDfs出错");
        }
    }

    public String upload(MultipartFile file) {
        try {
            //加载fdfs的配置
            initFdfsConfig();
            //创建tracker client
            TrackerClient trackerClient = new TrackerClient();
            //创建trackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storage client
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storeStorage);
            //上传文件
            byte[] bytes = file.getBytes();
            //文件原始名称
            String originalFilename = file.getOriginalFilename();
            //文件扩展名
            String extName = originalFilename.substring(originalFilename.lastIndexOf("."));
            //文件id
            String s = storageClient1.upload_file1(bytes, extName, null);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
