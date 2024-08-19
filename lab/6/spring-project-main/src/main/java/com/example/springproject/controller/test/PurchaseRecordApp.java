package com.example.springproject.controller.test;

import com.example.springproject.domain.Purchase;
import com.example.springproject.dto.PurchaseDto;
import com.example.springproject.service.purchase.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exer")
public class PurchaseRecordApp {

    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseRecordApp(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping("/record")
    public List<Purchase> findAll(){
        //Fot testing the concrete class
        System.out.println(purchaseService.getClass().getName());
        return purchaseService.findAll();
    }

    @PostMapping("/record")
    public PurchaseDto addOne(@RequestBody PurchaseDto purchaseDto){
        System.out.println(purchaseDto.getUsername());
        purchaseService.save(purchaseDto);
        return purchaseService.findById(purchaseDto.getId());
    }

    @PutMapping("/record2")
    public PurchaseDto update(PurchaseDto purchaseDto){
        purchaseService.save(purchaseDto);
        return purchaseService.findById(purchaseDto.getId());
    }

    @PutMapping("/record")
    public PurchaseDto update(@RequestParam long id,
                              @RequestParam String username,
                              @RequestParam String date,
                              @RequestParam double money,
                              @RequestParam int type,
                              @RequestParam String description){
        PurchaseDto request = PurchaseDto.builder()
            .id(id)
            .username(username)
            .description(description)
            .money(money)
            .type(type)
            .date(date)
            .build();
        purchaseService.save(request);
        return purchaseService.findById(request.getId());
    }

    @DeleteMapping("record/{id}")
    public void deleteOne(@PathVariable long id){
        purchaseService.deleteById(id);
    }

    @PostMapping("find")
    public List<Purchase> findByNameANDType(@RequestParam String username,
                                                  @RequestParam int type){
        return purchaseService.findByNameAndAndType(username,type);
    }



}
