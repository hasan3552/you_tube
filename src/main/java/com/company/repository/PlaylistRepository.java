package com.company.repository;

import com.company.entity.PlaylistEntity;
import com.company.query.PlaylistFullInfo;
import com.company.query.PlaylistShortInfo;
import com.company.query.PlaylistVideoLimit2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface PlaylistRepository extends JpaRepository<PlaylistEntity, String> {

    @Query(value = "select c.* " +
            "from playlist c " +
            "order by c.created_date " +
            "limit :limit " +
            "offset :offset", nativeQuery = true)
    List<PlaylistEntity> pagination(@Param("limit") Integer limit, @Param("offset") Integer offset);

//    id, name,created_date,channel(id,name),video_count,video_list[{id,name,key,duration}] (first 2)

    @Query(value = "SELECT p.uuid as playlistId, p.name as playListName, p.created_date as playListCreatedDate, " +
            "                       c.uuid as channelId, c.name as channelName, " +
            "                       (select cast(count(*) as int) from playlist_video  as pv where pv.playlist_id = p.uuid ) as countVideo " +
            "                   from  playlist as p " +
            "                   inner JOIN channel as c on p.channel_id = c.uuid " +
            "                   where c.uuid =:channelId " +
            "                   and c.visible and p.visible", nativeQuery = true)
    List<PlaylistShortInfo> getPlayListByChannelId(@Param("channelId") String channelId);

    @Query(value = "select pv.video_id as videoId, v.name as videoName " +
            "from playlist_video as pv " +
            "inner join video as v on pv.video_id = v.uuid " +
            "where pv.playlist_id =:plId " +
            "and pv.visible " +
            "and v.visible " +
            "order by pv.order_number , pv.created_date " +
            "limit 2", nativeQuery = true)
    List<PlaylistVideoLimit2> playlistShortInfoLimit2(@Param("plId") String playlistId);

    // id,name,video_count, total_view_count (shu play listdagi videolarni ko'rilganlar soni last_update_date

    @Query(value = "select pv.playlist_id as playlistId, pv.playlist_name, as playlistName " +
            "v.name as videoName, v.uuid as videoId," +
            " v.review_id as reviewId, " +
            "(select cast(count(pwv.*) as int) as viewCount " +
            "from profile_watch_video as pwv " +
            "where pwv.video_id = pv.video_id) " +
            "inner join video as v on pv.video_id = v.uuid " +
            "where pv.playlist_id = :plId "+
            "and pv.visible " +
            "and v.visible " +
            "order by pv.order_number , pv.created_date ",nativeQuery = true)
    List<PlaylistFullInfo> playlistFullInfoList(@Param("plId") String playlistId);

}
