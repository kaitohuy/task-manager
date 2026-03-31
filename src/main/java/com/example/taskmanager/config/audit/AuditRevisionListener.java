package com.example.taskmanager.config.audit;

import com.example.taskmanager.entity.AuditRevisionEntity;
import com.example.taskmanager.utils.SecurityUtils;
import com.example.taskmanager.utils.RequestUtils;
import org.hibernate.envers.RevisionListener;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevisionEntity rev = (AuditRevisionEntity) revisionEntity;

        String username = SecurityUtils.getCurrentUsername();
        String ip = RequestUtils.getClientIP();

        rev.setUsername(username);
        rev.setIpAddress(ip);
    }
}
