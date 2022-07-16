package com.company.query;

import java.time.LocalDateTime;

public interface PlaylistShortInfo {

    String getPlaylistId();
    String getPlaylistName();
    LocalDateTime getPlaylistCreatedDate();
    String getChannelId();
    String getChannelName();
    Integer getCountVideo();
}
