package com.blog.MonBlog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.MonBlog.dtos.PostDTO;
import com.blog.MonBlog.entity.Post;
import com.blog.MonBlog.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {
	
	
	private final PostService postService;
	
	public PostController(PostService postService){
		this.postService = postService;
	}

    // GET /api/posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // GET /api/posts/{slug}
    @GetMapping("/{slug}")
    public ResponseEntity<Post> getPost(@PathVariable String slug) {
        return ResponseEntity.ok(postService.getPostBySlug(slug));
    }

    // POST /api/posts
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostDTO dto) {
        return ResponseEntity.status(201).body(postService.createPost(dto));
    }

    // DELETE /api/posts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
	

}
