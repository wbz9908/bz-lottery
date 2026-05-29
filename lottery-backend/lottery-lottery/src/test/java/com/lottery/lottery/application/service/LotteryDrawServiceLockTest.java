package com.lottery.lottery.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lottery.common.exception.BusinessException;
import com.lottery.lottery.application.dto.DrawRequest;
import com.lottery.lottery.application.dto.DrawResponse;
import com.lottery.lottery.application.strategy.*;
import com.lottery.domain.entity.DrawRecord;
import com.lottery.domain.entity.Prize;
import com.lottery.lottery.infrastructure.mapper.DrawRecordMapper;
import com.lottery.lottery.infrastructure.mapper.PrizeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LotteryDrawServiceLockTest {

    @Mock
    private PrizeMapper prizeMapper;

    @Mock
    private DrawRecordMapper drawRecordMapper;

    @Mock
    private LotteryStrategyResolver lotteryStrategyResolver;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @InjectMocks
    private LotteryDrawService lotteryDrawService;

    @Test
    void shouldThrowExceptionWhenLockCannotBeAcquired() throws InterruptedException {
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(false);

        BusinessException error = assertThrows(BusinessException.class,
                () -> lotteryDrawService.draw(new DrawRequest(10001L, "REQ-LOCK-1")));
        assertEquals("draw in progress, please try again later", error.getMessage());
        verify(rLock, never()).unlock();
    }

    @Test
    void shouldReleaseLockAfterSuccessfulDraw() throws InterruptedException {
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        Prize prize = prize(5L, "Lucky Prize", 5, "Coffee Gift Box", new BigDecimal("0.18"));
        LotteryDrawStrategy strategy = mock(LotteryDrawStrategy.class);
        DrawStrategyResult result = new DrawStrategyResult(prize, DrawStrategyType.PROBABILITY_ONLY, null, null);

        when(drawRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(drawRecordMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());
        when(prizeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(prize));
        when(lotteryStrategyResolver.resolve()).thenReturn(strategy);
        when(strategy.selectPrize(any())).thenReturn(result);
        when(prizeMapper.decreaseStock(5L)).thenReturn(1);
        when(drawRecordMapper.insert(any(DrawRecord.class))).thenReturn(1);

        lotteryDrawService.draw(new DrawRequest(10001L, "REQ-LOCK-2"));

        verify(rLock).unlock();
    }

    @Test
    void shouldReleaseLockAfterBusinessException() throws InterruptedException {
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        when(drawRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(prizeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());

        assertThrows(BusinessException.class,
                () -> lotteryDrawService.draw(new DrawRequest(10001L, "REQ-LOCK-3")));

        verify(rLock).unlock();
    }

    @Test
    void shouldNotUnlockIfNotHeldByCurrentThread() throws InterruptedException {
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(false);

        Prize prize = prize(5L, "Lucky Prize", 5, "Coffee Gift Box", new BigDecimal("0.18"));
        LotteryDrawStrategy strategy = mock(LotteryDrawStrategy.class);
        DrawStrategyResult result = new DrawStrategyResult(prize, DrawStrategyType.PROBABILITY_ONLY, null, null);

        when(drawRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(drawRecordMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());
        when(prizeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(prize));
        when(lotteryStrategyResolver.resolve()).thenReturn(strategy);
        when(strategy.selectPrize(any())).thenReturn(result);
        when(prizeMapper.decreaseStock(5L)).thenReturn(1);
        when(drawRecordMapper.insert(any(DrawRecord.class))).thenReturn(1);

        lotteryDrawService.draw(new DrawRequest(10001L, "REQ-LOCK-4"));

        verify(rLock, never()).unlock();
    }

    @Test
    void shouldReturnExistingRecordWhenRequestNoExists() throws InterruptedException {
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        DrawRecord existing = new DrawRecord();
        existing.setRecordNo("DRAW-EXISTING");
        existing.setUserId(10001L);
        existing.setDrawStatus(1);
        existing.setPrizeId(10L);
        existing.setPrizeCode("CODE-10");
        existing.setPrizeName("Test Prize");
        existing.setPrizeLevel("Normal");
        existing.setHitProbability(new BigDecimal("0.5"));
        existing.setDrawRemark("Existing draw");

        when(drawRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);

        DrawResponse response = lotteryDrawService.draw(new DrawRequest(10001L, "REQ-LOCK-5"));

        assertEquals("DRAW-EXISTING", response.recordNo());
        assertEquals(1, response.drawStatus());
        verify(rLock).unlock();
        verify(prizeMapper, never()).selectList(any(LambdaQueryWrapper.class));
    }

    private Prize prize(Long id, String level, int levelSort, String name, BigDecimal probability) {
        Prize prize = new Prize();
        prize.setId(id);
        prize.setPrizeLevel(level);
        prize.setPrizeLevelSort(levelSort);
        prize.setPrizeName(name);
        prize.setPrizeCode("CODE-" + id);
        prize.setProbability(probability);
        prize.setAvailableStock(10);
        prize.setStatus(1);
        return prize;
    }
}
