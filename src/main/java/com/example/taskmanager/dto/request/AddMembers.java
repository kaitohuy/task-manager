package com.example.taskmanager.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class AddMembers {
    private List<Long> userIds;
}