package a.slelin.work.task.management.auth.controller.rest;

import a.slelin.work.task.management.auth.service.RolesService;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.auth.RoleRD;
import a.slelin.work.task.management.core.dto.auth.RoleWD;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(
        value = "/api/admin/roles",
        consumes = {"application/json", "application/xml", "application/yaml"},
        produces = {"application/json", "application/xml", "application/yaml"}
)
@RequiredArgsConstructor
public class RolesController {

    private final RolesService service;

    @GetMapping(consumes = "*/*")
    public SheetDto<RoleRD> getRoles(@PageableDefault(sort = "name") Pageable pageable) {
        return service.getRoles(pageable);
    }

    @GetMapping(value = "/{role}", consumes = "*/*")
    public RoleRD getRoleById(@PathVariable Long role) {
        return service.getRoleById(role);
    }

    @GetMapping(value = "/name/{role}", consumes = "*/*")
    public RoleRD getRoleByName(@PathVariable String role) {
        return service.getRoleByName(role);
    }

    @PostMapping("/search")
    public SheetDto<RoleRD> searchRoles(@RequestBody FilterChain filters,
                                        @PageableDefault(sort = "name") Pageable pageable) {
        return service.searchRoles(filters, pageable);
    }

    @PostMapping
    public ResponseEntity<RoleRD> createRole(@RequestBody RoleWD role) {
        RoleRD savedRole = service.createRole(role);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .pathSegment(savedRole.id().toString())
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body(savedRole);
    }

    @PutMapping("/{role}")
    public RoleRD updateRole(@PathVariable Long role,
                             @RequestBody RoleWD updRole) {
        return service.updateRole(role, updRole);
    }

    @PatchMapping("/{role}")
    public RoleRD patchRole(@PathVariable Long role,
                            @RequestBody RoleWD pthRole) {
        return service.patchRole(role, pthRole);
    }

    @DeleteMapping(value = "/{role}", consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> deleteRole(@PathVariable Long role) {
        service.deleteRole(role);
        return ResponseEntity.noContent().build();
    }
}
