package com.Toyota.BackendProject.service.Abstract;

import com.Toyota.BackendProject.dto.response.CampaignResponse;

import java.util.List;

public interface CampaignService {
    List<CampaignResponse> getAllCampaigns();

    CampaignResponse getOneCampaign(Long id);
}
