async function fetchFraudulentOrders() {
    try {
        const response = await fetch("http://localhost:8080/api/ai/fraud-detection");
        const data = await response.json();

        const fraudDiv = document.getElementById("fraud-orders");
        fraudDiv.innerHTML = "";

        if (data.length === 0 || (data.length === 1 && data[0].message)) {
            fraudDiv.innerHTML = `<p>${data[0].message || "No fraudulent activity detected."}</p>`;
            return;
        }

        const table = document.createElement("table");
        table.innerHTML = `
            <tr>
                <th>Order ID</th>
                <th>Customer ID</th>
                <th>Total Items</th>
                <th>Unique Products</th>
            </tr>
        `;

        data.forEach(order => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${order.order_id}</td>
                <td>${order.customer_id}</td>
                <td>${order.total_items}</td>
                <td>${order.unique_products}</td>
            `;
            table.appendChild(row);
        });

        fraudDiv.appendChild(table);

    } catch (error) {
        console.error("Error fetching fraud data:", error);
    }
}
