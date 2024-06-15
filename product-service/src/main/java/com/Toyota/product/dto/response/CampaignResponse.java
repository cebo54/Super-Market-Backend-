package com.Toyota.product.dto.response;

import com.Toyota.product.entity.Campaign;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignResponse {
    private String name;

    private String description;



    public static CampaignResponse convert(Campaign campaign){
        return CampaignResponse.builder()
                .name(campaign.getName())
                .description(campaign.getDescription())
                .build();
    }
}
