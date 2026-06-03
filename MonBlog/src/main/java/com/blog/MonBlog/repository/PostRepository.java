package com.blog.MonBlog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.MonBlog.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

	Optional<Post> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
