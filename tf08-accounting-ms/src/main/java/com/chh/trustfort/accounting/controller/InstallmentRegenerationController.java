//package com.chh.trustfort.accounting.controller;
//
//import com.chh.trustfort.accounting.constant.ApiPath;
//import com.chh.trustfort.accounting.dto.ApiResponse;
//import com.chh.trustfort.accounting.model.PayableInvoice;
//import com.chh.trustfort.accounting.model.PaymentInstallment;
//import com.chh.trustfort.accounting.service.InstallmentService;
//import com.chh.trustfort.accounting.service.PayableInvoiceService;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping(ApiPath.BASE_API)
//@RequiredArgsConstructor
//@Slf4j
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Installment Management", description = "Manage invoice payment installments")
//public class InstallmentRegenerationController {
//
//    private final InstallmentService installmentService;
//    private final PayableInvoiceService payableInvoiceService;
//
//    @PostMapping(ApiPath.REGENERATE_INSTALLMENTS)
//    public ResponseEntity<?> regenerateInstallments(@RequestParam Long invoiceId, @RequestParam int numberOfInstallments) {
//        try {
//            PayableInvoice invoice = payableInvoiceService.getInvoiceById(invoiceId);
//            if (invoice == null) {
//                return ResponseEntity.badRequest().body(
//                        ApiResponse.error("Invoice not found with ID: " + invoiceId));
//            }
//
//            List<PaymentInstallment> existing = installmentService.getInstallmentsByInvoice(invoiceId);
//            if (!existing.isEmpty()) {
//                installmentService.deleteInstallmentsByInvoice(invoiceId);
//                log.warn("Existing installments deleted for invoice: {}", invoice.getInvoiceNumber());
//            }
//
//            installmentService.generateInstallments(invoiceId, numberOfInstallments);
//            return ResponseEntity.ok(
//                    Map.of(
//                            "status", "success",
//                            "message", "Installments regenerated successfully.",
//                            "invoiceId", invoiceId,
//                            "installments", numberOfInstallments,
//                            "invoiceNumber", invoice.getInvoiceNumber(),
//                            "vendorName", invoice.getVendorName(),
//                            "amount", invoice.getAmount(),
//                            "dueDate", invoice.getDueDate()
//                    )
//            );
//        } catch (Exception e) {
//            log.error("Failed to regenerate installments", e);
//            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to regenerate installments"));
//        }
//    }
//}
