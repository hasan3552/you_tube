package com.company.dto.playlist;

import com.company.query.PlaylistVideoLimit2;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PlaylistShortInfoDTO {

//    private String videoId;
//    private String videoName;
    private List<PlaylistVideoLimit2> videoShortInfoDTOS;
    private String playlistId;
    private String playlistName;
    private LocalDateTime playlistCreatedDate;
    private String channelId;
    private String channelName;
    private Integer countVideo;
    private String attachUrl;

}
