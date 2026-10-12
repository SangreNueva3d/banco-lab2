package com.udea.lab1v20262.controller;

import com.udea.lab1v20262.DTO.TransactionDTO;
import com.udea.lab1v20262.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Se llama al controlador directamente (como en el laboratorio) con el servicio simulado
@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private TransactionDTO dto() {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderAccountNumber("ACC-001");
        dto.setReceiverAccountNumber("ACC-002");
        dto.setAmount(100.0);
        return dto;
    }

    @Test
    void transferMoney_exitosa_responde200ConLaTransaccion() {
        TransactionDTO request = dto();
        when(transactionService.transferMoney(request)).thenReturn(request);

        ResponseEntity<?> response = transactionController.transferMoney(request);

        assertEquals(200, response.getStatusCode().value());
        assertSame(request, response.getBody());
    }

    @Test
    void transferMoney_conErrorDeNegocio_responde400ConElMensaje() {
        TransactionDTO request = dto();
        when(transactionService.transferMoney(request))
                .thenThrow(new IllegalArgumentException("Saldo insuficiente"));

        ResponseEntity<?> response = transactionController.transferMoney(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Saldo insuficiente", response.getBody());
    }

    @Test
    void getTransactionsByAccount_delegaEnElServicio() {
        List<TransactionDTO> expected = List.of(dto());
        when(transactionService.getTransactionsForAccount("ACC-001")).thenReturn(expected);

        assertSame(expected, transactionController.getTransactionsByAccount("ACC-001"));
    }

    @Test
    void updateTransaction_responde200() {
        TransactionDTO request = dto();
        when(transactionService.updateTransaction(5L, request)).thenReturn(request);

        ResponseEntity<TransactionDTO> response = transactionController.updateTransaction(5L, request);

        assertEquals(200, response.getStatusCode().value());
        assertSame(request, response.getBody());
    }

    @Test
    void deleteTransaction_responde204() {
        ResponseEntity<Void> response = transactionController.deleteTransaction(5L);

        assertEquals(204, response.getStatusCode().value());
        verify(transactionService).deleteTransaction(5L);
    }
}
