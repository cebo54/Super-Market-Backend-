package com.Toyota.BackendProject.api;

import com.Toyota.BackendProject.Util.GenericResponse;
import com.Toyota.BackendProject.dto.response.CampaignResponse;
import com.Toyota.BackendProject.service.Abstract.CampaignService;
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

    @GetMapping("/getAllCampaigns")
    public GenericResponse<List<CampaignResponse>> getAllCampaigns(){

        return GenericResponse.successResult(campaignService.getAllCampaigns(),"success.message.successful");
    }
    @GetMapping("/getOneCampaign/{id}")
    public GenericResponse<CampaignResponse> getOneCampaign(@PathVariable Long id){

        return GenericResponse.successResult(campaignService.getOneCampaign(id),"success.message.successful");
    }
}
