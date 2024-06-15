package com.Toyota.sale.dao;


import com.Toyota.sale.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign,Long> {

    Campaign findCampaignByCategoryId(Long id);
}
