package vn.iostar.postservice.service;

import vn.iostar.postservice.dto.CommentDto;
import vn.iostar.postservice.entity.Comment;

public interface MapperService {
    CommentDto mapToCommentDto(Comment comment);
}
