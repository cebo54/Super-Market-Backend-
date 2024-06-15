package com.Toyota.product.service.concrete;


import com.Toyota.product.dao.CampaignRepository;
import com.Toyota.product.dto.response.CampaignResponse;
import com.Toyota.product.entity.Campaign;
import com.Toyota.product.entity.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CampaignServiceImplTest {
    private CampaignServiceImpl campaignService;
    private CampaignRepository campaignRepository;

    @BeforeEach
    void setUp() {
        campaignRepository=Mockito.mock(CampaignRepository.class);
        campaignService=new CampaignServiceImpl(campaignRepository);
    }
    public Campaign generateCampaign(){
        return  Campaign.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .category(generateCategory())
                .build();
    }
    public Category generateCategory(){
        return Category.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldReturnAllCampaignsWithCampaignResponse() {
        Campaign campaign1=generateCampaign();
        Campaign campaign2=generateCampaign();

        List<Campaign> campaignList= Arrays.asList(campaign1,campaign2);

        Mockito.when(campaignRepository.findAll()).thenReturn(campaignList);

        List<CampaignResponse>campaignResponses=campaignService.getAllCampaigns();

        Mockito.verify(campaignRepository,Mockito.times(1)).findAll();
        assertEquals(campaignList.size(),campaignResponses.size());
        for (int i =0 ; i<campaignList.size();i++){
            assertEquals(campaignList.get(i).getName(),campaignResponses.get(i).getName());
            assertEquals(campaignList.get(i).getDescription(),campaignResponses.get(i).getDescription());
        }


    }

    @Test
    void shouldReturnOneCampaignWithCampaignResponse_whenCampaignIdExist() {
        Long id=1L;
        Campaign campaign=generateCampaign();
        campaign.setId(id);
        campaign.setName("name");
        campaign.setDescription("desc");
        campaign.setCategory(generateCategory());

        Mockito.when(campaignRepository.findById(id)).thenReturn(Optional.of(campaign));
        CampaignResponse campaignResponse=campaignService.getOneCampaign(id);
        assertEquals(campaign.getName(),campaignResponse.getName());
        assertEquals(campaign.getDescription(),campaignResponse.getDescription());
        Mockito.verify(campaignRepository,Mockito.times(1)).findById(id);


    }
}