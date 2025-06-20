/*package com.walid.backend;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walid.backend.Controller.ProductController;
import com.walid.backend.Controller.OrderController;
import com.walid.backend.Controller.AdminController;
import com.walid.backend.DAO.AdminDAO;
import com.walid.backend.DAO.OrderDAO;
import com.walid.backend.DAO.ProductDAO;
import com.walid.backend.Service.AdminService;
import com.walid.backend.Service.OrderService;
import com.walid.backend.Service.ProductService;
import com.walid.backend.Controller.SupplierController;
import com.walid.backend.Controller.WholesaleCustomerController;
import com.walid.backend.Service.SupplierService;
import com.walid.backend.Service.WholesaleCustomerService;
import com.walid.backend.DAO.SupplierDAO;
import com.walid.backend.DAO.WholesaleCustomerDAO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class App1 {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        // Initialize DAOs and Services
        ProductDAO productDAO = new ProductDAO();
        AdminDAO adminDAO = new AdminDAO();
        OrderDAO orderDAO = new OrderDAO();

        ProductService productService = new ProductService(productDAO);
        OrderService orderService = new OrderService(orderDAO);
        AdminService adminService = new AdminService(adminDAO);

        // Initialize Controllers
        ProductController productController = new ProductController(productService, adminService);
        OrderController orderController = new OrderController(orderService);
        AdminController adminController = new AdminController(adminService);

        SupplierDAO supplierDAO = new SupplierDAO();
        WholesaleCustomerDAO customerDAO = new WholesaleCustomerDAO();

        SupplierService supplierService = new SupplierService(supplierDAO);
        WholesaleCustomerService customerService = new WholesaleCustomerService(customerDAO);

        SupplierController supplierController = new SupplierController(supplierService);
        WholesaleCustomerController customerController = new WholesaleCustomerController(customerService);

        // Start Javalin server and configure routes
        Javalin app = startJavalin();
        configureRoutes(app, productController, orderController, adminController, supplierController, customerController);
    }

    private static Javalin startJavalin() {
        return Javalin.create(config -> {
            config.jetty.server(() -> {
                Server server = new Server(new QueuedThreadPool());
                ServerConnector connector = new ServerConnector(server);
                connector.setPort(8080);
                server.addConnector(connector);
                return server;
            });

            // Configure static files
            String projectRoot = System.getProperty("user.dir");
            Path frontendPath = Paths.get(projectRoot, "walid-backend", "src", "main", "frontend").normalize();
            if (Files.exists(frontendPath)) {
                config.staticFiles.add(frontendPath.toString(), Location.EXTERNAL);
            } else {
                System.err.println("Frontend path not found: " + frontendPath.toAbsolutePath());
            }

        }).start(8080);
    }

    private static void configureRoutes(Javalin app,
                                        ProductController productController,
                                        OrderController orderController,
                                        AdminController adminController,
                                        SupplierController supplierController,
                                        WholesaleCustomerController customerController) {

        System.out.println("Configuring API routes...");

        setupCORS(app);
        setupExceptionHandling(app);

        // Product-related routes
        app.post("/api/addproduct", productController::addProductHandler);
        app.get("/api/products", productController::getAllProductsHandler);
        app.get("/api/products/{productId}", productController::getProductByIdHandler);
        app.put("/api/products/{productId}", productController::updateProductHandler);
        app.delete("/api/products/{productId}", productController::deleteProductHandler);
        app.get("/api/products-with-promotions", productController::getAllProductsWithPromotionsHandler);

        // Order-related routes

        // Specific routes first
        app.get("/api/orders/confirm-orders", orderController::getConfirmedOrdersByDateRangeHandler);
        app.put("/api/orders/confirm/{orderId}", orderController::confirmOrderHandler);

        // In App.java, inside configureRoutes method
        app.get("/api/orders/minimal-details", orderController::getOrdersWithMinimalDetailsHandler);


        // General routes after
        app.get("/api/orders/{orderId}", orderController::getOrderByIdHandler);
        app.put("/api/orders/{orderId}", orderController::updateOrderHandler);
        app.delete("/api/orders/{orderId}", orderController::deleteOrderHandler);

        // Other order routes
        app.post("/api/create-order", orderController::createOrderHandler);
        app.get("/api/orders", orderController::getAllOrdersHandler);

        // Sales Data Routes
        app.get("/api/sales-summary", orderController::getSalesSummaryHandler);
        app.get("/api/today-orders", orderController::getTodayOrdersHandler);
        app.get("/api/sales-data-chart", orderController::getSalesDataHandler);

        // Low Stock and Store Numbers
        app.get("/api/low-stock-items", productController::getLowStockItemsHandler);
        app.get("/api/store-numbers", productController::getStoreNumbersHandler);

        // Admin-related routes
        app.post("/api/admin/login", adminController::adminLogin);
        app.post("/api/admin/create", adminController::createAdmin);
        app.get("/api/admins", adminController::getAllAdminsHandler);
        app.put("/api/admin/update", adminController::updatePermissions);
        app.delete("/api/admin/delete", adminController::deleteAdmin);

        // Supplier routes
        app.post("/api/addsuppliers", supplierController::addSupplierHandler);
        app.get("/api/suppliers", supplierController::getAllSuppliersHandler);
        app.put("/api/suppliers/{id}", supplierController::updateSupplierHandler);
        app.delete("/api/suppliers/{id}", supplierController::deleteSupplierHandler);

        // Wholesale customer routes
        app.post("/api/customers", customerController::addCustomerHandler);
        app.get("/api/customers", customerController::getAllCustomersHandler);
        app.put("/api/customers/{id}", customerController::updateCustomerHandler);
        app.delete("/api/customers/{id}", customerController::deleteCustomerHandler);

        // Promotion routes
        app.post("/api/create-promo", adminController::createPromotion);
        app.put("/api/promo/{promo_id}", adminController::updatePromotion);
        app.delete("/api/delete/{promo_id}", adminController::deletePromotion);
        app.get("/api/promotions", adminController::getAllPromotions); // Fetch all promotions
        app.get("/api/promotions/{promo_id}", adminController::getPromotionById);


        //profits


        app.get("/api/profit", orderController::getProfitByDateRangeHandler);

        //logout
        app.post("/api/admin/logout", adminController::adminLogout);


        // Default route to serve the index.html (frontend entry point)
        app.get("/", ctx -> {
            ctx.redirect("/index.html");
        });
    }

    private static void setupCORS(Javalin app) {
        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            ctx.header("Access-Control-Allow-Credentials", "true");
        });

        app.options("/*", ctx -> {
            ctx.status(204).result("OK");
        });
    }

    private static void setupExceptionHandling(Javalin app) {
        app.exception(Exception.class, (e, ctx) -> {
            logger.error("An unexpected error occurred:", e);
            if (e instanceof RuntimeException && e.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
                ctx.status(400).json(Map.of("message", "Invalid data format: " + e.getCause().getMessage()));
            } else {
                ctx.status(500).json(Map.of("error", "Internal Server Error", "message", e.getMessage()));
            }
        });
    }
}*/
