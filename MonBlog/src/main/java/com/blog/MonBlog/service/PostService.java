package com.blog.MonBlog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.blog.MonBlog.dtos.PostDTO;
import com.blog.MonBlog.entity.Post;
import com.blog.MonBlog.repository.PostRepository;

@Service
public class PostService {

	 private final PostRepository postRepository;
	 
	 public  PostService(PostRepository postRepository){
		 this.postRepository = postRepository;
	 }

	    public List<Post> getAllPosts() {
	        return postRepository.findAll();
	    }

	    public Post getPostBySlug(String slug) {
	        return postRepository.findBySlug(slug)
	            .orElseThrow(() -> new RuntimeException("Article non trouvé : " + slug));
	    }

	    public Post createPost(PostDTO dto) {
	        if (postRepository.existsBySlug(dto.getSlug())) {
	            throw new RuntimeException("Ce slug existe déjà");
	        }
	        Post post = new Post();
	        post.setTitle(dto.getTitle());
	        post.setSlug(dto.getSlug());
	        post.setExcerpt(dto.getExcerpt());
	        post.setContent(dto.getContent());
	        return postRepository.save(post);
	    }

	    public void deletePost(Long id) {
	        postRepository.deleteById(id);
	    }
}
