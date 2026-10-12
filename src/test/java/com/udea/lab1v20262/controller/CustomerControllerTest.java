package com.udea.lab1v20262.controller;

import com.udea.lab1v20262.DTO.CustomerDTO;
import com.udea.lab1v20262.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private CustomerDTO dto(Double balance) {
        return new CustomerDTO(1L, "Ana", "Gomez", "ACC-001", balance);
    }

    @Test
    void getAllCustomers_responde200ConLaLista() {
        List<CustomerDTO> customers = List.of(dto(1000.0));
        when(customerService.getAllCustomers()).thenReturn(customers);

        ResponseEntity<List<CustomerDTO>> response = customerController.getAllCustomers();

        assertEquals(200, response.getStatusCode().value());
        assertSame(customers, response.getBody());
    }

    @Test
    void getCustomerById_responde200() {
        CustomerDTO customer = dto(1000.0);
        when(customerService.getCustomerById(1L)).thenReturn(customer);

        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertSame(customer, response.getBody());
    }

    @Test
    void createCustomer_conSaldo_responde200() {
        CustomerDTO customer = dto(1000.0);
        when(customerService.createCustomer(customer)).thenReturn(customer);

        ResponseEntity<CustomerDTO> response = customerController.createCustomer(customer);

        assertEquals(200, response.getStatusCode().value());
        assertSame(customer, response.getBody());
    }

    @Test
    void createCustomer_sinSaldo_lanzaExcepcionYNoLlamaAlServicio() {
        CustomerDTO sinSaldo = dto(null);

        assertThrows(IllegalArgumentException.class, () -> customerController.createCustomer(sinSaldo));
        verifyNoInteractions(customerService);
    }

    @Test
    void updateCustomer_responde200() {
        CustomerDTO customer = dto(500.0);
        when(customerService.updateCustomer(1L, customer)).thenReturn(customer);

        ResponseEntity<CustomerDTO> response = customerController.updateCustomer(1L, customer);

        assertEquals(200, response.getStatusCode().value());
        assertSame(customer, response.getBody());
    }

    @Test
    void deleteCustomer_responde204() {
        ResponseEntity<Void> response = customerController.deleteCustomer(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(customerService).deleteCustomer(1L);
    }
}
