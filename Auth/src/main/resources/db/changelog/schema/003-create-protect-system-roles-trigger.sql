--liquibase formatted sql

--changeset a.slelin:003-create-protect-system-roles-trigger context:!test

CREATE
    OR REPLACE FUNCTION protect_system_roles()
    RETURNS TRIGGER AS
'
    BEGIN
        IF TG_OP = ''DELETE'' THEN
            IF OLD.name IN (''ROLE_USER'', ''ROLE_ADMIN'') THEN
                RAISE EXCEPTION ''Cannot delete system role: %'', OLD.name;
            END IF;
            RETURN OLD;
        END IF;
        IF TG_OP = ''UPDATE'' THEN
            IF OLD.name IN (''ROLE_USER'', ''ROLE_ADMIN'') AND NEW.name <> OLD.name THEN
                RAISE EXCEPTION ''Cannot change name of system role: %'', OLD.name;
            END IF;
            RETURN NEW;
        END IF;

        RETURN NULL;
    END; ' LANGUAGE plpgsql;

CREATE TRIGGER trg_protect_system_roles
    BEFORE DELETE OR
        UPDATE OF name
    ON roles
    FOR EACH ROW
EXECUTE FUNCTION protect_system_roles();

--rollback DROP TRIGGER IF EXISTS trg_protect_system_roles ON roles;
--rollback DROP FUNCTION IF EXISTS protect_system_roles;