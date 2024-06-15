package com.Toyota.product.service.concrete;


import com.Toyota.product.dao.CampaignRepository;
import com.Toyota.product.dto.response.CampaignResponse;
import com.Toyota.product.entity.Campaign;
import com.Toyota.product.service.Abstract.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    @Override
    public List<CampaignResponse> getAllCampaigns() {
        List<Campaign>campaigns=campaignRepository.findAll();
        List<CampaignResponse>cvr=campaigns.stream().map(CampaignResponse::convert).collect(Collectors.toList());
        return cvr;
    }

    @Override
    public CampaignResponse getOneCampaign(Long id) {
        Campaign campaign=campaignRepository.findById(id).orElseThrow();
        return CampaignResponse.convert(campaign);
    }


}
