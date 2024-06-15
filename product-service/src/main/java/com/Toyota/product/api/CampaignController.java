package com.Toyota.product.api;


import com.Toyota.product.dto.response.CampaignResponse;
import com.Toyota.product.service.Abstract.CampaignService;
import com.Toyota.product.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/campaign")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;


    // Request for using list all campaigns
    @GetMapping("/getAllCampaigns")
    public GenericResponse<List<CampaignResponse>> getAllCampaigns(){

        return GenericResponse.successResult(campaignService.getAllCampaigns(),"success.message.successful");
    }

    /*
    * This request returns de specific campaign by using id
    * @param id of the desired campaign
    */
    @GetMapping("/getOneCampaign/{id}")
    public GenericResponse<CampaignResponse> getOneCampaign(@PathVariable Long id){

        return GenericResponse.successResult(campaignService.getOneCampaign(id),"success.message.successful");
    }
}
