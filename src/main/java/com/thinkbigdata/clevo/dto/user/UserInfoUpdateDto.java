package com.thinkbigdata.clevo.dto.user;

import com.thinkbigdata.clevo.enums.Category;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter @Setter @Builder
public class UserInfoUpdateDto {
    @Min(value = 1) @Max(value = 3)
    private Integer level;
    @Size(min = 1, max = 5)
    private List<Category> category;
}
