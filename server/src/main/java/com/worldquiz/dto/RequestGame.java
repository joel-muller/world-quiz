/* (C)2026 */
package com.worldquiz.dto;

import com.worldquiz.entities.Category;
import com.worldquiz.entities.Tag;
import java.util.List;

public record RequestGame(Category category, List<Tag> tags, Integer number) {}
