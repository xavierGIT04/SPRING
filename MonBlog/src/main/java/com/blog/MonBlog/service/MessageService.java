package com.blog.MonBlog.service;

import org.springframework.stereotype.Service;

import com.blog.MonBlog.dtos.MessageDTO;
import com.blog.MonBlog.entity.Message;
import com.blog.MonBlog.repository.MessageRepository;

@Service
public class MessageService {
	
	private final MessageRepository messageRepository;

	public MessageService(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}
	
    public Message saveMessage(MessageDTO dto) {
        Message message = new Message();
        message.setName(dto.getName());
        message.setEmail(dto.getEmail());
        message.setMessage(dto.getMessage());
        return messageRepository.save(message);
    }

}
