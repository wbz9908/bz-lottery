package com.lottery.lottery.application.strategy;

import com.lottery.domain.entity.Prize;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

final class WeightedPrizeSelector {

    private WeightedPrizeSelector() {
    }

    // BigDecimal→double 存在精度损失，对于极小概率大量奖品的场景可能引入偏差
    static Prize select(List<Prize> prizes) {
        BigDecimal totalProbability = prizes.stream()
                .map(Prize::getProbability)
                .filter(probability -> probability != null && probability.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalProbability.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        double randomValue = ThreadLocalRandom.current().nextDouble(totalProbability.doubleValue());
        double cumulativeProbability = 0D;
        Prize lastPrize = null;

        for (Prize prize : prizes) {
            if (prize.getProbability() == null || prize.getProbability().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            cumulativeProbability += prize.getProbability().doubleValue();
            lastPrize = prize;
            if (randomValue < cumulativeProbability) {
                return prize;
            }
        }

        // 浮点精度导致 randomValue 落在总概率和累积值之间的间隙时，兜底返回最后一个有效奖品
        return lastPrize;
    }
}
