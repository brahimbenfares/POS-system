package com.walid.backend;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.walid.backend.Controller.ProductController;
import com.walid.backend.Controller.OrderController;
//import com.walid.backend.AI.SalesForecastAI;
import com.walid.backend.Controller.AISalesForecastController;
import com.walid.backend.Controller.AdminController;
import com.walid.backend.Controller.CustomerController;
import com.walid.backend.DAO.AdminDAO;
import com.walid.backend.DAO.OrderDAO;
import com.walid.backend.DAO.ProductDAO;
import com.walid.backend.Service.AISalesForecastService;
import com.walid.backend.Service.AdminService;
//import com.walid.backend.Service.CustomerService;
import com.walid.backend.Service.OrderService;
import com.walid.backend.Service.ProductService;
import com.walid.backend.Controller.SupplierController;
//import com.walid.backend.Controller.WholesaleCustomerController;
import com.walid.backend.Service.SupplierService;
//import com.walid.backend.Service.WholesaleCustomerService;
import ai.onnxruntime.OrtException;
import com.walid.backend.DAO.SupplierDAO;
//import com.walid.backend.DAO.WholesaleCustomerDAO;
//import com.walid.backend.DAO.CustomerDAO;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException, OrtException {
        // Initialize DAOs
        ProductDAO productDAO = new ProductDAO();
        AdminDAO adminDAO = new AdminDAO();
        OrderDAO orderDAO = new OrderDAO();
        //CustomerDAO customer = new CustomerDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
        //WholesaleCustomerDAO customerDAO = new WholesaleCustomerDAO();

        
        // Initialize  Services

        ProductService productService = new ProductService(productDAO);
        OrderService orderService = new OrderService(orderDAO);
        AdminService adminService = new AdminService(adminDAO);
        //CustomerService customerService= new CustomerService();
        SupplierService supplierService = new SupplierService(supplierDAO);
        //WholesaleCustomerService wholecustomerService = new WholesaleCustomerService(customerDAO);


        // Initialize Controllers
        ProductController productController = new ProductController(productService, adminService);
        OrderController orderController = new OrderController(orderService);
        AdminController adminController = new AdminController(adminService);
        CustomerController customerController = new CustomerController();
        SupplierController supplierController = new SupplierController(supplierService);
        //WholesaleCustomerController wholesaleCustomerController = new WholesaleCustomerController(wholecustomerService);

        



        AISalesForecastController aiController = new AISalesForecastController();

// ✅ Initialize AI Sales Forecast Service
AISalesForecastService forecastService = new AISalesForecastService();

// ✅ Initialize Scheduled Executor for Daily AI Forecast and Promotion Updates
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2); // Uses 2 threads for concurrency

