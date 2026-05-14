/* (C)2026 */
package com.worldquiz.dto;

import com.worldquiz.entities.Category;
import com.worldquiz.entities.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateQuizRequest(
        @NotNull @NotEmpty List<Category> categories,
        @NotNull @NotEmpty List<Tag> tags,
        Integer number) {}
