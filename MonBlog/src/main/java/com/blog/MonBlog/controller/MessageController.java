package com.blog.MonBlog.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blog.MonBlog.dtos.MessageDTO;
import com.blog.MonBlog.entity.Message;
import com.blog.MonBlog.service.MessageService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
	
	private final MessageService messageService;
	
	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}

    // POST /api/messages
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody MessageDTO dto) {
        return ResponseEntity.status(201).body(messageService.saveMessage(dto));
    }

}