// 🔄 Schedule Daily AI Sales Forecast Update
scheduler.scheduleAtFixedRate(() -> {
    try {
        System.out.println("🔄 Running AI Sales Forecast Update...");
        forecastService.generateDailyForecasts(); // Calls AI forecast generation
        System.out.println("✅ AI Forecasts updated successfully.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}, 0, 1, TimeUnit.DAYS); // Runs every 24 hours

// 🔄 Schedule Daily Promotion Update
scheduler.scheduleAtFixedRate(() -> {
    try {
        System.out.println("🔄 Running daily promotion update...");
        forecastService.updatePromotionsDaily(); // Calls the update method
        System.out.println("✅ Promotions updated successfully.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}, 0, 1, TimeUnit.DAYS); 
//}, 0, 1, TimeUnit.MINUTES);
// Runs every 24 hours and for immediate test use this }, 0, 1, TimeUnit.MINUTES);



        // Start Javalin server and configure routes
        Javalin app = startJavalin();
        configureRoutes(app, productController, orderController, adminController, supplierController,aiController,customerController);
    }



    
    private static Javalin startJavalin() {
        String projectRoot = System.getProperty("user.dir"); // Get the project root
        String frontendPath = Paths.get(projectRoot, "frontend", "html").toAbsolutePath().toString(); // Correct path
    
        return Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";   // Serve from root (`/`)
                staticFiles.directory = frontendPath;  // Set the correct frontend directory
                staticFiles.location = Location.EXTERNAL; // Use external path
            });
    
            // Jetty server setup
            config.jetty.server(() -> {
                Server server = new Server(new QueuedThreadPool());
                ServerConnector connector = new ServerConnector(server);
                connector.setPort(8080);
                server.addConnector(connector);
                return server;
            });
    
        }).start(8080);
    }
    

    
    

        

    private static void configureRoutes(Javalin app,
                                        ProductController productController,
                                        OrderController orderController,
                                        AdminController adminController,
                                        SupplierController supplierController,
                                        AISalesForecastController aiController,
                                        CustomerController customerController) {

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
        //app.post("/api/customers", wholecustomerController::addCustomerHandler);
        //app.get("/api/customers", wholecustomerController::getAllCustomersHandler);
        //app.put("/api/customers/{id}", wholecustomerController::updateCustomerHandler);
        //app.delete("/api/customers/{id}", wholecustomerController::deleteCustomerHandler);

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



        /* 🔥 AI 🔥 */
        app.get("/api/ai/predict-sales/{productId}/{daysAhead}", aiController.predictSales);
        app.get("/api/ai/get-forecasts/{productId}", aiController.getForecasts);  
        app.get("/api/ai/validate-forecasts/{productId}", aiController.validateForecasts); // Validate AI accuracy
        
        
        // check stock quantity based the for cast 
        app.get("/api/ai/check-stock-risks", aiController.checkStockRisks); // Identify low-stock risks
        // ✅ AI-Powered Dynamic Reordering System
        app.get("/api/ai/reorder-recommendations", aiController.getReorderRecommendations);
        app.get("/api/ai/vendor-recommendations", aiController.getVendorRecommendations);
        app.post("/api/ai/orders/create-reorders", aiController.createReorders);
        app.get("/api/ai/reorders/grouped-history", aiController.getPastReorders);
        app.get("/api/ai/reorders/{date}/details", aiController.getReorderDetails);
        app.put("/api/ai/reorders/update-all", aiController.updateAllReorderStatuses);
        app.get("/api/ai/customer-recommendations/{customerId}", aiController.getCustomerRecommendations);
        app.get("/api/ai/chatbot", aiController.getChatbotResponse);
        app.get("/api/ai/fraud-detection", aiController.detectFraud);
        
        

        
        // ✅ AI-Based Demand Trends Analysis
        app.get("/api/ai/demand-trends", aiController.getDemandTrends);

        // ✅ AI-Powered Auto-Pricing & Discounts
        app.get("/api/ai/price-suggestions", aiController.getPriceSuggestions);
        // ✅ AI-Generated Sales Forecast Reports
        app.get("/api/ai/sales-report", aiController.getSalesForecastReport);

        // ✅ AI-Based Customer Purchase Behavior
        app.get("/api/ai/customer-trends", aiController.getCustomerTrends);


        // ✅ AI-Driven Stock Optimization & Waste Reduction
        app.get("/api/ai/stock-optimization", aiController.getStockOptimization);

        app.get("/api/ai/auto-replenish", aiController.getAutoReplenishment);
        

        app.get("/api/ai/personalized-promotions", aiController.getPersonalizedPromotions);

        app.post("/api/ai/update-promotions", aiController.updatePromotionsDaily);


        // Customer

        app.post("/api/customers", customerController::addCustomerHandler);
        app.post("/api/customers/login", customerController::loginHandler);
        app.post("/api/customers/register", customerController::registerHandler);
        app.post("/api/customers/set-password", customerController::setPasswordHandler);
        app.get("/api/customers/profile", customerController::getCustomerProfile);
     
        
        // Default route to serve the index.html (frontend entry point)
        app.get("/", ctx -> {
            ctx.redirect("index.html"); // Serve from /static folder
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
}



/*app.get("/api/ai/predict-sales/{productId}/{daysAhead}", ctx -> {
            int productId = Integer.parseInt(ctx.pathParam("productId"));
            int daysAhead = Integer.parseInt(ctx.pathParam("daysAhead"));
            
            SalesForecastAI aiModel = new SalesForecastAI();
            double predictedSales = aiModel.predictSales(productId, daysAhead);
            
            ctx.json(Map.of(
                "product_id", productId,
                "days_ahead", daysAhead,
                "predicted_sales", predictedSales
            ));
        });*/
