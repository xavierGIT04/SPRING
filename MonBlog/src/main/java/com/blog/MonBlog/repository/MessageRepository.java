package com.blog.MonBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.MonBlog.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long>{

}
