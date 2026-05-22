package com.lottery.lottery.application.dto;

// requestNo 为 null 时 service 层自动生成（格式：REQ + 时间戳 + userId），非 null 时用于幂等去重
public record DrawRequest(Long userId, String requestNo) {
}
