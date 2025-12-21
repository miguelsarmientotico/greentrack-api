package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.dto.PagedResponse;
import com.greentrack.greentrack_api.dto.loan.LoanDTO;
import com.greentrack.greentrack_api.dto.loan.LoanResponseDTO;
import com.greentrack.greentrack_api.entity.LoanEntity;
import com.greentrack.greentrack_api.mapper.LoanMapper;
import com.greentrack.greentrack_api.service.LoanService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/loans")
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

    // Endpoint para CREAR un préstamo
    @PostMapping
    public ResponseEntity<LoanResponseDTO> createLoan(@Validated(OnCreate.class) @RequestBody LoanDTO loanDTO) {
        LoanEntity newLoan = loanService.createLoan(loanDTO);
        LoanResponseDTO response = mapper.entityToApiResponse(newLoan);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint específico para DEVOLVER (Check-in) un equipo
    @PatchMapping("/{loanId}/return")
    public ResponseEntity<LoanEntity> returnLoan(@PathVariable UUID loanId) {
        try {
            LoanEntity loan = loanService.returnLoan(loanId);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanEntity> getLoanById(@PathVariable UUID loanId) {
        return loanService.getLoanById(loanId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{loanId}")
    public ResponseEntity<Void> deleteLoan(@PathVariable UUID loanId) {
        loanService.deleteLoan(loanId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> deleteLoansBulk(@RequestBody Map<String, List<UUID>> payload) {
        List<UUID> ids = payload.get("ids");
         if (ids != null && !ids.isEmpty()) {
            loanService.deleteLoansBulk(ids);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    // DTO para la creación
    public record CreateLoanRequest(UUID employeeId, UUID deviceId) {}
}
