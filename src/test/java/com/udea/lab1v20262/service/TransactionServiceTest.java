package com.udea.lab1v20262.service;

import com.udea.lab1v20262.DTO.TransactionDTO;
import com.udea.lab1v20262.entity.Customer;
import com.udea.lab1v20262.entity.Transaction;
import com.udea.lab1v20262.repository.CustomerRepository;
import com.udea.lab1v20262.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

// Prueba unitaria pura: los repositorios son "mocks", así no se necesita MySQL
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    // Mockito crea el servicio e inyecta los dos mocks de arriba
    @InjectMocks
    private TransactionService transactionService;

    private Customer sender;
    private Customer receiver;

    @BeforeEach
    void setUp() {
        sender = new Customer(1L, "ACC-001", "Ana", "Gomez", 1000.0);
        receiver = new Customer(2L, "ACC-002", "Luis", "Perez", 500.0);
    }

    private TransactionDTO request(String from, String to, Double amount) {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderAccountNumber(from);
        dto.setReceiverAccountNumber(to);
        dto.setAmount(amount);
        return dto;
    }

    @Test
    void transferMoney_actualizaSaldosYGuardaLaTransaccion() {
        when(customerRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumber("ACC-002")).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TransactionDTO result = transactionService.transferMoney(request("ACC-001", "ACC-002", 200.0));

        assertEquals(800.0, sender.getBalance(), 0.001);
        assertEquals(700.0, receiver.getBalance(), 0.001);
        assertEquals(Long.valueOf(10L), result.getId());
        assertEquals(200.0, result.getAmount(), 0.001);
        verify(customerRepository).save(sender);
        verify(customerRepository).save(receiver);
    }

    @Test
    void transferMoney_conCuentaNula_lanzaExcepcionSinTocarLaBD() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transferMoney(request(null, "ACC-002", 100.0)));

        assertTrue(ex.getMessage().contains("obligatorios"));
        verifyNoInteractions(customerRepository, transactionRepository);
    }

    @Test
    void transferMoney_remitenteNoExiste_lanzaExcepcion() {
        when(customerRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transferMoney(request("ACC-001", "ACC-002", 100.0)));

        assertTrue(ex.getMessage().contains("remitente no existe"));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transferMoney_receptorNoExiste_lanzaExcepcion() {
        when(customerRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumber("ACC-002")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transferMoney(request("ACC-001", "ACC-002", 100.0)));

        assertTrue(ex.getMessage().contains("receptor no existe"));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transferMoney_saldoInsuficiente_noModificaNada() {
        when(customerRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumber("ACC-002")).thenReturn(Optional.of(receiver));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transferMoney(request("ACC-001", "ACC-002", 5000.0)));

        assertTrue(ex.getMessage().contains("Saldo insuficiente"));
        assertEquals(1000.0, sender.getBalance(), 0.001);
        assertEquals(500.0, receiver.getBalance(), 0.001);
        verify(customerRepository, never()).save(any(Customer.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void getTransactionsForAccount_devuelveLasTransaccionesComoDTO() {
        Transaction t = new Transaction(5L, "ACC-001", "ACC-002", 100.0, LocalDateTime.of(2026, 10, 1, 10, 0));
        when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber("ACC-001", "ACC-001"))
                .thenReturn(List.of(t));

        List<TransactionDTO> result = transactionService.getTransactionsForAccount("ACC-001");

        assertEquals(1, result.size());
        assertEquals(Long.valueOf(5L), result.get(0).getId());
        assertEquals("ACC-002", result.get(0).getReceiverAccountNumber());
        assertEquals(100.0, result.get(0).getAmount(), 0.001);
    }

    @Test
    void updateTransaction_existente_actualizaLosCampos() {
        Transaction existing = new Transaction(7L, "ACC-001", "ACC-002", 10.0, LocalDateTime.now());
        when(transactionRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionDTO result = transactionService.updateTransaction(7L, request("ACC-003", "ACC-004", 99.0));

        assertEquals("ACC-003", result.getSenderAccountNumber());
        assertEquals("ACC-004", result.getReceiverAccountNumber());
        assertEquals(99.0, result.getAmount(), 0.001);
    }

    @Test
    void updateTransaction_noExiste_lanzaExcepcion() {
        when(transactionRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> transactionService.updateTransaction(7L, request("ACC-001", "ACC-002", 10.0)));
    }

    @Test
    void deleteTransaction_existente_laElimina() {
        when(transactionRepository.existsById(7L)).thenReturn(true);

        transactionService.deleteTransaction(7L);

        verify(transactionRepository).deleteById(7L);
    }

    @Test
    void deleteTransaction_noExiste_lanzaExcepcion() {
        when(transactionRepository.existsById(7L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> transactionService.deleteTransaction(7L));
        verify(transactionRepository, never()).deleteById(anyLong());
    }
}
