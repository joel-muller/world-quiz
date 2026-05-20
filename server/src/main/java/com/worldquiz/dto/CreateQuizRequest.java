/* (C)2026 */
package com.worldquiz.dto;

import com.worldquiz.entities.Category;
import com.worldquiz.entities.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateQuizRequest(
        @NotNull @NotEmpty List<Category> categories,
        @NotNull @NotEmpty List<Tag> tags,
        @NotNull @Min(1) @Max(10000) Integer number) {}
