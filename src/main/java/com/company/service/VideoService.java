package com.company.service;

import com.company.dto.ResponseInfoDTO;
import com.company.dto.playlist.VideoShortInfoDTO;
import com.company.dto.tag.TagDTO;
import com.company.dto.video.VideoCreatedDTO;
import com.company.dto.video.VideoFullInfoDTO;
import com.company.dto.video.VideoUpdateDTO;
import com.company.entity.*;
import com.company.enums.LikeStatus;
import com.company.enums.ProfileRole;
import com.company.exp.ItemNotFoundException;
import com.company.exp.NotPermissionException;
import com.company.query.VideoShortInfo;
import com.company.query.VideoViewLikeDislikeCountAndStatusByProfile;
import com.company.repository.VideoRepository;
import com.company.repository.VideoTagRepository;
import com.company.repository.VideoWatchedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private VideoTagRepository videoTagRepository;
    @Autowired
    private PlaylistVideoService playlistVideoService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AttachService attachService;
    @Autowired
    private VideoWatchedRepository videoWatchedRepository;

    public ResponseInfoDTO createdVideo(VideoCreatedDTO dto) {

        VideoEntity entity = new VideoEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAttachId(dto.getAttach());
        entity.setCategoryId(dto.getCategoryId());

        if (dto.getChannelId() != null) {
            entity.setChannelId(dto.getChannelId());
        }

        if (dto.getReview() != null) {
            entity.setReviewId(dto.getReview());
        }

        entity.setChannelId(dto.getChannelId());

        videoRepository.save(entity);

        if (dto.getPlaylist() != null) {
            playlistVideoService.created(dto.getPlaylist(), entity.getUuid());
        }
        dto.getTags().forEach(tag -> {
            TagEntity tagEntity = tagService.createdIfNotExist(tag);
            VideoTagEntity videoTag = new VideoTagEntity();

            videoTag.setVideoId(entity.getUuid());
            videoTag.setTagId(tagEntity.getId());

            videoTagRepository.save(videoTag);
        });

        return new ResponseInfoDTO(1, "success");
    }

    public ResponseInfoDTO update(VideoUpdateDTO dto, String videoId) {

        ProfileEntity profile = profileService.getProfile();

        VideoEntity entity = get(videoId);

        if (!entity.getChannel().getProfileId().equals(profile.getId()) &&
                !profile.getRole().equals(ProfileRole.ROLE_ADMIN)) {
            return new ResponseInfoDTO(-1, "no access");
        }

        entity.setDescription(dto.getDescription());
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());

        AttachEntity oldPhoto = entity.getReview();
        entity.setReviewId(dto.getReviewId());

        videoRepository.save(entity);

        if (oldPhoto != null) {
            attachService.deleted(oldPhoto.getUuid());
        }

        return new ResponseInfoDTO(1, "success");

    }

    private VideoEntity get(String videoId) {

        return videoRepository.findById(videoId).orElseThrow(() -> {
            throw new ItemNotFoundException("video not fount");
        });
    }

    public List<VideoShortInfoDTO> searchByCategory(Integer categoryId, Integer size, Integer page) {

        List<VideoShortInfo> search = videoRepository.searchByCategory(categoryId, size, page * size);

        List<VideoShortInfoDTO> dtos = new ArrayList<>();
        search.forEach(info -> {
            dtos.add(new VideoShortInfoDTO(info.getVideoId(),info.getVideoName(),
                    attachService.getAttachOpenUrl(info.getVideoReviewId()), info.getViewCount()));
        });

        return dtos;
    }

    public List<VideoShortInfoDTO> searchByName(String text, Integer size, Integer page) {


        List<VideoShortInfo> search = videoRepository.searchByName("%"+text+"%", size, page * size);

        List<VideoShortInfoDTO> dtos = new ArrayList<>();
        search.forEach(info -> {
            dtos.add(new VideoShortInfoDTO(info.getVideoId(),info.getVideoName(),
                    attachService.getAttachOpenUrl(info.getVideoReviewId()), info.getViewCount()));
        });

        return dtos;
    }

    public List<VideoShortInfoDTO> searchByTag(Integer tagId, Integer size, Integer page) {

        List<VideoShortInfo> search = videoRepository.searchByTag(tagId, size, page * size);

        List<VideoShortInfoDTO> dtos = new ArrayList<>();
        search.forEach(info -> {
            dtos.add(new VideoShortInfoDTO(info.getVideoId(),info.getVideoName(),
                    attachService.getAttachOpenUrl(info.getVideoReviewId()), info.getViewCount()));
        });

        return dtos;

    }

    public VideoFullInfoDTO getById(String videoId) {

        ProfileEntity profile = profileService.getProfile();

        VideoEntity entity = get(videoId);

        if (!entity.getChannel().getProfileId().equals(profile.getId()) &&
                !profile.getRole().equals(ProfileRole.ROLE_ADMIN)) {
            throw new NotPermissionException("no access");
        }

        VideoFullInfoDTO dto = new VideoFullInfoDTO();
        dto.setAttachUrl(attachService.getAttachOpenUrl(entity.getAttachId()));
        dto.setUuid(entity.getUuid());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setReviewId(entity.getReviewId());
        dto.setReviewUrl(attachService.getAttachOpenUrl(entity.getReviewId()));
        dto.setAttachId(entity.getAttachId());
        dto.setCategoryId(entity.getCategoryId());
        dto.setCategoryName(entity.getCategory().getName());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setChannelId(entity.getChannelId());
        dto.setChannelName(entity.getChannel().getName());
        dto.setChannelUrl(attachService.getAttachOpenUrl(entity.getChannel().getAttachId()));
        dto.setShareCount(entity.getSharedCount());

        List<VideoTagEntity> list = videoTagRepository.findAllByVideo(entity);
        List<TagDTO> tagDTOS = new ArrayList<>();

        list.forEach(videoTagEntity -> {
            tagDTOS.add(new TagDTO(videoTagEntity.getTagId(),videoTagEntity.getTag().getName()));
        });
        dto.setTagDTOS(tagDTOS);

        VideoViewLikeDislikeCountAndStatusByProfile count =
                videoWatchedRepository.count(entity.getUuid(), profile.getId());
        dto.setViewCount(count.getViewCount());
        dto.setLikeCount(count.getLikeCount());
        dto.setDislikeCount(count.getDislikeCount());
        dto.setStatus(LikeStatus.valueOf(count.getStatus()));

        return dto;

    }

    public List<VideoShortInfoDTO> pagination(Integer size, Integer page) {

        List<VideoShortInfo> all = videoRepository.paginationForAdmin(page, size);

        List<VideoShortInfoDTO> dtoList = new LinkedList<>();

        all.forEach(info -> {
            dtoList.add(new VideoShortInfoDTO(info.getVideoId(),info.getVideoName(),
                    attachService.getAttachOpenUrl(info.getVideoReviewId()), info.getViewCount()));
        });

        return dtoList;
    }


    public List<VideoShortInfoDTO> searchByChannel(String channelId, Integer size, Integer page) {

        List<VideoShortInfo> search = videoRepository.searchByChannel(channelId, size, page * size);

        List<VideoShortInfoDTO> dtos = new ArrayList<>();
        search.forEach(info -> {
            dtos.add(new VideoShortInfoDTO(info.getVideoId(),info.getVideoName(),
                    attachService.getAttachOpenUrl(info.getVideoReviewId()), info.getViewCount()));
        });

        return dtos;
    }
}
