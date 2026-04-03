package com.example.taskmanager.config.audit;

import com.example.taskmanager.config.security.SecurityService;
import com.example.taskmanager.entity.AuditRevisionEntity;
import com.example.taskmanager.utils.RequestUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.RevisionListener;

@RequiredArgsConstructor
public class AuditRevisionListener implements RevisionListener {

    private final SecurityService securityService;

    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevisionEntity rev = (AuditRevisionEntity) revisionEntity;

        String username = securityService.getCurrentUsername();
        String ip = RequestUtils.getClientIP();

        rev.setUsername(username);
        rev.setIpAddress(ip);
    }
}
