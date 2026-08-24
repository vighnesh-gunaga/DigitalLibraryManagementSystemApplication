package com.example.DigitalLibraryManagementSystem.controller;

import com.example.DigitalLibraryManagementSystem.entity.Publisher;
import com.example.DigitalLibraryManagementSystem.service.PublisherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publisher")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }
    @GetMapping("/allpublisher")
    public List<Publisher> getAllPublisher()
    {
        return publisherService.getAllPublishers();
    }

    @GetMapping("/publisher/{id}")
    public Publisher getPublisherById(@PathVariable Long id)
    {
        return publisherService.getPublisherById(id);
    }

    @PostMapping("/addpublisher")
    public Publisher addPublisher(@RequestBody Publisher publisher)
    {
        return publisherService.addPublisher(publisher);
    }
    @PutMapping("/updatepublisher/{id}")
    public Publisher updatePublisher(@PathVariable Long id,@RequestBody Publisher publisher)
    {
        return publisherService.updatePublisher(id,publisher);
    }
    @DeleteMapping("/deletepublisher/{id}")
    public String deletePublisher(@PathVariable Long id)
    {
        publisherService.deletePublisher(id);
        return "Publisher deleted successfully";
    }
}
