package com.company.dto;

import com.company.dto.playlist.VideoShortInfoDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaylistFullDTO {

    private String playlistName;
    private String playlistId;
    private Integer playlistViewCount;
    private List<VideoShortInfoDTO> videoShortInfoDTOS;

}
