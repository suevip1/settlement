package com.example.settlement.settle.model.valueobj;

import com.example.settlement.clear.model.ClearingSetting;
import com.example.settlement.common.enums.SettleModeEnum;
import com.example.settlement.common.exceptions.ErrorNo;
import com.example.settlement.common.exceptions.ErrorNoException;
import com.example.settlement.settle.infra.SettleErrorNo;
import com.example.settlement.settle.infra.enums.DelayCalcMode;
import com.example.settlement.settle.infra.enums.SettleCycleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.AbstractSet;
import java.util.Date;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.WEEKS;

/**
 *
 * @author yangwu_i
 * @date 2023/5/1 09:37
 */
@Slf4j
public record SettleConfig(Instant SETTLE_ZERO, ChronoUnit TRADE_CLEARING_SUMMARY_MODE, ClearingSetting setting)
        implements Comparable<SettleConfig> {
    public SettleConfig(ClearingSetting setting) {
        // 汇总模式：目前只支持天级别汇总
        this(Instant.parse("2022-01-01T00:00:00Z"), ChronoUnit.DAYS, setting);
    }

    @Override
    public int compareTo(SettleConfig o) {
        return setting.getActivationTime().compareTo(o.setting().getActivationTime());
    }

    // 当前交易是否匹配结算配置
    public boolean match(long userId, int userProduct, Date transTime) {
        return setting.getUserId().equals(userId)
                && setting.getUserProduct().equals(userProduct)
                && setting.getActivationTime().compareTo(transTime) <= 0;
    }

    // 最近（3天）一段时间的起始时间（根据配置来处理最近结算详情单）
    public Date getStartAtTimeOfRecentPeriod() {
        if (TRADE_CLEARING_SUMMARY_MODE == ChronoUnit.DAYS) {
            return Date.from(Instant.now().atZone(setting.getZoneId()).truncatedTo(ChronoUnit.DAYS).minusDays(2).toInstant());
        }
        throw new ErrorNoException(ErrorNo.SERVER_ERROR, "暂不支持的结算配置");
    }

    // 取交易日期当天0点
    public Date getSummaryTime(Date transTime) {
        if (TRADE_CLEARING_SUMMARY_MODE == ChronoUnit.DAYS) {
            return Date.from(transTime.toInstant().atZone(setting.getZoneId()).truncatedTo(ChronoUnit.DAYS).toInstant());
        }
        throw new ErrorNoException(ErrorNo.SERVER_ERROR, "暂不支持的结算配置");
    }

    public Date getSettleTime(Date transTime) {
        Date endTime = getLiquidEndDay(transTime);
        ZonedDateTime zonedTime = endTime.toInstant().atZone(setting.getZoneId());
        SettleCycleEnum cycle = getSettleCycle();
        switch (cycle.getDelayCalcMode()) {
            case WORKDAY:
                // 结算遇指定延期的工作日天数结束
                int count = 0;
                while (true) {
                    if (!isHoliday(zonedTime)) {
                        count++;
                    }
                    if (count >= cycle.getCount()) {
                        return Date.from(zonedTime.toInstant());
                    }
                    zonedTime = zonedTime.plusDays(1);
                }
            case NATUAL_DAY:
                return Date.from(zonedTime.plusDays(cycle.getCount() - 1).toInstant());
            default:
                throw new ErrorNoException(SettleErrorNo.SETTLE_UNSUPPORTED_DELAY_MODE, "不支持的延期计算模式");
        }
    }

    private Date getLiquidEndDay(Date transTime) {
        SettleCycleEnum cycle = getSettleCycle();

        ZonedDateTime startTime = transTime.toInstant().atZone(setting.getZoneId()).truncatedTo(ChronoUnit.DAYS);
        switch (cycle.getSummaryMode()) {
            case DAY:
                return extendEndTime(startTime, ChronoUnit.DAYS, 1, cycle.getDelayCalcMode());
            case WEEK:
                startTime = startTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                return extendEndTime(startTime, WEEKS, 1, cycle.getDelayCalcMode());
            case BIWEEK:
                startTime = startTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                long weeks = WEEKS.between(startTime, SETTLE_ZERO.atZone(setting.getZoneId()));
                startTime = startTime.minusWeeks(weeks % 2);
                return extendEndTime(startTime, WEEKS, 2, cycle.getDelayCalcMode());
            case MONTH:
                startTime = startTime.with(TemporalAdjusters.firstDayOfMonth());
                return extendEndTime(startTime, ChronoUnit.MONTHS, 1, cycle.getDelayCalcMode());
            default:
                throw new ErrorNoException(SettleErrorNo.SETTLE_UNSUPPORTED_CYCLE, "不支持的结算周期");
        }
    }

    public static void main(String[] args) {
        ZonedDateTime now = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);
        ZonedDateTime now2 = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        System.out.println(now);
        System.out.println(now2);

        Instant parse = Instant.parse("2022-01-01T00:00:00Z");
        ZonedDateTime date = parse.atZone(ZoneId.systemDefault());
        long weeks = WEEKS.between(now2, date);
        System.out.println(weeks);

        ZonedDateTime now3 = now2.minusWeeks(weeks % 2);
        System.out.println(now3);

    }
    // 正常情况向后延期 count 个 unit，但是 workday 模式下，如果后面 count 个 unit 也是假期
    // 则需要对其扩展 count 个 unit
    private Date extendEndTime(ZonedDateTime startTime, ChronoUnit unit, int count, DelayCalcMode delayCalcMode) {
        // 按天（工作日）情况下，汇总自当日起，遇工作日结束
        // 按周（工作日）情况下，汇总自当周起，遇非全休工作周结束
        // 以此类推 双周 月
        ZonedDateTime endTime = startTime.plus(count, unit);
        switch (delayCalcMode) {
            case WORKDAY:
                while (isAllHolidayForward(endTime, unit, count)) {
                    endTime = endTime.plus(count, unit);
                }
                return Date.from(endTime.toInstant());
            case NATUAL_DAY:
                return Date.from(endTime.toInstant());
            default:
                throw new ErrorNoException(SettleErrorNo.SETTLE_UNSUPPORTED_DELAY_MODE, "不支持的延期计算模式");
        }
    }

    private boolean isAllHolidayForward(ZonedDateTime date, ChronoUnit unit, int count) {
        ZonedDateTime endTime = date.plus(count, unit);
        while (isHoliday(date)) {
            // 已经达到 endTime, 说明全是 holiday 返回true
            if (date.until(endTime, DAYS) <= 1) {
                return true;
            }
            date = date.plusDays(1);
        }
        return false;
    }

    private boolean isHoliday(ZonedDateTime date) {
        Assert.notNull(setting.getCountryCode(), "国家代码不能为空");
        Assert.notNull(setting.getCityId(), "城市代码不能为空");

        try {
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            // 中国没有银行节假日，采用公众假日
            if (StringUtils.equalsIgnoreCase("CN", setting.getCountryCode())) {
                // todo: 调用公众假日接口
                return false;
            } else {
                // todo: 调用银行假日接口
                return true;
            }
        } catch (Exception e) {
            log.error("", e);
            throw new ErrorNoException(SettleErrorNo.SETTLE_HOLIDAY_ERROR, "假日接口调用失败");
        }
    }

    // 获取结算周期：实时结算、分期结算（后置汇总）、默认自然日+1生成结算单
    private SettleCycleEnum getSettleCycle() {
        // todo: 待完成分期结算（后置汇总）模式
        return SettleModeEnum.REAL_TIME.getValue() == setting.getSettleMode() ?
                SettleCycleEnum.DAY_AFTER_1_DAY :
                SettleCycleEnum.valueOf(setting.getSettleMode());
    }
}
