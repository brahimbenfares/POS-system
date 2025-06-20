async function fetchRecommendations() {
    const customerId = document.getElementById("customerId").value;
    if (!customerId) {
        alert("Please enter a Customer ID.");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/ai/customer-recommendations/${customerId}`);
        const data = await response.json();

        const recommendationsDiv = document.getElementById("recommendations");
        recommendationsDiv.innerHTML = "";

        if (data.length === 0) {
            recommendationsDiv.innerHTML = "<p>No recommendations available.</p>";
            return;
        }

        const list = document.createElement("ul");
        data.forEach(item => {
            const li = document.createElement("li");
            li.textContent = `${item.product_name} (Purchased ${item.purchase_count} times)`;
            list.appendChild(li);
        });

        recommendationsDiv.appendChild(list);

    } catch (error) {
        console.error("Error fetching recommendations:", error);
    }
}
