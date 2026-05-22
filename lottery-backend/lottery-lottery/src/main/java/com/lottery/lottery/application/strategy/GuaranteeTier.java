package com.lottery.lottery.application.strategy;

import java.util.Set;

// 保底阶梯：threshold 表示"每 N 次抽奖触发保底"（即连续 N-1 次未命中该 tier 后，第 N 次保底命中）
// acceptedLevelSorts 定义该 tier 包含的 prize_level_sort 值（1=特等奖，2-4=中档，5=幸运，6=参与）
public enum GuaranteeTier {
    SPECIAL("special-tier", 50, Set.of(1)),
    MID_TIER("1-3 tier", 30, Set.of(2, 3, 4)),
    LUCKY("lucky-tier", 10, Set.of(5)),
    // threshold=0 使 PARTICIPATION 永远不会作为目标 tier 触发，仅作为降级链的最终兜底
    PARTICIPATION("participation-tier", 0, Set.of(6));

    private final String label;
    private final int threshold;
    private final Set<Integer> acceptedLevelSorts;

    GuaranteeTier(String label, int threshold, Set<Integer> acceptedLevelSorts) {
        this.label = label;
        this.threshold = threshold;
        this.acceptedLevelSorts = acceptedLevelSorts;
    }

    public String label() {
        return label;
    }

    public int threshold() {
        return threshold;
    }

    public boolean matches(Integer prizeLevelSort) {
        return prizeLevelSort != null && acceptedLevelSorts.contains(prizeLevelSort);
    }
}
