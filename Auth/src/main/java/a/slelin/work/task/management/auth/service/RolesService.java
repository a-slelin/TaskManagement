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
import a.slelin.work.task.management.core.exception.UniqueFieldEntityException;
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

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public SheetDto<RoleRD> getRoles(@NotNull @Valid Pageable pageable) {
        return SheetDto.of(roleRepository.findAll(pageable), roleMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public RoleRD getRoleById(@NotNull @Min(1) Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Role.class, id));

        return roleMapper.toDTO(role);
    }

    @Transactional(readOnly = true)
    public RoleRD getRoleByName(@NotBlank @Pattern(regexp = "^ROLE_[A-Z_]+$") String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundByPropertyException(Role.class, "name", name));

        return roleMapper.toDTO(role);
    }

    @Transactional(readOnly = true)
    public SheetDto<RoleRD> searchRoles(@NotNull @Valid FilterChain filters,
                                        @NotNull @Valid Pageable pageable) {
        Specification<Role> specification = FilterUtil.toSpecification(filters);
        return SheetDto.of(roleRepository.findAll(specification, pageable), roleMapper::toDTO);
    }

    public RoleRD createRole(@NotNull @Valid RoleWD newRole) {

        if (roleRepository.existsByName(newRole.name())) {
            throw new UniqueFieldEntityException(Role.class, "name", newRole.name());
        }

        Role role = roleMapper.toEntity(newRole);
        role = roleRepository.save(role);
        return roleMapper.toDTO(role);
    }

    public RoleRD updateRole(@NotNull @Min(1) Long id,
                             @NotNull @Valid RoleWD updRole) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Role.class, id));

        UpdateNameSystemRoleException.checkAndThrow(role);

        if (roleRepository.existsByName(updRole.name())) {
            throw new UniqueFieldEntityException(Role.class, "name", updRole.name());
        }

        Role updatedRole = roleMapper.toEntity(updRole);
        updatedRole.setId(id);
        updatedRole.setUsers(role.getUsers());
        updatedRole = roleRepository.save(updatedRole);

        return roleMapper.toDTO(updatedRole);
    }

    public RoleRD patchRole(@NotNull @Min(1) Long id,
                            @NotNull @Valid RoleWD ptcRole) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Role.class, id));

        if (ptcRole.name() != null) {
            UpdateNameSystemRoleException.checkAndThrow(role);

            if (roleRepository.existsByName(ptcRole.name())) {
                throw new UniqueFieldEntityException(Role.class, "name", ptcRole.name());
            }
        }

        role = roleMapper.patch(role, ptcRole);
        role = roleRepository.save(role);

        return roleMapper.toDTO(role);
    }

    public void deleteRole(@NotNull @Min(1) Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Role.class, id));

        DeleteSystemRoleException.checkAndThrow(role);

        roleRepository.deleteById(id);
    }
}
