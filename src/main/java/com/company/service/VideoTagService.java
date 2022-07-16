package com.company.service;

import com.company.dto.ResponseInfoDTO;
import com.company.entity.VideoTagEntity;
import com.company.exp.ItemNotFoundException;
import com.company.repository.VideoTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VideoTagService {

    @Autowired
    private VideoTagRepository videoTagRepository;

    public ResponseInfoDTO changeVisible(String videoId, Integer tagId) {

        Optional<VideoTagEntity> optional = videoTagRepository.findByVideoIdAndTagId(videoId, tagId);

        if (optional.isEmpty()){
            throw new ItemNotFoundException("not fount");
        }

        VideoTagEntity entity = optional.get();
        entity.setVisible(!entity.getVisible());

        videoTagRepository.save(entity);

        return new ResponseInfoDTO(1,"success");
    }
}
