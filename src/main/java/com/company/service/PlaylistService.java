package com.company.service;

import com.company.dto.ResponseInfoDTO;
import com.company.dto.PlaylistFullDTO;
import com.company.dto.playlist.*;
import com.company.entity.AttachEntity;
import com.company.entity.PlaylistEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.ProfileRole;
import com.company.exp.BadRequestException;
import com.company.exp.ItemNotFoundException;
import com.company.exp.NotPermissionException;
import com.company.query.PlaylistFullInfo;
import com.company.query.PlaylistVideoLimit2;
import com.company.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private AttachService attachService;
    @Autowired
    private ProfileService profileService;


    public PlaylistDTO created(PlaylistCreatedDTO dto) {

        PlaylistEntity entity = new PlaylistEntity();
        entity.setName(dto.getName());
        entity.setOrder(dto.getOrder());
        entity.setAttachId(dto.getAttachId());
        entity.setChannelId(dto.getChannelId());

        playlistRepository.save(entity);

        return getDTO(entity);
    }

    private PlaylistDTO getDTO(PlaylistEntity entity) {

        PlaylistDTO dto = new PlaylistDTO();
        dto.setName(entity.getName());
        dto.setOrder(entity.getOrder());
        dto.setChannel(channelService.getById(entity.getChannelId()));
        dto.setAttachUrl(attachService.getAttachOpenUrl(entity.getAttachId()));
        dto.setStatus(entity.getStatus());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setVisible(entity.getVisible());
        dto.setUuid(entity.getUuid());

        return dto;
    }

    public PlaylistDTO update(PlaylistUpdateDTO dto, String playlistId) {

        PlaylistEntity entity = get(playlistId);
        if (!entity.getChannel().getProfileId().equals(profileService.getProfile().getId())) {
            throw new BadRequestException("No access");
        }

        entity.setName(dto.getName());
        entity.setOrder(dto.getOrder());
        entity.setStatus(dto.getStatus());

        AttachEntity oldPhoto = entity.getAttach();
        entity.setAttachId(dto.getAttachId());

        playlistRepository.save(entity);

        if (oldPhoto != null) {
            attachService.deleted(oldPhoto.getUuid());
        }

        return getDTO(entity);
    }

    private PlaylistEntity get(String playlistId) {

        return playlistRepository.findById(playlistId).orElseThrow(() -> {
            throw new ItemNotFoundException("palylist not fount");
        });
    }

    public ResponseInfoDTO changeVisible(String pId) {

        ProfileEntity profile = profileService.getProfile();
        PlaylistEntity playlist = get(pId);
        if (!profile.getRole().equals(ProfileRole.ROLE_ADMIN) &&
                !playlist.getChannel().getProfileId().equals(profile.getId())) {
            throw new NotPermissionException("No access");
        }

        playlist.setVisible(!playlist.getVisible());
        playlistRepository.save(playlist);

        return new ResponseInfoDTO(1, "success");
    }

    public List<PlaylistDTO> pagination(Integer page, Integer size) {

        List<PlaylistEntity> pagination = playlistRepository.pagination(size, page * size);

        List<PlaylistDTO> playlistDTO = new ArrayList<>();
        pagination.forEach(playlist -> {
            playlistDTO.add(getDTO(playlist));
        });

        return playlistDTO;
    }

    public List<PlaylistShortInfoDTO> getPlaylistByChannel(String channelId) {

        List<PlaylistShortInfoDTO> playlistShortInfoDTOS = new ArrayList<>();

        playlistRepository.getPlayListByChannelId(channelId).forEach(playlistShortInfo -> {

            List<PlaylistVideoLimit2> infoLimit2 = playlistRepository
                    .playlistShortInfoLimit2(playlistShortInfo.getPlaylistId());

            PlaylistShortInfoDTO dto = new PlaylistShortInfoDTO();
            dto.setVideoShortInfoDTOS(infoLimit2);
            dto.setPlaylistId(playlistShortInfo.getPlaylistId());
            dto.setChannelId(playlistShortInfo.getChannelId());
            dto.setChannelName(playlistShortInfo.getChannelName());
            dto.setPlaylistName(playlistShortInfo.getPlaylistName());
            dto.setPlaylistCreatedDate(playlistShortInfo.getPlaylistCreatedDate());
            dto.setCountVideo(playlistShortInfo.getCountVideo());
//            dto.setAttachUrl(attachService.getAttachOpenUrl());

            playlistShortInfoDTOS.add(dto);
        });

        return playlistShortInfoDTOS;
    }

    public PlaylistFullDTO getPlaylistVideosByPlaylistId(String playlistId) {

        List<PlaylistFullInfo> list = playlistRepository.playlistFullInfoList(playlistId);

        PlaylistFullDTO dto = new PlaylistFullDTO();
        dto.setPlaylistId(list.get(0).getPlaylistId());
        dto.setPlaylistName(list.get(0).getPlaylistName());

        int playlistViewCount = 0;

        List<VideoShortInfoDTO> dtos = new ArrayList<>();

        for (PlaylistFullInfo playlistFullInfo : list) {
            dtos.add(new VideoShortInfoDTO(playlistFullInfo.getVideoId(), playlistFullInfo.getVideoName(),
                    attachService.getAttachOpenUrl(playlistFullInfo.getReviewId()), playlistFullInfo.getViewCount()));

            playlistViewCount += playlistFullInfo.getViewCount();
        }

        dto.setPlaylistViewCount(playlistViewCount);
        dto.setVideoShortInfoDTOS(dtos);

        return dto;
    }
}
