package com.walid.backend.Service;

import com.walid.backend.DAO.CustomerDAO;
import com.walid.backend.Model.Customer;
import com.walid.backend.Model.JwtUtil;

import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

public class CustomerService {
    private CustomerDAO customerDAO = new CustomerDAO();

    // ✅ Create Customer (returns customer object)
    public Customer createCustomer(String username, String email, String phone, String password, String address) throws SQLException {
        Customer existingCustomer = customerDAO.findCustomerByEmail(email);
        
        if (existingCustomer != null) {
            return existingCustomer;  // Return existing customer if found
        }

        Customer newCustomer = new Customer();
        newCustomer.setUsername(username);
        newCustomer.setEmail(email);
        newCustomer.setPhoneNumber(phone);
        newCustomer.setPasswordHash(password);  // Hash this properly
        newCustomer.setAddress(address);

        return customerDAO.createCustomer(newCustomer);
    }

         // ✅ Check if customer exists by email (used for login or setting password)
    public Customer getCustomerByEmail(String email) throws SQLException {
        return customerDAO.findCustomerByEmail(email);
    }

    // ✅ Login Customer (Authenticate & Generate Token)
    public String loginCustomer(String email, String password) throws SQLException {
        Customer customer = customerDAO.findCustomerByEmail(email);
    
        if (customer == null || customer.getPasswordHash() == null) {
            return null; // Customer not found or password not set
        }
    
        // ✅ Fix: Ensure password validation works properly
        boolean passwordMatches = BCrypt.checkpw(password, customer.getPasswordHash());
    
        if (passwordMatches) {
            return JwtUtil.generateToken(email, customer.getCustomerId()); // ✅ Generate JWT token
        }
        
        return null; // Invalid password
    }
    

    // ✅ Set Password for an Existing Customer
    public boolean setPassword(int customerId, String password) throws SQLException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()); // Secure Hash
        return customerDAO.updatePassword(customerId, hashedPassword);
    }

    // ✅ Create a New Customer Account (Standalone)
public Customer registerCustomer(String username, String email, String phone, String password, String address) throws SQLException {
    Customer existingCustomer = customerDAO.findCustomerByEmail(email);
    
    if (existingCustomer != null) {
        return null; // Email already exists
    }

    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());  // ✅ Hash the password

    Customer newCustomer = new Customer();
    newCustomer.setUsername(username);
    newCustomer.setEmail(email);
    newCustomer.setPhoneNumber(phone);
    newCustomer.setPasswordHash(hashedPassword);  // ✅ Store hashed password
    newCustomer.setAddress(address);

    return customerDAO.createCustomer(newCustomer);
}

public Customer getCustomerById(int customerId) throws SQLException {
    return customerDAO.findCustomerById(customerId);
}



}
