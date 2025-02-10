package com.adam.post.service.impl;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.CommonConstant;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.core.utils.SqlUtils;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.post.mapper.PostFavourMapper;
import com.adam.post.mapper.PostMapper;
import com.adam.post.mapper.PostTagMapper;
import com.adam.post.mapper.PostThumbMapper;
import com.adam.post.model.entity.*;
import com.adam.post.model.request.post.PostAddRequest;
import com.adam.post.model.request.post.PostEditRequest;
import com.adam.post.model.request.post.PostQueryRequest;
import com.adam.post.model.vo.PostTagVO;
import com.adam.post.model.vo.PostVO;
import com.adam.post.service.PostImageService;
import com.adam.post.service.PostService;
import com.adam.post.service.TagService;
import com.adam.service.user.bo.UserBasicInfoBO;
import com.adam.service.user.service.UserBasicRpcService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author chenjiahan
 * @description 针对表【post(帖子表)】的数据库操作Service实现
 * @createDate 2025-01-19 13:03:04
 */
@Service
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
        implements PostService {

    private static final Gson GSON = new Gson();

    @Resource
    private PostImageService postImageService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private TagService tagService;

    @Resource
    private PostTagMapper postTagMapper;

    @Resource
    private PostFavourMapper postFavourMapper;

    @Resource
    private PostThumbMapper postThumbMapper;

    @DubboReference
    private UserBasicRpcService userBasicRpcService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addPost(PostAddRequest postAddRequest) {
        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();

        // 保存帖子信息
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
        post.setUserId(currentUser.getId());
        baseMapper.insert(post);

        // 保存图片信息
        List<String> imageList = postAddRequest.getImageList();
        if (!CollectionUtils.isEmpty(imageList)) {
            postImageService.addPostImage(post.getId(), imageList, postAddRequest.getCoverIndex());
        }

        // 保存标签信息
        List<Long> tagIdList = postAddRequest.getTagIdList();
        if (!CollectionUtils.isEmpty(tagIdList)) {
            tagService.associatePostTagList(post.getId(), tagIdList, currentUser.getId());
        }

        log.info("发布帖子成功 「{}」，创建用户信息：「{}」", GSON.toJson(post), GSON.toJson(currentUser));
        return post.getId();

    }

    @Override
    public boolean editPost(PostEditRequest postEditRequest) {
        // 判断是否是创建者
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        Post originalPost = baseMapper.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, postEditRequest.getId())
                .select(Post::getUserId));
        ThrowUtils.throwIf(originalPost == null, ErrorCodeEnum.NOT_FOUND_ERROR, "原帖已不存在！");

        if (!Objects.equals(originalPost.getUserId(), currentUser.getId())) {
            throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR, "仅帖子创作者可编辑帖子内容");
        }

        Post post = new Post();
        BeanUtils.copyProperties(postEditRequest, post);
        log.info("更新帖子成功 「{}」，创建用户信息：「{}」", GSON.toJson(postEditRequest), GSON.toJson(currentUser));
        return true;
    }

    @Override
    public boolean deletePost(Long postId) {
        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        Long userId = currentUser.getId();
        Post originalPost = baseMapper.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, postId)
                .select(Post::getUserId));
        ThrowUtils.throwIf(originalPost == null, ErrorCodeEnum.NOT_FOUND_ERROR, "原帖已不存在！");

        if (!Objects.equals(originalPost.getUserId(), userId)) {
            throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR, "仅帖子创作者可删除帖子内容");
        }

        // 事务删除所有信息
        transactionTemplate.execute(status -> {
            // 删除原帖信息
            baseMapper.deleteById(postId);

            // 删除帖子图片信息
            postImageService.remove(Wrappers.<PostImage>lambdaQuery()
                    .eq(PostImage::getPostId, postId));

            // 删除帖子标签信息
            postTagMapper.delete(Wrappers.<PostTag>lambdaQuery()
                    .eq(PostTag::getPostId, postId));
            return Boolean.TRUE;

            // todo 删除点赞信息
        });

        log.info("删除 帖子「{}」，创建者 「{}」", postId, GSON.toJson(currentUser));

        return true;
    }

    @Override
    public PostVO getPostVO(Long postId) {
        // todo 缓存改造
        Post post = baseMapper.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, postId)
                .last(DatabaseConstant.LIMIT_ONE));
        ThrowUtils.throwIf(post == null, ErrorCodeEnum.NOT_FOUND_ERROR, "帖子信息不存在");

        // 获取当前登录用户 id
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        Long userId = currentUser.getId();

        PostVO postVO = new PostVO();
        BeanUtils.copyProperties(post, postVO);

        // 获取图片
        List<PostImage> postImageList = postImageService.list(Wrappers.<PostImage>lambdaQuery()
                .eq(PostImage::getPostId, postId)
                .select(PostImage::getImage, PostImage::getIsCover));
        List<String> imageList = new ArrayList<>();
        for (int index = 0; index < postImageList.size(); index++) {
            PostImage postImage = postImageList.get(index);
            imageList.add(postImage.getImage());
            if (postImage.getIsCover() == 1) {
                postVO.setCoverIndex(index + 1);
            }
        }
        postVO.setImageList(imageList);

        // 获取标签
        List<PostTagVO> postTagVOList = tagService.getTagVOListByPostId(postId);
        postVO.setTagList(postTagVOList);

        // 获取帖子创建者信息
        UserBasicInfoBO userBasicInfoBO = userBasicRpcService.getUserBasicInfoByUserId(post.getUserId());
        postVO.setCreateUser(userBasicInfoBO);

        // 判断是否点赞
        boolean hasThumb = postThumbMapper.exists(Wrappers.<PostThumb>lambdaQuery()
                .eq(PostThumb::getPostId, postId)
                .eq(PostThumb::getUserId, userId));
        postVO.setHasFavour(hasThumb);

        // 判断是否收藏
        boolean hasFavour = postFavourMapper.exists(Wrappers.<PostFavour>lambdaQuery()
                .eq(PostFavour::getPostId, postId)
                .eq(PostFavour::getUserId, userId));
        postVO.setHasFavour(hasFavour);

        return postVO;
    }

    @Override
    public Page<PostVO> pagePostVO(PostQueryRequest postQueryRequest) {
        long size = postQueryRequest.getPageSize();
        long current = postQueryRequest.getCurrent();
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        Page<Post> postPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(postQueryRequest));

        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<Post> postList = postPage.getRecords();
        if (CollectionUtils.isEmpty(postList)) {
            return postVOPage;
        }
        Set<Long> postIdSet = postList.stream().map(Post::getId).collect(Collectors.toSet());

        // 获取所有图片信息
        List<PostImage> postImageList = postImageService.list(Wrappers.<PostImage>lambdaQuery()
                .in(PostImage::getPostId, postIdSet)
                .select(PostImage::getImage, PostImage::getPostId, PostImage::getIsCover));
        // postId -> imageList, coverIndex
        Map<Long, Pair<List<String>, Integer>> postImageMap = postImageList.stream()
                .collect(Collectors.groupingBy(
                        PostImage::getPostId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                postImages -> {
                                    List<String> imageList = postImages.stream().map(PostImage::getImage).toList();
                                    int coverIndex = IntStream.range(0, postImages.size())
                                            .filter(i -> postImages.get(i).getIsCover() == 1) // 找到 isCover 为 1 的图片
                                            .findFirst() // 获取第一个封面图片的索引
                                            .orElse(0) + 1;

                                    return Pair.of(imageList, coverIndex);
                                }
                        )
                ));

        // 获取所有标签列表
        List<PostTagVO> tagVOList = tagService.getTagVOListByPostIdSet(postIdSet);
        // postId -> tagList
        Map<Long, List<PostTagVO>> postTagListMap = tagVOList.stream().collect(Collectors.groupingBy(PostTagVO::getPostId));

        // 获取用户点赞、收藏状态
        Map<Long, Boolean> postIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> postIdHasFavourMap = new HashMap<>();
        // 获取收藏
        List<PostFavour> postFavourList = postFavourMapper.selectList(
                Wrappers.<PostFavour>lambdaQuery()
                        .eq(PostFavour::getUserId, currentUser.getId())
                        .in(PostFavour::getPostId, postIdSet)
        );
        postFavourList.forEach(postFavour -> postIdHasFavourMap.put(postFavour.getPostId(), true));

        // 获取点赞
        List<PostThumb> postPostThumbList = postThumbMapper.selectList(
                Wrappers.<PostThumb>lambdaQuery()
                        .in(PostThumb::getPostId, postIdSet)
                        .eq(PostThumb::getUserId, currentUser.getId())
        );
        postPostThumbList.forEach(postPostThumb -> postIdHasThumbMap.put(postPostThumb.getPostId(), true));

        // 获取创作用户
        Set<Long> userIdList = postList.stream().map(Post::getUserId).collect(Collectors.toSet());
        List<UserBasicInfoBO> createUserList = userBasicRpcService.getUserBasicInfoListByUserIdList(userIdList);
        // userId -> userInfo
        Map<Long, UserBasicInfoBO> createUserMap = createUserList.stream()
                .collect(Collectors.toMap(UserBasicInfoBO::getId, userInfo -> userInfo));

        // 封装 postVO 列表
        List<PostVO> postVOList = postList.stream().map(post -> {
            PostVO postVO = new PostVO();
            BeanUtils.copyProperties(post, postVO);
            // 设置图片列表
            Pair<List<String>, Integer> postPair = postImageMap.get(post.getId());
            if (ObjectUtils.isNotEmpty(postPair)) {
                postVO.setImageList(postPair.getFirst());
                postVO.setCoverIndex(postPair.getSecond());
            } else {
                postVO.setImageList(Collections.emptyList());
                postVO.setCoverIndex(1);
            }
            // 设置帖子标签
            postVO.setTagList(postTagListMap.getOrDefault(post.getId(), Collections.emptyList()));
            // 设置点赞
            postVO.setHasThumb(postIdHasThumbMap.getOrDefault(post.getId(), false));
            // 设置收藏
            postVO.setHasFavour(postIdHasFavourMap.getOrDefault(post.getId(), false));
            // 设置创作者
            postVO.setCreateUser(createUserMap.get(post.getUserId()));
            return postVO;
        }).toList();

        postVOPage.setRecords(postVOList);
        return postVOPage;
    }

    public QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (postQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = postQueryRequest.getSearchText();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        Long id = postQueryRequest.getId();
        String title = postQueryRequest.getTitle();
        List<Long> tagIdList = postQueryRequest.getTagIdList();
        Long userId = postQueryRequest.getUserId();
        List<Long> ids = postQueryRequest.getIds();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.in(!CollectionUtils.isEmpty(ids), "id", ids);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        // 添加对 tagIdList 的搜索条件
        if (!CollectionUtils.isEmpty(tagIdList)) {
            queryWrapper.inSql("id", "SELECT post_id FROM post_tag WHERE tag_id IN (" + StringUtils.join(tagIdList, ",") + ")");
        }
        queryWrapper.eq("is_delete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}