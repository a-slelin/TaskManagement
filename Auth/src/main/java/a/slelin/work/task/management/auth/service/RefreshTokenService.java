package a.slelin.work.task.management.auth.service;

import a.slelin.work.task.management.auth.entity.RefreshToken;
import a.slelin.work.task.management.auth.entity.User;
import a.slelin.work.task.management.auth.exception.AdminActAdminException;
import a.slelin.work.task.management.auth.mapper.RefreshTokenMapper;
import a.slelin.work.task.management.auth.repository.RefreshTokenRepository;
import a.slelin.work.task.management.auth.repository.UserRepository;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.auth.RefreshTokenRD;
import a.slelin.work.task.management.core.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import a.slelin.work.task.management.core.util.filter.FilterUtil;
import jakarta.validation.Valid;
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
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    private final RefreshTokenMapper mapper;

    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public SheetDto<RefreshTokenRD> getAll(@NotNull @Valid Pageable pageable) {
        return SheetDto.of(repo.findAll(pageable), mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public RefreshTokenRD getById(@NotNull @Valid UUID id) {
        RefreshToken token = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(RefreshToken.class, id));

        return mapper.toDTO(token);
    }

    @Transactional(readOnly = true)
    public SheetDto<RefreshTokenRD> getByUser(@NotNull @Valid UUID user,
                                              @NotNull @Valid Pageable pageable) {
        if (!userRepo.existsById(user)) {
            throw new EntityNotFoundByIdException(User.class, user);
        }

        return SheetDto.of(repo.findAllByUserId(pageable, user), mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public SheetDto<RefreshTokenRD> getByFilter(@NotNull @Valid FilterChain filters,
                                                @NotNull @Valid Pageable pageable) {
        Specification<RefreshToken> specification = FilterUtil.toSpecification(filters);
        return SheetDto.of(repo.findAll(specification, pageable), mapper::toDTO);
    }

    public void deleteById(@NotNull @Valid UUID id) {
        RefreshToken token = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(RefreshToken.class, id));

        if (token.getUser().isAdmin()) {
            throw new AdminActAdminException(AdminActAdminException.Operation.TERMINATE_SESSION);
        }

        repo.deleteById(id);
    }

    public void deleteByUser(@NotNull @Valid UUID user) {
        User userEntity = userRepo.findById(user)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, user));

        if (userEntity.isAdmin()) {
            throw new AdminActAdminException(AdminActAdminException.Operation.TERMINATE_SESSION);
        }

        repo.deleteByUser(userEntity);
    }
}
