package com.marketplace.salesapi.product.controller;

import com.marketplace.salesapi.product.model.Tag;
import com.marketplace.salesapi.product.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "API para gerenciamento de tags de produtos")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    @Operation(summary = "Listar todas as tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter tag por ID")
    public ResponseEntity<Tag> getTagById(@PathVariable Long id) {
        Tag tag = tagService.getTagById(id);
        return ResponseEntity.ok(tag);
    }

    @PostMapping
    @Operation(summary = "Criar uma nova tag")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tag> createTag(@RequestParam String name) {
        Tag tag = tagService.createTag(name);
        return new ResponseEntity<>(tag, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma tag")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}