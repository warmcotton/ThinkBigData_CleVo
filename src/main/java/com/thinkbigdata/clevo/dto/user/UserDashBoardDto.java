package com.thinkbigdata.clevo.dto.user;

import com.thinkbigdata.clevo.dto.CustomPage;
import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
@Getter @Setter
public class UserDashBoardDto {
    private UserDto user;
    private Page<UserSentenceDto> user_sentences;
    private Page<LearningLogDto> learning_logs;
}
