document.addEventListener("DOMContentLoaded", async () => {
    try {
        const pricingData = await fetchPricingData();
        populatePricingTable(pricingData);
    } catch (error) {
        console.error("❌ Error fetching pricing data:", error);
    }
});

// ✅ Fetch AI Pricing Data
async function fetchPricingData() {
    try {
        const response = await fetch("/api/ai/price-suggestions");
        return await response.json();
    } catch (error) {
        console.error("Error fetching pricing data:", error);
        return [];
    }
}

// ✅ Populate Table with Pricing Data
function populatePricingTable(data) {
    const tableBody = document.getElementById("pricingTable");
    tableBody.innerHTML = "";

    data.forEach((item) => {
        const priceChange = ((item.suggested_price - item.current_price) / item.current_price) * 100;
        const changeColor = priceChange < 0 ? "text-green-600" : "text-red-600";

        const row = `
            <tr class="border-b">
                <td class="border px-4 py-2">${item.product_id}</td>
                <td class="border px-4 py-2">${item.current_price.toFixed(2)} DA</td>
                <td class="border px-4 py-2">${item.suggested_price.toFixed(2)} DA</td>
                <td class="border px-4 py-2 ${changeColor}">${priceChange.toFixed(2)}%</td>
                <td class="border px-4 py-2">
                    <button class="bg-blue-500 text-white px-4 py-1 rounded hover:bg-blue-600">
                        قبول السعر
                    </button>
                </td>
            </tr>
        `;
        tableBody.innerHTML += row;
    });
}
