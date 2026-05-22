package com.lottery.lottery.application.strategy;

import com.lottery.lottery.domain.entity.LotterySystemConfig;
import com.lottery.lottery.infrastructure.mapper.LotterySystemConfigMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class LotteryStrategyResolver {

    public static final String STRATEGY_CONFIG_KEY = "LOTTERY_DRAW_STRATEGY";

    private final LotterySystemConfigMapper lotterySystemConfigMapper;
    private final Map<DrawStrategyType, LotteryDrawStrategy> strategies;
    // volatile 确保 admin 线程 reload() 后所有请求线程立即可见
    private volatile DrawStrategyType activeType = DrawStrategyType.PROBABILITY_ONLY;

    public LotteryStrategyResolver(
            LotterySystemConfigMapper lotterySystemConfigMapper,
            List<LotteryDrawStrategy> strategyList
    ) {
        this.lotterySystemConfigMapper = lotterySystemConfigMapper;
        this.strategies = new EnumMap<>(DrawStrategyType.class);
        strategyList.forEach(strategy -> this.strategies.put(strategy.getType(), strategy));
    }

    @PostConstruct
    public void warmUp() {
        reload();
    }

    // activeType 不在 map 中时（如策略 Bean 加载失败）静默降级为概率模式
    public LotteryDrawStrategy resolve() {
        return strategies.getOrDefault(activeType, strategies.get(DrawStrategyType.PROBABILITY_ONLY));
    }

    public DrawStrategyType currentType() {
        return activeType;
    }

    public void reload() {
        LotterySystemConfig config = lotterySystemConfigMapper.selectById(STRATEGY_CONFIG_KEY);
        activeType = DrawStrategyType.fromConfigValue(config == null ? null : config.getConfigValue());
    }
}
