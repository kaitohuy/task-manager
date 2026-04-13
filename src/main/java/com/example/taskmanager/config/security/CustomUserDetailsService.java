package com.example.taskmanager.config.security;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.PermissionRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        User user = userRepository.findByIdentifier(input).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        List<String> roleNames = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        
        Set<String> permissions = permissionRepository.findNamesByRoleNames(roleNames);
        
        // Apply individual overrides (Allow/Deny)
        if (user.getPermissionOverrides() != null) {
            for (com.example.taskmanager.entity.UserPermission override : user.getPermissionOverrides()) {
                String permName = override.getPermission().getName();
                if (override.isDenied()) {
                    permissions.remove(permName);
                } else {
                    permissions.add(permName);
                }
            }
        }
        
        return new CustomUserDetails(user, permissions);
    }
}
