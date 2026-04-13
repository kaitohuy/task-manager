package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    
    @Query(value = "SELECT p.name FROM permissions p " +
                   "JOIN role_permissions rp ON p.id = rp.permission_id " +
                   "WHERE rp.role_name IN :roleNames", nativeQuery = true)
    Set<String> findNamesByRoleNames(@Param("roleNames") List<String> roleNames);
}
