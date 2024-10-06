package com.thinkbigdata.clevo.dto.user;

import com.thinkbigdata.clevo.category.Category;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UserInfoDto {
    @NotNull
    @Min(value = 1) @Max(value = 3)
    private Integer level;
    @NotNull
    @Size(min = 1, max = 5)
    private List<Category> category;
}
