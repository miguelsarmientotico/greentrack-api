package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.dto.PagedResponse;
import com.greentrack.greentrack_api.dto.loan.LoanDTO;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<PagedResponse<LoanResponseDTO>> getLoans(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Page<LoanEntity> entitiesPage = loanService.getAllLoans(page - 1, limit);
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
        summary = "${api.loans.filter-loans.summary:Not Configured}",
        description = "${api.loans.filter-loans.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filter")
    public ResponseEntity<List<LoanEntity>> filterLoans(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        
        List<LoanEntity> loans = loanService.filterLoans(employeeId, dateFrom, dateTo);
        if (loans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loans);
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
    public ResponseEntity<LoanResponseDTO> createLoan(@Validated(OnCreate.class) @RequestBody LoanDTO loanDTO) {
        LoanEntity newLoan = loanService.createLoan(loanDTO);
        LoanResponseDTO response = mapper.entityToApiResponse(newLoan);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
    public ResponseEntity<LoanResponseDTO> returnLoan(@PathVariable UUID loanId) {
        try {
            LoanEntity loan = loanService.returnLoan(loanId);
            LoanResponseDTO response = mapper.entityToApiResponse(loan);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<LoanEntity> getLoanById(@PathVariable UUID loanId) {
        return loanService.getLoanById(loanId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
    public ResponseEntity<Void> deleteLoan(@PathVariable UUID loanId) {
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
    public ResponseEntity<Void> deleteLoansBulk(@RequestBody Map<String, List<UUID>> payload) {
        List<UUID> ids = payload.get("ids");
         if (ids != null && !ids.isEmpty()) {
            loanService.deleteLoansBulk(ids);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
