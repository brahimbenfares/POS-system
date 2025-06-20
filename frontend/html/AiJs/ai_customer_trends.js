document.addEventListener("DOMContentLoaded", async () => {
    document.getElementById("dateFilter").addEventListener("change", loadCustomerTrends);
    await loadCustomerTrends(); // Initial load
});

// ✅ Fetch AI Customer Trends Data Based on Filter
async function fetchCustomerTrends(dateFilter = "all") {
    try {
        const response = await fetch(`/api/ai/customer-trends?filter=${dateFilter}`);
        return await response.json();
    } catch (error) {
        console.error("Error fetching customer trends:", error);
        return [];
    }
}

// ✅ Reload Data When Filter Changes
async function loadCustomerTrends() {
    const dateFilter = document.getElementById("dateFilter").value;
    const customerTrends = await fetchCustomerTrends(dateFilter);
    
    populateTopOrderedProductsTable(customerTrends);
    populateTrendingProductsTable(customerTrends);
    renderPeakHoursChart(customerTrends);
}

// ✅ Populate "Most Ordered Products" Table
function populateTopOrderedProductsTable(data) {
    const tableBody = document.getElementById("topOrderedProductsTable");
    tableBody.innerHTML = "";

    // Sort by total orders (highest first)
    const sortedData = [...data].sort((a, b) => b.total_orders - a.total_orders);

    sortedData.slice(0, 10).forEach((item, index) => {
        const row = `
            <tr class="border-b">
                <td class="border px-4 py-2 text-center">${index + 1}</td>
                <td class="border px-4 py-2">${item.product_name}</td>
                <td class="border px-4 py-2 text-center">${item.total_orders}</td>
                <td class="border px-4 py-2 text-center">${item.total_sold}</td>
            </tr>
        `;
        tableBody.innerHTML += row;
    });
}

// ✅ Populate "Trending Products" Table
function populateTrendingProductsTable(data) {
    const tableBody = document.getElementById("trendingProductsTable");
    tableBody.innerHTML = "";

    // Sort by total sold (highest first)
    const sortedData = [...data].sort((a, b) => b.total_sold - a.total_sold);

    sortedData.slice(0, 10).forEach((item, index) => {
        const row = `
            <tr class="border-b">
                <td class="border px-4 py-2 text-center">${index + 1}</td>
                <td class="border px-4 py-2">${item.product_name}</td>
                <td class="border px-4 py-2 text-center">${item.total_sold}</td>
                <td class="border px-4 py-2 text-center">${item.total_orders}</td>
            </tr>
        `;
        tableBody.innerHTML += row;
    });
}

// ✅ Render Peak Buying Hours Chart (Bar Chart)
function renderPeakHoursChart(data) {
    const ctx = document.getElementById("peakHoursChart").getContext("2d");

    // Aggregate peak hours
    const peakHours = {};
    data.forEach(item => {
        const hour = item.peak_hour;
        peakHours[hour] = (peakHours[hour] || 0) + item.total_orders;
    });

    // Sort hours
    const sortedHours = Object.keys(peakHours)
        .map(hour => ({ hour: parseInt(hour), count: peakHours[hour] }))
        .sort((a, b) => a.hour - b.hour);

    // Prepare data for Chart.js
    const labels = sortedHours.map(item => `${item.hour}:00`);
    const values = sortedHours.map(item => item.count);

    // Clear existing chart before creating a new one
    if (window.peakHoursChartInstance) {
        window.peakHoursChartInstance.destroy();
    }

    window.peakHoursChartInstance = new Chart(ctx, {
        type: "bar",
        data: {
            labels,
            datasets: [{
                label: "🕒 عدد الطلبات لكل ساعة",
                data: values,
                backgroundColor: "rgba(54, 162, 235, 0.6)",
                borderColor: "rgba(54, 162, 235, 1)",
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
}
