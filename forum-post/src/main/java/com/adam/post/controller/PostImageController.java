package com.adam.post.controller;

import com.adam.post.service.PostImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 帖子图片相关接口
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/19 13:06
 */
@RestController
@RequestMapping("/post/image")
@Tag(name = "帖子图片相关接口")
@Slf4j
public class PostImageController {

    @Resource
    private PostImageService postImageService;
}
