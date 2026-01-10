package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.request.RoleRequest;
import com.hau.ExamInvigilationManagement.dto.response.RoleResponse;
import com.hau.ExamInvigilationManagement.entity.Role;
import com.hau.ExamInvigilationManagement.mapper.RoleMapper;
import com.hau.ExamInvigilationManagement.repository.RoleRepository;
import com.hau.ExamInvigilationManagement.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Tên quyền đã tồn tại!");
        }
        return roleMapper.toRoleResponse(roleRepository.save(roleMapper.toRole(request)));
    }

    @Override
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền!"));
        roleMapper.updateRole(role, request);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @Override
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }
}