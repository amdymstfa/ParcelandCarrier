package com.logistics.parcelandcarrier.service;

import com.logistics.parcelandcarrier.dto.request.TransporterRequest;
import com.logistics.parcelandcarrier.dto.response.UserResponse;
import com.logistics.parcelandcarrier.entity.User;
import com.logistics.parcelandcarrier.mapper.UserMapper;
import com.logistics.parcelandcarrier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransporterService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserResponse createTransporter(TransporterRequest request) {

    String hashedPassword = passwordEncoder.encode(request.getPassword());

    User entity = userMapper.toEntity(request, hashedPassword);

    User saved = userRepository.save(entity);

    return userMapper.toResponse(saved);
  }

  public UserResponse updateTransporter(String id, TransporterRequest request) {
    User entity = userRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

    String hashedPassword = request.getPassword() != null
      ? passwordEncoder.encode(request.getPassword())
      : null;

    userMapper.updateEntityFromDto(request, hashedPassword, entity);

    User updated = userRepository.save(entity);
    return userMapper.toResponse(updated);
  }
}
