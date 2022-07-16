package com.company.controller;

import com.company.dto.ResponseInfoDTO;
import com.company.service.VideoTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video_tag")
public class VideoTagController {

    @Autowired
    private VideoTagService videoTagService;

    @DeleteMapping("/video")
    public ResponseEntity<?> deletedFromVideoTag(@RequestParam("videoId") String videoId,
                                                 @RequestParam("tagId") Integer tagId){

        ResponseInfoDTO dto = videoTagService.changeVisible(videoId, tagId);
        return ResponseEntity.ok(dto);

    }

}
