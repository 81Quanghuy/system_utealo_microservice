package vn.iostar.postservice.mapper;

import vn.iostar.model.PostElastic;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.model.PostDocument;

public class PostMapper {

    //Mapping giưữa Post và PostDocument
    public static PostDocument toPostDocument(Post post) {
        PostDocument postDocument = new PostDocument();
        postDocument.setId(post.getId());
        postDocument.setPhotos(post.getPhotos());
        postDocument.setFiles(post.getFiles());
        postDocument.setVideo(post.getVideo());
        postDocument.setLocation(post.getLocation());
        postDocument.setContent(post.getContent());
        postDocument.setPrivacyLevel(post.getPrivacyLevel().toString());
        postDocument.setUserId(post.getUserId());
        postDocument.setGroupId(post.getGroupId());
        return postDocument;
    }
    public static PostElastic toPostElastic(PostDocument user) {
        PostElastic postElastic = new PostElastic();
        postElastic.setId(user.getId());
        postElastic.setPhotos(user.getPhotos());
        postElastic.setFiles(user.getFiles());
        postElastic.setVideo(user.getVideo());
        postElastic.setLocation(user.getLocation());
        postElastic.setContent(user.getContent());
        postElastic.setPrivacyLevel(user.getPrivacyLevel());
        postElastic.setUserId(user.getUserId());
        postElastic.setGroupId(user.getGroupId());
        return postElastic;
    }
}
