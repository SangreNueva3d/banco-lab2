package com.udea.lab1v20262.service;

import com.udea.lab1v20262.DTO.CustomerDTO;
import com.udea.lab1v20262.entity.Customer;
import com.udea.lab1v20262.mapper.CustomerMapper;
import com.udea.lab1v20262.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// El repositorio y el mapper son mocks: se prueba solo la lógica del servicio
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    // El servicio usa inyección por constructor; Mockito le pasa los dos mocks
    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO dto;

    @BeforeEach
    void setUp() {
        customer = new Customer(1L, "ACC-001", "Ana", "Gomez", 1000.0);
        dto = new CustomerDTO(1L, "Ana", "Gomez", "ACC-001", 1000.0);
    }

    @Test
    void getAllCustomers_devuelveLaListaComoDTO() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerMapper.toDTO(customer)).thenReturn(dto);

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
    }

    @Test
    void getCustomerById_existente_devuelveElDTO() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(customer)).thenReturn(dto);

        assertSame(dto, customerService.getCustomerById(1L));
    }

    @Test
    void getCustomerById_noExiste_lanzaExcepcion() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.getCustomerById(99L));

        assertEquals("Cliente no encontrado", ex.getMessage());
    }

    @Test
    void createCustomer_guardaYDevuelveElDTO() {
        when(customerMapper.toEntity(dto)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDTO(customer)).thenReturn(dto);

        assertSame(dto, customerService.createCustomer(dto));
        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomer_existente_actualizaLosCampos() {
        CustomerDTO changes = new CustomerDTO(null, "Maria", "Lopez", "ACC-009", 250.0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDTO(customer)).thenReturn(dto);

        customerService.updateCustomer(1L, changes);

        assertEquals("Maria", customer.getFirstName());
        assertEquals("Lopez", customer.getLastName());
        assertEquals("ACC-009", customer.getAccountNumber());
        assertEquals(250.0, customer.getBalance(), 0.001);
    }

    @Test
    void updateCustomer_noExiste_lanzaExcepcion() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> customerService.updateCustomer(99L, dto));
    }

    @Test
    void deleteCustomer_existente_loElimina() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        customerService.deleteCustomer(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteCustomer_noExiste_lanzaExcepcion() {
        when(customerRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> customerService.deleteCustomer(99L));
        verify(customerRepository, never()).deleteById(anyLong());
    }
}
