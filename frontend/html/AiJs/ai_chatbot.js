function setChatbotInput(question) {
    document.getElementById("chatbot-input").value = question;
}


async function askChatbot() {
    const userMessage = document.getElementById("chatbot-input").value.trim();
    const chatbotDiv = document.getElementById("chatbot-response");

    if (!userMessage) {
        chatbotDiv.innerHTML = `<p class="text-red-500">❌ يرجى إدخال سؤال أولاً.</p>`;
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/ai/chatbot?message=${encodeURIComponent(userMessage)}`);
        const data = await response.json();

        let parsedData;
        try {
            parsedData = typeof data.message === 'string' ? JSON.parse(data.message) : data.message;
        } catch {
            parsedData = data.message;
        }

        // Render appropriate UI based on the question
        if (userMessage.toLowerCase().includes("predict sales")) {
            chatbotDiv.innerHTML = renderSalesForecast(parsedData);
        } else if (userMessage.toLowerCase().includes("price suggestion")) {
            chatbotDiv.innerHTML = renderPriceSuggestions(parsedData);
        } else if (userMessage.toLowerCase().includes("customer trend")) {
            chatbotDiv.innerHTML = renderCustomerTrends(parsedData);
        } else if (userMessage.toLowerCase().includes("suspicious") || userMessage.toLowerCase().includes("fraud")) {
            chatbotDiv.innerHTML = `<p class="text-green-600">${parsedData[0]?.message || "✅ No issues detected."}</p>`;
        } else if (Array.isArray(parsedData) && parsedData.length === 0) {
            chatbotDiv.innerHTML = `<p class="text-yellow-500">ℹ️ لا توجد بيانات حالياً لهذا الاستفسار.</p>`;
        } else {
            chatbotDiv.innerHTML = `<p>${data.message}</p>`;
        }

    } catch (error) {
        console.error("❌ Error fetching chatbot response:", error);
        chatbotDiv.innerHTML = `<p class="text-red-500">❌ حدث خطأ أثناء الاتصال بالخادم.</p>`;
    }
}


function renderSalesForecast(data) {
    if (!Array.isArray(data)) return "<p>❌ Invalid data format</p>";

    let rows = data.map(item => `
        <tr class="border-b">
            <td class="p-2">${item.product_name}</td>
            <td class="p-2 text-center">${item.total_predicted_sales.toFixed(2)}</td>
            <td class="p-2 text-center">${item.total_predicted_revenue.toFixed(2)} DA</td>
        </tr>
    `).join('');

    return `
        <div>
            <h2 class="text-lg font-semibold mb-2">📈 توقعات المبيعات للأسبوع القادم:</h2>
            <table class="min-w-full table-auto border text-sm bg-white">
                <thead class="bg-gray-100">
                    <tr>
                        <th class="p-2 text-left">المنتج</th>
                        <th class="p-2 text-center">المبيعات المتوقعة</th>
                        <th class="p-2 text-center">الإيرادات المتوقعة</th>
                    </tr>
                </thead>
                <tbody>${rows}</tbody>
            </table>
        </div>
    `;
}

function renderPriceSuggestions(data) {
    if (!Array.isArray(data)) return "<p>❌ Invalid data format</p>";

    let cards = data.map(item => `
        <div class="border rounded-lg p-4 shadow-sm bg-gray-50">
            <p class="text-gray-700 font-medium">🛒 Product ID: <span class="text-blue-600">${item.product_id}</span></p>
            <p>💰 Current Price: <span class="line-through text-red-500">${item.current_price} DA</span></p>
            <p>✅ Suggested Price: <span class="text-green-600">${item.suggested_price} DA</span></p>
        </div>
    `).join('');

    return `
        <div>
            <h2 class="text-lg font-semibold mb-2">📊 اقتراحات الأسعار:</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">${cards}</div>
        </div>
    `;
}

function renderCustomerTrends(data) {
    if (!Array.isArray(data)) return "<p>❌ Invalid data format</p>";

    let rows = data.map(item => `
        <tr class="border-b">
            <td class="p-2">${item.product_name}</td>
            <td class="p-2 text-center">${item.total_sold}</td>
            <td class="p-2 text-center">${item.total_orders}</td>
            <td class="p-2 text-center">${item.peak_hour}:00</td>
        </tr>
    `).join('');

    return `
        <div>
            <h2 class="text-lg font-semibold mb-2">📌 اتجاهات العملاء الحالية:</h2>
            <table class="min-w-full table-auto border text-sm bg-white">
                <thead class="bg-gray-100">
                    <tr>
                        <th class="p-2 text-left">المنتج</th>
                        <th class="p-2 text-center">إجمالي المبيعات</th>
                        <th class="p-2 text-center">عدد الطلبات</th>
                        <th class="p-2 text-center">الساعة الأكثر نشاطًا</th>
                    </tr>
                </thead>
                <tbody>${rows}</tbody>
            </table>
        </div>
    `;
}
