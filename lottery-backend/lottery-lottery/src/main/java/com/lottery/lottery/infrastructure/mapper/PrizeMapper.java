package com.lottery.lottery.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lottery.domain.entity.Prize;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface PrizeMapper extends BaseMapper<Prize> {

    // 乐观锁原子扣减：返回 0 表示库存已耗尽或被并发请求抢先，调用方需重试或报错
    @Update("""
            update "lottery_platform"."lottery_prize"
            set available_stock = available_stock - 1,
                updated_at = current_timestamp
            where id = #{prizeId}
              and deleted = false
              and status = 1
              and available_stock > 0
            """)
    int decreaseStock(@Param("prizeId") Long prizeId);
}
