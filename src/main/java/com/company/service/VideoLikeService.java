package com.company.service;

import com.company.entity.ProfileEntity;
import com.company.entity.ProfileWatchedVideoEntity;
import com.company.entity.VideoLikeEntity;
import com.company.enums.LikeStatus;
import com.company.exp.BadRequestException;
import com.company.exp.ItemNotFoundException;
import com.company.repository.VideoRepository;
import com.company.repository.VideoWatchedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class VideoLikeService {
    @Autowired
    private VideoWatchedRepository videoWatchedRepository;
    @Autowired
    private ProfileService profileService;

    public void videoLike(String videoId) {
        ProfileEntity profile = profileService.getProfile();
        likeDislike(videoId, profile.getId(), LikeStatus.LIKE);
    }

    public void videoDisLike(String videoId) {
        ProfileEntity profile = profileService.getProfile();
        likeDislike(videoId, profile.getId(), LikeStatus.DISLIKE);
    }

    private void likeDislike(String videoId, Integer pId, LikeStatus status) {
        Optional<ProfileWatchedVideoEntity> optional = videoWatchedRepository
                .findByVideoIdAndProfileId(videoId, pId);

        if (optional.isEmpty()){
            throw new BadRequestException("Videoni kurmay turip layk bosib bo'lmaydi.");
        }

        ProfileWatchedVideoEntity entity = optional.get();
        entity.setStatus(status);

        videoWatchedRepository.save(entity);

    }

    public void removeLike(String videoId) {
        ProfileEntity profile = profileService.getProfile();
        likeDislike(videoId,profile.getId(),LikeStatus.NULL);
    }

//    public Map<String, Integer> countLikeDislike(String videoId){
//
//    }
}
