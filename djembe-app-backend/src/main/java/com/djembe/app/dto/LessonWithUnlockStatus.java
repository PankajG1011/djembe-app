package com.djembe.app.dto;

import com.djembe.app.model.Lesson;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonWithUnlockStatus {
    private Lesson lesson;
    private boolean unlocked;
}
