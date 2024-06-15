package com.Toyota.product.service.Abstract;

import com.Toyota.product.dto.response.CampaignResponse;

import java.util.List;

public interface CampaignService {
    List<CampaignResponse> getAllCampaigns();

    CampaignResponse getOneCampaign(Long id);
}
