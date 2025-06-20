document.addEventListener('DOMContentLoaded', () => {
    fetchReorderRecommendations();
    fetchReorderHistory(); // ✅ Fetch grouped reorders
});

// ✅ Fetch AI reorder recommendations
async function fetchReorderRecommendations() {
    try {
        const response = await fetch('http://localhost:8080/api/ai/reorder-recommendations');
        const data = await response.json();
        renderReorderTable(data);
    } catch (error) {
        console.error("❌ Error fetching reorder recommendations:", error);
    }
}

// ✅ Fetch past reorders (Grouped by reorder date)
async function fetchReorderHistory() {
    try {
        const response = await fetch('http://localhost:8080/api/ai/reorders/grouped-history');
        const data = await response.json();
        renderReorderHistory(data);
    } catch (error) {
        console.error("❌ Error fetching reorder history:", error);
    }
}

// ✅ Render AI reorder recommendations in the table
function renderReorderTable(reorders) {

    const tableBody = document.getElementById('reorderTable');
    tableBody.innerHTML = "";

    if (reorders.length === 0) {
        return;
    }

    reorders.forEach(product => {

        const row = document.createElement("tr");
        row.innerHTML = `
            <td class="border px-4 py-2 text-center">
                <input type="checkbox" class="reorder-checkbox" data-product-id="${product.product_id}">
            </td>
            <td class="border px-4 py-2">${product.product_id}</td>
            <td class="border px-4 py-2">${product.product_name}</td>
            <td class="border px-4 py-2">${product.stock_quantity}</td>
            <td class="border px-4 py-2">${product.predicted_sales}</td>
            <td class="border px-4 py-2">
                <input type="number" id="reorder_${product.product_id}" class="border px-2 py-1 w-16" value="${product.reorder_quantity}" min="1">
            </td>
            <td class="border px-4 py-2">
                <select id="supplier_${product.product_id}" class="border px-2 py-1 w-full">
                    <option value="">اختيار المورد</option>
                </select>
            </td>
        `;
        tableBody.appendChild(row);

        // ✅ Load suppliers and pre-select the last used supplier
        loadSuppliers(product.product_id, product.last_supplier_id);
    });
}

// ✅ Fetch suppliers and pre-select the last used one
async function loadSuppliers(productId, lastSupplierId) {
    try {
        const response = await fetch('http://localhost:8080/api/ai/vendor-recommendations');
        const suppliers = await response.json();
        const dropdown = document.getElementById(`supplier_${productId}`);

        suppliers.forEach(supplier => {
            const option = document.createElement("option");
            option.value = supplier.supplier_id;
            option.textContent = supplier.supplier_name;
            dropdown.appendChild(option);
        });

        // ✅ Pre-select the last used supplier (if available)
        if (lastSupplierId) {
            dropdown.value = lastSupplierId;
        }
    } catch (error) {
        console.error("❌ Error loading suppliers:", error);
    }
}

// ✅ Confirm and submit all selected reorder requests
function confirmReorders() {
    const selectedRows = document.querySelectorAll(".reorder-checkbox:checked");

    if (selectedRows.length === 0) {
        alert("❌ يرجى تحديد منتجات لإعادة الطلب.");
        return;
    }

    const reorderRequests = [];

    selectedRows.forEach(row => {
        const productId = row.dataset.productId;
        const reorderQty = parseInt(document.getElementById(`reorder_${productId}`).value, 10);
        const supplierId = document.getElementById(`supplier_${productId}`).value;

        if (!supplierId) {
            alert(`❌ يرجى اختيار مورد للمنتج رقم ${productId}.`);
            return;
        }

        reorderRequests.push({ productId, supplierId, reorderQty });
    });

    if (reorderRequests.length > 0) {
        submitBulkReorder(reorderRequests);
    }
}

// ✅ Submit multiple reorder requests in one API call
async function submitBulkReorder(reorderRequests) {
    try {
        const response = await fetch('http://localhost:8080/api/ai/orders/create-reorders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(reorderRequests)
        });

        const result = await response.json();
        alert(result.message || "✅ تم إرسال جميع الطلبات بنجاح!");

        // Refresh history after submitting
        fetchReorderHistory();
    } catch (error) {
        console.error("❌ Error submitting reorder requests:", error);
    }
}

