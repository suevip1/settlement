package com.example.settlement.config.apollo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 12:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MchDetail {
    private String taxCode; // 税号
    private String postCode; // 邮编
    private String address;
    private String country;
    private String city;
    private String state; // 洲
    private String mcc;
    private String phone;
    private String email;
    private String websiteLink;
}
