package com.example.settlement.config.apollo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author yangwu_i
 * @date 2023/4/30 12:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MchKey {
    private String mchPublicKey;
    private String didiPrivateKey;
    private String mchPrivateKey;
    private String didiPublicKey;
}