function renderReorderHistory(reorders) {

    const tableBody = document.getElementById('reorderHistoryTable');
    tableBody.innerHTML = "";

    if (reorders.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-4">لا يوجد سجلات إعادة طلب.</td></tr>`;
        return;
    }

    // ✅ Group products by reorder date
    const groupedReorders = {};
    reorders.forEach(reorder => {
        const formattedDate = new Date(reorder.reorder_date).toISOString().split("T")[0];

        if (!groupedReorders[formattedDate]) {
            groupedReorders[formattedDate] = { 
                confirmed: 0, 
                canceled: 0, 
                pending: 0, 
                reorder_id: reorder.reorder_id 
            };
        }

        // ✅ Count Confirmed, Canceled, and Pending Products Separately
        if (reorder.status === "Confirmed") {
            groupedReorders[formattedDate].confirmed += reorder.total_products;
        } else if (reorder.status === "Canceled") {
            groupedReorders[formattedDate].canceled += reorder.total_products;
        } else {
            groupedReorders[formattedDate].pending += reorder.total_products;
        }
    });

    // ✅ Render the table rows with status column
    Object.entries(groupedReorders).forEach(([date, data]) => {
        const overallStatus = determineOverallStatus(data.confirmed, data.canceled, data.pending);
        
        const row = document.createElement("tr");
        row.innerHTML = `
            <td class="border px-4 py-2">${data.reorder_id}</td>
            <td class="border px-4 py-2">${date}</td>
            <td class="border px-4 py-2">${data.confirmed}</td>
            <td class="border px-4 py-2">${data.canceled}</td>
            <td class="border px-4 py-2">${getStatusBadge(overallStatus)}</td> <!-- ✅ NEW: Status Column -->
            <td class="border px-4 py-2">
                <a href="ai_reorder_details.html?reorder_date=${date}" 
                    class="bg-blue-500 text-white px-3 py-1 rounded">
                        عرض التفاصيل
                </a>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

// ✅ Function to Determine Overall Status for a Reorder Group
function determineOverallStatus(confirmed, canceled, pending) {
    if (pending > 0) return "قيد الانتظار";  // ⏳ قيد الانتظار (Pending)
    if (confirmed > 0 && canceled > 0) return "جزئي";  // ⚠ جزئي (Partial)
    if (confirmed > 0 && canceled === 0) return "مكتمل";  // ✅ مكتمل (Completed)
    if (canceled > 0 && confirmed === 0) return "ملغي";  // ❌ ملغي (Cancelled)
    return "غير معروف";  // 🔴 حالة غير معروفة (Unknown)
}


// ✅ Helper function to display reorder status with colored badges
function getStatusBadge(status) {
    switch (status) {
        case "قيد الانتظار":
            return `<span class="bg-yellow-500 text-white px-2 py-1 rounded">&#x23F3; قيد الانتظار</span>`; // ⏳
        case "مكتمل":
            return `<span class="bg-green-500 text-white px-2 py-1 rounded">&#x2705; مكتمل</span>`; // ✅
        case "ملغي":
            return `<span class="bg-red-500 text-white px-2 py-1 rounded">&#x274C; ملغي</span>`; // ❌
        case "جزئي":
            return `<span class="bg-gray-700 text-white px-2 py-1 rounded">&#x26A0; جزئي</span>`; // ⚠
        default:
            return `<span class="bg-gray-500 text-white px-2 py-1 rounded">${status}</span>`;
    }
}





/*document.addEventListener('DOMContentLoaded', () => {
    fetchReorderRecommendations();
});

// ✅ Fetch AI reorder recommendations
async function fetchReorderRecommendations() {
    try {
        const response = await fetch('http://localhost:8080/api/ai/reorder-recommendations');
        const data = await response.json();
        renderReorderTable(data);
    } catch (error) {
        console.error("❌ Error fetching reorder recommendations:", error);
    }
}

// ✅ Render AI reorder recommendations in the table
function renderReorderTable(reorders) {
    console.log("🔍 Received reorders:", reorders);

    const tableBody = document.getElementById('reorderTable');
    tableBody.innerHTML = "";

    if (reorders.length === 0) {
        console.log("⚠️ No reorders found!");
        return;
    }

    reorders.forEach(product => {
        console.log("✅ Adding product:", product);

        const row = document.createElement("tr");
        row.innerHTML = `
            <td class="border px-4 py-2 text-center">
                <input type="checkbox" class="reorder-checkbox" data-product-id="${product.product_id}">
            </td>
            <td class="border px-4 py-2">${product.product_id}</td>
            <td class="border px-4 py-2">${product.product_name}</td>
            <td class="border px-4 py-2">${product.stock_quantity}</td>
            <td class="border px-4 py-2">${product.predicted_sales}</td>
            <td class="border px-4 py-2">
                <input type="number" id="reorder_${product.product_id}" class="border px-2 py-1 w-16" value="${product.reorder_quantity}" min="1">
            </td>
            <td class="border px-4 py-2">
                <select id="supplier_${product.product_id}" class="border px-2 py-1 w-full">
                    <option value="">اختيار المورد</option>
                </select>
            </td>
        `;
        tableBody.appendChild(row);

        // ✅ Load suppliers and pre-select the last used supplier
        loadSuppliers(product.product_id, product.last_supplier_id);
    });
}

// ✅ Fetch suppliers and pre-select the last used one
async function loadSuppliers(productId, lastSupplierId) {
    try {
        const response = await fetch('http://localhost:8080/api/ai/vendor-recommendations');
        const suppliers = await response.json();
        const dropdown = document.getElementById(`supplier_${productId}`);

        suppliers.forEach(supplier => {
            const option = document.createElement("option");
            option.value = supplier.supplier_id;
            option.textContent = supplier.supplier_name;
            dropdown.appendChild(option);
        });

        // ✅ Pre-select the last used supplier (if available)
        if (lastSupplierId) {
            dropdown.value = lastSupplierId;
        }
    } catch (error) {
        console.error("❌ Error loading suppliers:", error);
    }
}

// ✅ Confirm and submit all selected reorder requests
function confirmReorders() {
    const selectedRows = document.querySelectorAll(".reorder-checkbox:checked");

    if (selectedRows.length === 0) {
        alert("❌ يرجى تحديد منتجات لإعادة الطلب.");
        return;
    }

    const reorderRequests = [];

    selectedRows.forEach(row => {
        const productId = row.dataset.productId;
        const reorderQty = parseInt(document.getElementById(`reorder_${productId}`).value, 10);
        const supplierId = document.getElementById(`supplier_${productId}`).value;

        if (!supplierId) {
            alert(`❌ يرجى اختيار مورد للمنتج رقم ${productId}.`);
            return;
        }

        reorderRequests.push({ productId, supplierId, reorderQty });
    });

    if (reorderRequests.length > 0) {
        submitBulkReorder(reorderRequests);
    }
}

// ✅ Submit multiple reorder requests in one API call
async function submitBulkReorder(reorderRequests) {
    try {
        const response = await fetch('http://localhost:8080/api/orders/create-reorders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(reorderRequests)
        });

        const result = await response.json();
        alert(result.message || "✅ تم إرسال جميع الطلبات بنجاح!");

    } catch (error) {
        console.error("❌ Error submitting reorder requests:", error);
    }
}

// ✅ Submit a single reorder request (optional)
async function submitReorder(productId) {
    const reorderQty = document.getElementById(`reorder_${productId}`).value;
    const supplierId = document.getElementById(`supplier_${productId}`).value;

    if (!supplierId) {
        alert("❌ يرجى اختيار مورد قبل تأكيد الطلب.");
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/ai/orders/create-reorder', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productId, supplierId, reorderQty })
        });

        const result = await response.json();
        alert(result.message || "✅ تم إرسال الطلب بنجاح!");

    } catch (error) {
        console.error("❌ Error submitting reorder request:", error);
    }
}*/
 