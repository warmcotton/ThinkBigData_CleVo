package com.thinkbigdata.clevo.dto.user;

import com.thinkbigdata.clevo.dto.CustomPage;
import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter @Setter
public class UserDashBoardDto {
    private UserDto user;
    private CustomPage<UserSentenceDto> user_sentences;
    private CustomPage<LearningLogDto> learning_logs;
}
