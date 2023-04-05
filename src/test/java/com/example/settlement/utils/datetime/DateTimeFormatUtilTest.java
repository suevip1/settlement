package com.example.settlement.utils.datetime;

import org.junit.jupiter.api.Test;

/**
 *
 * @author yangwu_i
 * @date 2023/4/5 21:10
 */
class DateTimeFormatUtilTest {

    @Test
    void test_CN_YYYY_MM_DD() {
        long timeStamp = System.currentTimeMillis();
        String formatDateTime = DateTimeFormatUtil.format("CN", timeStamp, DateTimeFormatUtil.YYYY_MM_DD);
        System.out.println(formatDateTime);
    }

    @Test
    void test_US_YYYY_MM_DD() {
        long timeStamp = System.currentTimeMillis();
        String formatDateTime = DateTimeFormatUtil.format("US", timeStamp, DateTimeFormatUtil.YYYY_MM_DD);
        System.out.println(formatDateTime);
    }

    @Test
    void test_CN_YYYY_MM_DD_HH_MM_SS() {
        long timeStamp = System.currentTimeMillis();
        String formatDateTime = DateTimeFormatUtil.format("CN", timeStamp, DateTimeFormatUtil.YYYY_MM_DD_HH_MM_SS);
        System.out.println(formatDateTime);
    }

    @Test
    void test_US_YYYY_MM_DD_HH_MM_SS() {
        long timeStamp = System.currentTimeMillis();
        String formatDateTime = DateTimeFormatUtil.format("US", timeStamp, DateTimeFormatUtil.YYYY_MM_DD_HH_MM_SS);
        System.out.println(formatDateTime);
    }
}