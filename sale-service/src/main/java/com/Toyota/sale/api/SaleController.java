package com.Toyota.sale.api;


import com.Toyota.sale.dto.request.SaleRequest;
import com.Toyota.sale.dto.response.SaleResponse;
import com.Toyota.sale.service.Abstract.SaleService;
import com.Toyota.sale.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for handling sale-related requests.
 */
@RestController
@RequestMapping("/sale")
@RequiredArgsConstructor
public class SaleController {
    private static final Logger logger = Logger.getLogger(SaleController.class);
    private final SaleService saleService;

    /**
     * Processes a sale request.
     *
     * @param saleRequest The sale request data.
     * @return A GenericResponse containing the sale response if successful, or an error message if not.
     */
    @PostMapping("/makesale")
    public GenericResponse<SaleResponse> sale(@RequestBody SaleRequest saleRequest){
        logger.info("Sale request received");
        try {
            SaleResponse saleResponse = saleService.sale(saleRequest);
            logger.info("Sale processed successfully");
            return GenericResponse.successResult(saleResponse,"success.message.successful");
        }catch (RuntimeException e){
            logger.error("Error processing sale" + e);
            return GenericResponse.errorResult("success.message.error");
        }
    }
}
