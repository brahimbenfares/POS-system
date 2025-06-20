package com.walid.backend.Service;

import com.walid.backend.DAO.WholesaleCustomerDAO;
import com.walid.backend.Model.WholesaleCustomer;
import java.sql.SQLException;
import java.util.List;

public class WholesaleCustomerService {
    private final WholesaleCustomerDAO customerDAO;

    // Constructor to inject the DAO
    public WholesaleCustomerService(WholesaleCustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    // Add a new wholesale customer
    public void addCustomer(WholesaleCustomer customer) throws SQLException {
        customerDAO.saveCustomer(customer);
    }

    // Retrieve all customers
    public List<WholesaleCustomer> getAllCustomers() throws SQLException {
        return customerDAO.getAllCustomers();
    }

    // Update a wholesale customer
    public void updateCustomer(WholesaleCustomer customer) throws SQLException {
        customerDAO.updateCustomer(customer);
    }

    // Delete a customer by ID
    public void deleteCustomer(int customerId) throws SQLException {
        customerDAO.deleteCustomer(customerId);
    }
}
