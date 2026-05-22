package com.lottery.lottery.application.strategy;

public enum DrawStrategyType {
    PROBABILITY_ONLY,
    GUARANTEE_LADDER;

    // null/空值/无法识别时静默降级为概率模式——避免 DB 配置 typo 导致服务不可用
    public static DrawStrategyType fromConfigValue(String value) {
        if (value == null || value.isBlank()) {
            return PROBABILITY_ONLY;
        }

        for (DrawStrategyType type : values()) {
            if (type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        return PROBABILITY_ONLY;
    }
}
