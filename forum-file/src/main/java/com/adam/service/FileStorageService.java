package com.adam.service;

import io.minio.errors.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author LiuLCH
 */
public interface FileStorageService {


    /**
     *  上传图片文件
     * @param prefix  文件前缀
     * @param filename  文件名
     * @param inputStream 文件流
     * @return  文件全路径
     */
    String uploadImgFile(String prefix, String filename,InputStream inputStream);

    /**
     *  上传html文件
     * @param prefix  文件前缀
     * @param filename   文件名
     * @param inputStream  文件流
     * @return  文件全路径
     */
    String uploadHtmlFile(String prefix, String filename,InputStream inputStream);


    /**
     *  上传视频文件
     * @param prefix  文件前缀
     * @param filename  文件名
     * @param inputStream 文件流
     * @return  文件全路径
     */
    String uploadVideoFile(String prefix, String filename, InputStream inputStream);

    /**
     * 上传视频文件，用于分片上传。
     * @param fileName 文件名
     * @param stream 文件流
     * @param contentType 视频类型
     * @return 是否上传成功
     */
    Boolean uploadVideoPartFile(String fileName, InputStream stream, String contentType);

    /**
     * 删除文件
     *
     * @param pathUrl 文件全路径
     */
    void delete(String pathUrl);

    /**
     * 下载文件
     * @param pathUrl  文件全路径
     * @return 返回文件流
     *
     */
    byte[]  downLoadFile(String pathUrl);

    /**
     * 合并分片文件为完整的视频文件
     *
     * @param presumableIdentifier 分片上传的唯一标识符，用于识别属于同一文件的所有分片
     * @param videoName           合并后的完整视频文件名
     * @return 合并后存放的文件路径
     */
    String composePart(String presumableIdentifier, String videoName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

}