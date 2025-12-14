package com.logistics.parcelandcarrier.service;

import com.logistics.parcelandcarrier.dto.request.PackageRequest;
import com.logistics.parcelandcarrier.dto.response.PackageResponse;
import com.logistics.parcelandcarrier.mapper.PackageMapper;
import com.logistics.parcelandcarrier.repository.PackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.logistics.parcelandcarrier.entity.Package;

@Service
@RequiredArgsConstructor
public class PackageService {

  private final PackageMapper packageMapper;
  private final PackageRepository packageRepository;

  public PackageResponse createPackage(PackageRequest request) {
    // Conversion DTO → Entity
    Package entity = packageMapper.toEntity(request);

    Package saved = packageRepository.save(entity);

    // Conversion Entity → DTO
    return packageMapper.toResponse(saved);
  }

  public PackageResponse updatePackage(String id, PackageRequest request) {
    Package entity = packageRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Package", "id", id));

    packageMapper.updateEntityFromDto(request, entity);

    Package updated = packageRepository.save(entity);
    return packageMapper.toResponse(updated);
  }
}
