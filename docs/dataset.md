## Roles
```sql
SELECT * from role;
-- insert rows
INSERT ALL
    INTO role(id, code, description) values ('role_user', 'role_user', 'Role for user')
    INTO role(id, code, description) values ('role_admin', 'role_admin', 'Role for admin')
    INTO role(id, code, description) values ('role_billops', 'role_billops', 'Role for billing operator')
SELECT 1 FROM dual;
```

## Permissions
```sql
SELECT * from permission;
-- insert permissions
INSERT ALL
    INTO permission(id, code, description, is_disabled) values ('admin:read', 'admin:read', 'Read on admin', 0)
    INTO permission(id, code, description, is_disabled) values ('admin:write', 'admin:write', 'Write on admin', 0)
    INTO permission(id, code, description, is_disabled) values ('customer:read', 'customer:read', 'Read on customer', 0)
    INTO permission(id, code, description, is_disabled) values ('customer:write', 'customer:write', 'Write on customer', 0)
    INTO permission(id, code, description, is_disabled) values ('account:read', 'account:read', 'Read on account', 0)
    INTO permission(id, code, description, is_disabled) values ('account:write', 'account:write', 'Write on account', 0)
    INTO permission(id, code, description, is_disabled) values ('customer_type:read', 'customer_type:read', 'Read on customer type', 0)
    INTO permission(id, code, description, is_disabled) values ('customer_type:write', 'customer_type:write', 'Write on customer type', 0)
SELECT 1 FROM DUAL;
```

## Roles & Permissions Mapping
```sql
SELECT * FROM role_permissions;
-- assign permissions to roles
INSERT ALL
    INTO role_permissions(role_entity_id, permissions_id) values ('role_admin', 'admin:read')
    INTO role_permissions(role_entity_id, permissions_id) values ('role_admin', 'admin:write')
    INTO role_permissions(role_entity_id, permissions_id) values ('role_user', 'customer:read')
    INTO role_permissions(role_entity_id, permissions_id) values ('role_user', 'account:read')
    INTO role_permissions(role_entity_id, permissions_id) values ('role_user', 'customer_type:read')
    INTO role_permissions(role_entity_id, permissions_id) values ('role_billops', 'customer:write')
    INTO role_permissions(role_entity_id, permissions_id) values ('role_billops', 'account:write')
    INTO role_permissions(role_entity_id, permissions_id) values ('role_billops', 'customer_type:write')
SELECT 1 FROM DUAL;
```

## Roles & Roles Mapping
```sql
SELECT * FROM role_roles;
INSERT ALL
    INTO role_roles(role_entity_id, roles_id) values('role_admin', 'role_user')
    INTO role_roles(role_entity_id, roles_id) values('role_admin', 'role_billops')
SELECT 1 FROM DUAL;
```