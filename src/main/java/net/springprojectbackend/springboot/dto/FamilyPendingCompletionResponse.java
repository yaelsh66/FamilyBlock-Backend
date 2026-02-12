package net.springprojectbackend.springboot.dto;

public record FamilyPendingCompletionResponse(String kidId, Long taskId, String title, String description, Integer time, String childComment, String parentComment) {

}
