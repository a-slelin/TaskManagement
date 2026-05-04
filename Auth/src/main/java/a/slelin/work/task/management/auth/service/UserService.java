package a.slelin.work.task.management.auth.service;

import a.slelin.work.task.management.auth.entity.Role;
import a.slelin.work.task.management.auth.entity.User;
import a.slelin.work.task.management.auth.exception.AdminActAdminException;
import a.slelin.work.task.management.auth.exception.UserRoleRevokeException;
import a.slelin.work.task.management.auth.mapper.UserMapper;
import a.slelin.work.task.management.auth.repository.RoleRepository;
import a.slelin.work.task.management.auth.repository.UserRepository;
import a.slelin.work.task.management.auth.util.SystemRole;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.auth.RoleCollection;
import a.slelin.work.task.management.core.dto.auth.UserRD;
import a.slelin.work.task.management.core.dto.auth.UserWD;
import a.slelin.work.task.management.core.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.core.exception.EntityNotFoundByPropertyException;
import a.slelin.work.task.management.core.exception.UniqueFieldEntityException;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import a.slelin.work.task.management.core.util.filter.FilterUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public SheetDto<UserRD> getAll(@NotNull @Valid Pageable pageable) {
        return SheetDto.of(userRepository.findAll(pageable), userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public UserRD getById(@NotNull UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserRD getByFactor(@NotBlank String factor) {
        User user = userRepository.findByFactor(factor)
                .orElseThrow(() -> new EntityNotFoundByPropertyException(User.class,
                        "factor (username, phone or email)", factor));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public SheetDto<UserRD> search(@NotNull @Valid FilterChain filters,
                                   @NotNull @Valid Pageable pageable) {
        Specification<User> specification = FilterUtil.toSpecification(filters);
        return SheetDto.of(userRepository.findAll(specification, pageable), userMapper::toDto);
    }

    public UserRD create(@NotNull @Valid UserWD newUser) {

        if (userRepository.existsByUsername(newUser.username())) {
            throw new UniqueFieldEntityException(User.class, "username", newUser.username());
        }

        if (newUser.phone() != null && userRepository.existsByPhone(newUser.phone())) {
            throw new UniqueFieldEntityException(User.class, "phone", newUser.phone());
        }

        if (newUser.email() != null && userRepository.existsByEmail(newUser.email())) {
            throw new UniqueFieldEntityException(User.class, "email", newUser.email());
        }

        User user = userMapper.toEntity(newUser);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserRD patch(@NotNull UUID id,
                        @NotNull UUID actor,
                        @NotNull @Valid UserWD ptcUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        if (user.isAdmin() && !id.equals(actor)) {
            throw new AdminActAdminException(AdminActAdminException.Operation.PATCH);
        }

        if (ptcUser.username() != null && userRepository.existsByUsername(ptcUser.username())) {
            throw new UniqueFieldEntityException(User.class, "username", ptcUser.username());
        }

        if (ptcUser.phone() != null && userRepository.existsByPhone(ptcUser.phone())) {
            throw new UniqueFieldEntityException(User.class, "phone", ptcUser.phone());
        }

        if (ptcUser.email() != null && userRepository.existsByEmail(ptcUser.email())) {
            throw new UniqueFieldEntityException(User.class, "email", ptcUser.email());
        }

        user = userMapper.patch(user, ptcUser);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    public void delete(@NotNull UUID id,
                       @NotNull UUID actor) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        if (user.isAdmin() && !id.equals(actor)) {
            throw new AdminActAdminException(AdminActAdminException.Operation.DELETE);
        }

        userRepository.deleteById(id);
    }

    public void grant(@NotNull UUID user,
                      @NotNull @Valid RoleCollection roles) {
        roles = RoleCollection.transform(roles);

        User entityUser = userRepository.findById(user)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, user));

        for (String role : roles.roles()) {
            Role entityRole = roleRepository.findByName(role)
                    .orElseThrow(() -> new EntityNotFoundByPropertyException(Role.class, "name", role));

            entityUser.addRole(entityRole);
        }

        userRepository.save(entityUser);
    }

    public void revoke(@NotNull UUID user,
                       @NotNull UUID actor,
                       @NotNull @Valid RoleCollection roles) {
        roles = RoleCollection.transform(roles);

        User entityUser = userRepository.findById(user)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, user));

        if (entityUser.isAdmin() && !user.equals(actor)) {
            throw new AdminActAdminException(AdminActAdminException.Operation.REVOKE);
        }

        for (String role : roles.roles()) {
            Role entityRole = roleRepository.findByName(role)
                    .orElseThrow(() -> new EntityNotFoundByPropertyException(Role.class, "name", role));

            if (SystemRole.isUserRole(entityRole)) {
                throw new UserRoleRevokeException();
            }

            entityUser.removeRole(entityRole);
        }

        userRepository.save(entityUser);
    }
}
