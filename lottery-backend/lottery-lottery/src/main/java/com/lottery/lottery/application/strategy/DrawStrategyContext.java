package com.lottery.lottery.application.strategy;

import com.lottery.domain.entity.DrawRecord;
import com.lottery.domain.entity.Prize;

import java.util.List;

public record DrawStrategyContext(
        Long userId,
        List<Prize> availablePrizes,
        List<DrawRecord> recentDrawRecords,
        DrawHistorySnapshot historySnapshot
) {
}
