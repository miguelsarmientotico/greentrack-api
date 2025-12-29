package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.dto.PagedResponse;
import com.greentrack.greentrack_api.dto.loan.LoanDTO;
import com.greentrack.greentrack_api.dto.loan.LoanFilterDTO;
import com.greentrack.greentrack_api.dto.loan.LoanResponseDTO;
import com.greentrack.greentrack_api.entity.LoanEntity;
import com.greentrack.greentrack_api.mapper.LoanMapper;
import com.greentrack.greentrack_api.service.LoanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/loans")
@Tag(name = "Loans", description = "${api.loans.tag.description:Not Configured}")
public class LoanController {

    private static final Logger LOG = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;
    private final LoanMapper mapper;

    public LoanController(
        LoanService loanService,
        LoanMapper mapper
    ) {
        this.loanService = loanService;
        this.mapper = mapper;
    }

    @Operation(
        summary = "${api.loans.get-loans.summary:Not Configured}",
        description = "${api.loans.get-loans.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PagedResponse<LoanResponseDTO>> getAllLoans(
        @ModelAttribute LoanFilterDTO filter,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC)
        Pageable pageable
    ) {
        Page<LoanEntity> entitiesPage = loanService.searchLoansAdvanced(filter, pageable);
        Page<LoanResponseDTO> dtoPage = entitiesPage.map(mapper::entityToApiResponse);
        PagedResponse<LoanResponseDTO> response = new PagedResponse<>(
            dtoPage.getContent(),
            dtoPage.getNumber() + 1,
            dtoPage.getSize(),
            dtoPage.getTotalElements(),
            dtoPage.getTotalPages(),
            dtoPage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "${api.loans.get-loan-by-id.summary:Not Configured}",
        description = "${api.loans.get-loan-by-id.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponseDTO> getLoanById(@PathVariable UUID loanId) {
        LOG.debug("Consultando detalle de préstamo ID: {}", loanId);
        LoanEntity loan = loanService.getLoanById(loanId);
        return ResponseEntity.ok(mapper.entityToApiResponse(loan));
    }

    @Operation(
        summary = "${api.loans.create-loan.summary:Not Configured}",
        description = "${api.loans.create-loan.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.responseCodes.created.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}"),
        @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<LoanResponseDTO> createLoan(
        @Validated(OnCreate.class) @RequestBody LoanDTO loanDTO,
        Authentication authentication
    ) {
        LOG.info(
            "🆕 Usuario Admin '{}' iniciando asignación de préstamo. Empleado Destino: {}, Dispositivo: {}", 
            authentication.getName(),
            loanDTO.getEmployeeId(),
            loanDTO.getDeviceId()
        );
        LoanEntity newLoan = loanService.createLoan(loanDTO);
        LOG.debug("Préstamo registrado correctamente con ID: {}", newLoan.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.entityToApiResponse(newLoan));
    }

    @Operation(
        summary = "${api.loans.return-loan.summary:Not Configured}",
        description = "${api.loans.return-loan.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{loanId}/return")
    public ResponseEntity<LoanResponseDTO> returnLoan(
        @PathVariable UUID loanId,
        Authentication authentication
    ) {
        LOG.info("🔄 Usuario Admin '{}' procesando DEVOLUCIÓN de préstamo ID: {}", authentication.getName(), loanId);
        LoanEntity loan = loanService.returnLoan(loanId);
        LOG.info("✅ Devolución confirmada. El dispositivo ahora está DISPONIBLE.");
        return ResponseEntity.ok(mapper.entityToApiResponse(loan));
    }

    @Operation(
        summary = "${api.loans.delete-loan.summary:Not Configured}",
        description = "${api.loans.delete-loan.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.responseCodes.noContent.description:Not Configured}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{loanId}")
    public ResponseEntity<Void> deleteLoan(
        @PathVariable UUID loanId,
        Authentication authentication
    ) {
        LOG.warn("⚠️ Eliminación de registro de préstamo ID: {} solicitada por '{}'", loanId, authentication.getName());
        loanService.deleteLoan(loanId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "${api.loans.bulk-delete.summary:Not Configured}",
        description = "${api.loans.bulk-delete.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> deleteLoansBulk(
        @RequestBody Map<String, List<UUID>> payload,
        Authentication authentication
    ) {
        List<UUID> ids = payload.get("ids");
        int count = (ids != null) ? ids.size() : 0;
        LOG.warn("⚠️ ELIMINACIÓN MASIVA de {} préstamos iniciada por '{}'", count, authentication.getName());
        loanService.deleteLoansBulk(ids);
        return ResponseEntity.ok().build();
    }
}
