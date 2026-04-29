package a.slelin.work.task.management.auth.service;

import a.slelin.work.task.management.auth.entity.Role;
import a.slelin.work.task.management.auth.exception.DeleteSystemRoleException;
import a.slelin.work.task.management.auth.exception.UpdateNameSystemRoleException;
import a.slelin.work.task.management.auth.mapper.RoleMapper;
import a.slelin.work.task.management.auth.repository.RoleRepository;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.auth.RoleRD;
import a.slelin.work.task.management.core.dto.auth.RoleWD;
import a.slelin.work.task.management.core.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.core.exception.EntityNotFoundByPropertyException;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import a.slelin.work.task.management.core.util.filter.FilterUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class RolesService {

    private final RoleRepository repo;

    private final RoleMapper mapper;

    @Transactional(readOnly = true)
    public SheetDto<RoleRD> getRoles(@NotNull @Valid Pageable pageable) {
        return SheetDto.of(repo.findAll(pageable), mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public RoleRD getRoleById(@NotNull @Min(1) Long id) {
        Role role = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Role.class, id));

        return mapper.toDTO(role);
    }

    @Transactional(readOnly = true)
    public RoleRD getRoleByName(@NotBlank @Pattern(regexp = "^ROLE_[A-Z_]+$") String name) {
        Role role = repo.findByName(name)
                .orElseThrow(() -> new EntityNotFoundByPropertyException(Role.class, "name", name));

        return mapper.toDTO(role);
    }

    @Transactional(readOnly = true)
    public SheetDto<RoleRD> searchRoles(@NotNull @Valid FilterChain filters,
                                        @NotNull @Valid Pageable pageable) {
        Specification<Role> specification = FilterUtil.toSpecification(filters);
        return SheetDto.of(repo.findAll(specification, pageable), mapper::toDTO);
    }

    public RoleRD createRole(@NotNull @Valid RoleWD newRole) {
        Role role = mapper.toEntity(newRole);
        role = repo.save(role);
        return mapper.toDTO(role);
    }

    public RoleRD updateRole(@NotNull @Min(1) Long id,
                             @NotNull @Valid RoleWD updRole) {
        Role role = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Role.class, id));

        UpdateNameSystemRoleException.checkAndThrow(role);

        Role updatedRole = mapper.toEntity(updRole);
        updatedRole.setId(id);
        updatedRole.setUsers(role.getUsers());
        updatedRole = repo.save(updatedRole);

        return mapper.toDTO(updatedRole);
    }

    public RoleRD patchRole(@NotNull @Min(1) Long id,
                            @NotNull @Valid RoleWD ptcRole) {
        Role role = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Role.class, id));

        if (ptcRole.name() != null) {
            UpdateNameSystemRoleException.checkAndThrow(role);
        }

        role = mapper.patch(role, ptcRole);
        role = repo.save(role);

        return mapper.toDTO(role);
    }

    public void deleteRole(@NotNull @Min(1) Long id) {
        Role role = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Role.class, id));

        DeleteSystemRoleException.checkAndThrow(role);

        repo.deleteById(id);
    }
}
