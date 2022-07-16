package com.company.controller;

import com.company.dto.ResponseInfoDTO;
import com.company.dto.playlist.VideoShortInfoDTO;
import com.company.dto.video.VideoCreatedDTO;
import com.company.dto.video.VideoFullInfoDTO;
import com.company.dto.video.VideoUpdateDTO;
import com.company.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("")
    public ResponseEntity<?> created(@RequestBody @Valid VideoCreatedDTO dto) {

        ResponseInfoDTO dto1 = videoService.createdVideo(dto);
        return ResponseEntity.ok(dto1);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody VideoUpdateDTO dto,
                                    @PathVariable("id") String videoId) {

        ResponseInfoDTO update = videoService.update(dto, videoId);
        return ResponseEntity.ok(update);
    }

    @GetMapping("/public/pagination")
    public ResponseEntity<?> pagination(@RequestParam("categoryId") Integer categoryId,
                                        @RequestParam("size") Integer size,
                                        @RequestParam("page") Integer page) {

        List<VideoShortInfoDTO> dtos = videoService.searchByCategory(categoryId, size, page);
        return ResponseEntity.ok(dtos);

    }

    @GetMapping("/public/search_title")
    public ResponseEntity<?> pagination(@RequestParam("search_text") String text,
                                        @RequestParam("size") Integer size,
                                        @RequestParam("page") Integer page) {

        List<VideoShortInfoDTO> dtos = videoService.searchByName(text, size, page);
        return ResponseEntity.ok(dtos);

    }

    @GetMapping("/public/search_tag")
    public ResponseEntity<?> paginationByTag(@RequestParam("tagId") Integer tagId,
                                             @RequestParam("size") Integer size,
                                             @RequestParam("page") Integer page) {

        List<VideoShortInfoDTO> dtos = videoService.searchByTag(tagId, size, page);
        return ResponseEntity.ok(dtos);

    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String videoId) {

        VideoFullInfoDTO dto = videoService.getById(videoId);
        return ResponseEntity.ok(dto);

    }

    @GetMapping("/adm/pagination")
    public ResponseEntity<?> pagination(@RequestParam("size") Integer size,
                                        @RequestParam("page") Integer page) {

        List<VideoShortInfoDTO> dtos = videoService.pagination(size, page);
        return ResponseEntity.ok(dtos);

    }

    @GetMapping("/public/search_channel")
    public ResponseEntity<?> paginationByChannel(@RequestParam("channleId") String channelId,
                                        @RequestParam("size") Integer size,
                                        @RequestParam("page") Integer page) {

        List<VideoShortInfoDTO> dtos = videoService.searchByChannel(channelId, size, page);
        return ResponseEntity.ok(dtos);

    }

}
