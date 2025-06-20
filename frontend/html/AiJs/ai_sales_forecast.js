document.addEventListener('DOMContentLoaded', () => {
    loadProducts();
});

// Load products into the dropdown
async function loadProducts() {
    try {
        const response = await fetch('http://localhost:8080/api/products');
        const products = await response.json();
        const dropdown = document.getElementById('productDropdown');

        dropdown.innerHTML = '<option value="">Select a product</option>';
        products.forEach(product => {
            const option = document.createElement('option');
            option.value = product.id;  // ✅ Fix: Use 'id' instead of 'product_id'
            option.textContent = product.name;  // ✅ Fix: Use 'name' instead of 'product_name'
            dropdown.appendChild(option);
        });

    } catch (error) {
        console.error('Error loading products:', error);
    }
}


// Fetch AI Sales Forecast
async function fetchForecast() {
    const productId = document.getElementById('productDropdown').value;
    const daysAhead = document.getElementById('daysAhead').value;
    if (!productId) return alert('Please select a product.');

    try {
        const response = await fetch(`http://localhost:8080/api/ai/predict-sales/${productId}/${daysAhead}`);
        const data = await response.json();
        alert(`Predicted sales: ${data.predicted_sales}`);
    } catch (error) {
        console.error('Error fetching AI forecast:', error);
    }
}

// Validate AI Forecast
async function validateForecast() {
    const productId = document.getElementById('productDropdown').value;
    if (!productId) return alert('Please select a product.');

    try {
        const response = await fetch(`http://localhost:8080/api/ai/validate-forecasts/${productId}`);
        const data = await response.json();
        renderForecastChart(data);
    } catch (error) {
        console.error('Error validating AI forecast:', error);
    }
}

// Render AI vs Actual Sales Chart
function renderForecastChart(data) {
    const labels = data.map(record => record.forecast_date);
    const predictedSales = data.map(record => record.predicted_sales);
    const actualSales = data.map(record => record.actual_sales);

    const ctx = document.getElementById('forecastChart').getContext('2d');
    if (window.forecastChartInstance) {
        window.forecastChartInstance.destroy();
    }
    window.forecastChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets: [
                {
                    label: 'Predicted Sales',
                    data: predictedSales,
                    borderColor: 'blue',
                    fill: false,
                },
                {
                    label: 'Actual Sales',
                    data: actualSales,
                    borderColor: 'green',
                    fill: false,
                }
            ]
        },
        options: {
            responsive: true,
            scales: {
                x: { title: { display: true, text: 'Date' } },
                y: { title: { display: true, text: 'Sales' } }
            }
        }
    });
}
