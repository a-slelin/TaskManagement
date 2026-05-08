package a.slelin.work.task.management.auth.controller.rest;

import a.slelin.work.task.management.auth.service.RefreshTokenService;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.auth.RefreshTokenRD;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/tokens",
        consumes = {"application/json", "application/xml", "application/yaml"},
        produces = {"application/json", "application/xml", "application/yaml"})
public class RefreshTokenController {

    private final RefreshTokenService service;

    @GetMapping(consumes = "*/*")
    public SheetDto<RefreshTokenRD> getAllRefreshTokens(@PageableDefault(sort = "token") Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping(path = "/{token}", consumes = "*/*")
    public RefreshTokenRD getRefreshToken(@PathVariable UUID token) {
        return service.getById(token);
    }

    @GetMapping(path = "/user/{user}", consumes = "*/*")
    public SheetDto<RefreshTokenRD> getUserRefreshTokens(@PathVariable UUID user,
                                                         @PageableDefault(sort = "token") Pageable pageable) {
        return service.getByUser(user, pageable);
    }

    @PostMapping(path = {"/search", "/filter"})
    public SheetDto<RefreshTokenRD> getRefreshTokensByFilter(@RequestBody FilterChain filters,
                                                             @PageableDefault(sort = "token") Pageable pageable) {
        return service.getByFilter(filters, pageable);
    }

    @DeleteMapping(path = "/{token}", consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> deleteRefreshToken(@PathVariable UUID token) {
        service.deleteById(token);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/user/{user}", consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> deleteUserRefreshTokens(@PathVariable UUID user) {
        service.deleteByUser(user);
        return ResponseEntity.noContent().build();
    }
}
