document.addEventListener('DOMContentLoaded', () => {
    fetchStockRisks();
});

// Fetch AI stock risk analysis
async function fetchStockRisks() {
    try {
        const response = await fetch('http://localhost:8080/api/ai/check-stock-risks');
        const data = await response.json();
        renderStockRiskTable(data);
    } catch (error) {
        console.error("❌ Error fetching stock risks:", error);
    }
}

// Render stock risk table
function renderStockRiskTable(stockRisks) {
    const tableBody = document.getElementById('stockRiskTable');
    tableBody.innerHTML = "";

    stockRisks.forEach(product => {
        const row = document.createElement("tr");

        // Determine risk level
        let riskLevel = "منخفض"; // Default: Low
        let bgColor = "bg-green-100";
        if (product.predicted_sales > product.stock_quantity) {
            riskLevel = "مرتفع";
            bgColor = "bg-red-100";
        } else if (product.predicted_sales * 0.8 > product.stock_quantity) {
            riskLevel = "متوسط";
            bgColor = "bg-yellow-100";
        }

        row.className = `${bgColor} border`;

        row.innerHTML = `
            <td class="border px-4 py-2">${product.product_id}</td>
            <td class="border px-4 py-2">${product.product_name}</td>
            <td class="border px-4 py-2">${product.stock_quantity}</td>
            <td class="border px-4 py-2">${product.predicted_sales}</td>
            <td class="border px-4 py-2 font-bold">${riskLevel}</td>
        `;

        tableBody.appendChild(row);
    });
}
