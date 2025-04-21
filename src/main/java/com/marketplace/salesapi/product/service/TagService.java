package com.marketplace.salesapi.product.service;

import com.marketplace.salesapi.product.model.Tag;
import com.marketplace.salesapi.product.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Transactional
    public Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name.trim().toLowerCase());
        return tagRepository.save(tag);
    }

    public Tag getOrCreateTag(String name) {
        return tagRepository.findByName(name.trim().toLowerCase())
                .orElseGet(() -> createTag(name));
    }

    public Set<Tag> getOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                tags.add(getOrCreateTag(tagName));
            }
        }
        return tags;
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag não encontrada com id: " + id));
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new EntityNotFoundException("Tag não encontrada com id: " + id);
        }
        tagRepository.deleteById(id);
    }
}