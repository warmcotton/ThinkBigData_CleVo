package com.thinkbigdata.clevo.dto.user;

import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter @Setter
public class UserDashBoardDto {
    private UserDto user;
    private List<UserSentenceDto> user_sentences;
    private List<LearningLogDto> learning_logs;
}
