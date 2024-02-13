package com.Toyota.BackendProject.Dao;

import com.Toyota.BackendProject.Entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign,Long> {

    Campaign findCampaignByCategoryId(Long id);
}
