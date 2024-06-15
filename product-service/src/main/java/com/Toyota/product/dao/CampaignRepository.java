package com.Toyota.product.dao;


import com.Toyota.product.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign,Long> {

    Campaign findCampaignByCategoryId(Long id);
}
