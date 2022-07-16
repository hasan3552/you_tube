package com.company.dto.video;

import com.company.entity.AttachEntity;
import com.company.entity.ChannelEntity;
import com.company.enums.VideoStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
public class VideoUpdateDTO {

    private String name;
    private String description;
    private String reviewId;
    private VideoStatus status;

}
