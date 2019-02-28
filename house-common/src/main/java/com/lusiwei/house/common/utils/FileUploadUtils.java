package com.lusiwei.house.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class FileUploadUtils {
    @Value("${file.path}")
    private String filePath;
    @Value("${file.type}")
    private String fileType;//头像

    /**
     * 上传并返回文件路径
     * @param multipartFile 文件
     * @param fileType 文件类别
     * @return 文件路径
     */
    public String upload(MultipartFile multipartFile, String fileType){
        //获取文件名
        String originalFilename = multipartFile.getOriginalFilename();
        //获取文件后缀
        String file_suffix = StrUtil.subAfter(originalFilename, ".", true);
        //通过uuid工具生成不重复的文件名
        String fileName = IdUtil.randomUUID();
        //拼接文件存放的物理路径
        String filePhysicalPath = filePath + "/" + fileType + "/" + fileName + "." + file_suffix;
        //利用工具类创建文件
        File file = FileUtil.touch(new File(filePhysicalPath));
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StrUtil.removePrefix(filePhysicalPath, filePath);
    }

    /**
     * 调用时不指定文件类别
     * @param multipartFile 文件
     * @return 相对路径
     */
    public String upload(MultipartFile multipartFile){
        return upload(multipartFile, fileType);
    }

    /**
     * 多文件上传
     * @param files 文件
     * @return
     */
    public List<String> upload(MultipartFile[] files) {
        List<String> list= CollUtil.newArrayList();
        for (MultipartFile file : files) {
            list.add(upload(file, fileType));
        }
        return list;
    }
}
