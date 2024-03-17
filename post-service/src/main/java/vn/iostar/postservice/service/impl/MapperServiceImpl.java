package vn.iostar.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.iostar.postservice.constant.AppConstant;
import vn.iostar.postservice.dto.CommentDto;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.service.MapperService;
import vn.iostar.postservice.service.client.UserClientService;
import vn.iostar.postservice.util.DateUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapperServiceImpl implements MapperService {

    private final UserClientService userClientService;
    @Override
    public CommentDto mapToCommentDto(Comment comment) {
        UserProfileResponse userProfileResponse = userClientService.getUser(comment.getUserId());

        return CommentDto.builder()
                .id(comment.getId())
                .authorId(userProfileResponse.getUserId())
                .authorName(userProfileResponse.getUserName())
                .authorAvatar(userProfileResponse.getAvatar())
                .subCommentDtos(comment.getSubComments().isEmpty() ?
                        null : comment.getSubComments().stream().map(this::mapToCommentDto).toList())
                .createdAt(comment.getCreateTime() == null ?
                null : DateUtil.date2String(comment.getCreateTime(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(comment.getUpdatedAt() == null ?
                        null : DateUtil.date2String(comment.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .content(comment.getContent())
                .build();
    }
}
