package com.example.taskmanager.config.audit;

import com.example.taskmanager.entity.AuditRevisionEntity;
import com.example.taskmanager.utils.RequestUtils;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevisionEntity rev = (AuditRevisionEntity) revisionEntity;

        // This listener is used during Hibernate bootstrap; keep it free of Spring bean dependencies
        // to avoid circular references (e.g., through repositories/EntityManagerFactory).
        String username = getCurrentUsername();
        String ip = RequestUtils.getClientIP();

        rev.setUsername(username);
        rev.setIpAddress(ip);
    }

    private static String getCurrentUsername() {
        var context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            return "anonymous";
        }
        return context.getAuthentication().getName();
    }
}
