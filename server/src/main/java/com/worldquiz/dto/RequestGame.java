/* (C)2026 */
package com.worldquiz.dto;

import com.worldquiz.entities.Category;
import com.worldquiz.entities.Tag;
import java.util.List;

public record RequestGame(List<Category> categories, List<Tag> tags, Integer number) {}
