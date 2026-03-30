SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('project_id_seq', (SELECT MAX(id) FROM project));
SELECT setval('task_id_seq', (SELECT MAX(id) FROM task));
SELECT setval('project_member_id_seq', (SELECT MAX(id) FROM project_member));