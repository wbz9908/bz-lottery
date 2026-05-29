package com.lottery.lottery.application.strategy;

import com.lottery.domain.entity.Prize;

public record DrawStrategyResult(
        Prize prize,
        DrawStrategyType strategyType,
        GuaranteeTier targetGuaranteeTier,
        GuaranteeTier actualGuaranteeTier
) {
    // 仅当 targetGuaranteeTier 非空时才表示保底命中（概率命中时此字段为 null）
    public boolean isGuaranteeHit() {
        return targetGuaranteeTier != null;
    }

    // 保底降级：命中保底但实际发放的奖品层级低于目标层级（如 SPECIAL 因库存不足降级到 MID_TIER）
    public boolean isDowngradedGuarantee() {
        return isGuaranteeHit() && actualGuaranteeTier != null && actualGuaranteeTier != targetGuaranteeTier;
    }
}
