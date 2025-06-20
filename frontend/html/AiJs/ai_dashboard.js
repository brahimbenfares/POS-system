document.addEventListener("DOMContentLoaded", async () => {
    try {
        const sales = await fetchSalesForecastSummary();
        const reorders = await fetchReorderSummary();
        const stockRisk = await fetchStockRiskSummary();
        const pricing = await fetchPricingSummary();
        const customerTrends = await fetchCustomerTrendsSummary();

        // Populate Cards with Data
        document.getElementById("salesForecastSummary").textContent = `${sales.totalPredicted.toFixed(2) || 0} منتج`;
        document.getElementById("reorderSummary").textContent = `${reorders.totalReorders || 0} طلب`;
        document.getElementById("stockRiskSummary").textContent = `${stockRisk.totalRisk || 0} منتج`;
        document.getElementById("pricingSummary").textContent = `💲 ${pricing.totalAdjustments || 0} تعديل`;
        document.getElementById("customerTrendsSummary").textContent = `${customerTrends.totalCustomers || 0} عميل`;
    } catch (error) {
        console.error("❌ Error fetching AI summaries:", error);
    }
});

// ✅ Fetch AI Sales Forecast Summary
async function fetchSalesForecastSummary() {
    try {
        const response = await fetch("/api/ai/sales-report");
        const data = await response.json();
        return { totalPredicted: data.reduce((acc, item) => acc + item.total_predicted_sales, 0) };
    } catch (error) {
        console.error("Error fetching sales forecast:", error);
        return { totalPredicted: 0 };
    }
}

// ✅ Fetch AI Reorder Summary
async function fetchReorderSummary() {
    try {
        const response = await fetch("/api/ai/reorders/grouped-history");
        const data = await response.json();
        return { totalReorders: data.length };
    } catch (error) {
        console.error("Error fetching reorder summary:", error);
        return { totalReorders: 0 };
    }
}

// ✅ Fetch AI Stock Risk Summary
async function fetchStockRiskSummary() {
    try {
        const response = await fetch("/api/ai/check-stock-risks");
        const data = await response.json();
        return { totalRisk: data.length };
    } catch (error) {
        console.error("Error fetching stock risks:", error);
        return { totalRisk: 0 };
    }
}

// ✅ Fetch AI Pricing Summary
async function fetchPricingSummary() {
    try {
        const response = await fetch("/api/ai/price-suggestions");
        const data = await response.json();
        return { totalAdjustments: data.length };
    } catch (error) {
        console.error("Error fetching pricing adjustments:", error);
        return { totalAdjustments: 0 };
    }
}

// ✅ Fetch AI Customer Trends Summary
async function fetchCustomerTrendsSummary() {
    try {
        const response = await fetch("/api/ai/customer-trends");
        const data = await response.json();
        return { totalCustomers: data.length };
    } catch (error) {
        console.error("Error fetching customer trends:", error);
        return { totalCustomers: 0 };
    }
}
