package com.example.DigitalLibraryManagementSystem.service;

import com.example.DigitalLibraryManagementSystem.entity.Publisher;
import com.example.DigitalLibraryManagementSystem.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public Publisher addPublisher(Publisher publisher) {

        if (publisherRepository.existsByNameIgnoreCase(publisher.getName())) {
            throw new RuntimeException("Publisher already exists");
        }

        return publisherRepository.save(publisher);
    }

    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public Publisher getPublisherById(Long id) {

        return publisherRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Publisher not found"));
    }

    public Publisher updatePublisher(Long id, Publisher publisher) {

        Publisher existingPublisher = getPublisherById(id);

        existingPublisher.setName(publisher.getName());

        return publisherRepository.save(existingPublisher);
    }

    public void deletePublisher(Long id) {

        Publisher publisher = getPublisherById(id);

        publisherRepository.delete(publisher);
    }
}