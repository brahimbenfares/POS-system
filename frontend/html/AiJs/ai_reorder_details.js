document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    const reorderDate = urlParams.get("reorder_date");

    if (reorderDate) {
        fetchReorderDetails(reorderDate);
    } else {
        console.error("❌ No reorder date provided.");
    }

    document.getElementById("sortBySupplier").addEventListener("click", () => {
        isSortedAscending = !isSortedAscending;
        renderReorderDetails(currentReorders);
    });

    document.getElementById("supplierFilter").addEventListener("change", filterBySupplier);
    document.getElementById("confirmAll").addEventListener("click", submitAllReorders);
});


let isSortedAscending = true;
let currentReorders = [];
let reorderStatusMap = JSON.parse(localStorage.getItem("reorderStatus")) || {}; // Store by `product_id`

// ✅ Fetch reorder details by date
async function fetchReorderDetails(reorderDate) {
    if (!reorderDate) return;

    try {
        const response = await fetch(`http://localhost:8080/api/ai/reorders/${reorderDate}/details`);
        const data = await response.json();
        currentReorders = data;
        renderReorderDetails(data);
    } catch (error) {
        console.error("❌ Error fetching reorder details:", error);
    }
}


// ✅ Render reorder details in the table (Sorted by Supplier)
function renderReorderDetails(reorders) {
    const tableBody = document.getElementById("reorderDetailsTable");
    tableBody.innerHTML = "";

    const supplierFilter = document.getElementById("supplierFilter");
    supplierFilter.innerHTML = `<option value="">عرض الجميع</option>`;

    const suppliers = new Set();

    reorders.sort((a, b) => {
        return isSortedAscending
            ? a.supplier_name.localeCompare(b.supplier_name)
            : b.supplier_name.localeCompare(a.supplier_name);
    });

    reorders.forEach(item => {
        suppliers.add(item.supplier_name);
        const key = `${item.product_id}_${item.supplier_id}`; // ✅ Use supplier_id
        const status = reorderStatusMap[key] || item.status;

        const row = document.createElement("tr");
        row.dataset.supplier = item.supplier_name;

        row.innerHTML = `
            <td class="border px-4 py-2">${item.product_name}</td>
            <td class="border px-4 py-2">${item.reorder_quantity}</td>
            <td class="border px-4 py-2">${item.supplier_name}</td>
            <td class="border px-4 py-2">
                <a href="tel:${item.supplier_phone}" class="text-blue-500 underline">${item.supplier_phone}</a>
            </td>
            <td class="border px-4 py-2">
                <a href="mailto:${item.supplier_email}" class="text-blue-500 underline">${item.supplier_email}</a>
            </td>
            <td class="border px-4 py-2 text-center">
                ${status === "Confirmed" ? "✅ تم التأكيد" : status === "Canceled" ? "❌ تم الإلغاء" : `
                    <button class="confirm-btn bg-green-500 text-white px-2 py-1 rounded" data-key="${key}">✅ تأكيد</button>
                    <button class="cancel-btn bg-red-500 text-white px-2 py-1 rounded" data-key="${key}">❌ إلغاء</button>
                `}
            </td>
        `;

        tableBody.appendChild(row);
    });

    document.querySelectorAll(".confirm-btn").forEach(btn => {
        btn.addEventListener("click", () => updateLocalReorderStatus(btn.dataset.key, "Confirmed"));
    });

    document.querySelectorAll(".cancel-btn").forEach(btn => {
        btn.addEventListener("click", () => updateLocalReorderStatus(btn.dataset.key, "Canceled"));
    });

    suppliers.forEach(supplier => {
        const option = document.createElement("option");
        option.value = supplier;
        option.textContent = supplier;
        supplierFilter.appendChild(option);
    });

    filterBySupplier();
}

// ✅ Store updated product status in localStorage
function updateLocalReorderStatus(key, status) {
    reorderStatusMap[key] = status;
    localStorage.setItem("reorderStatus", JSON.stringify(reorderStatusMap));
    renderReorderDetails(currentReorders);
}

// ✅ Submit all changes to the database in a single request
async function submitAllReorders() {
    const updates = Object.entries(reorderStatusMap)
        .map(([key, status]) => {
            const [product_id, supplier_id] = key.split("_"); // ✅ Send supplier_id instead of supplier_name
            return { product_id: parseInt(product_id), supplier_id: parseInt(supplier_id), status };
        });

    if (updates.length === 0) {
        alert("❌ لا توجد تغييرات لحفظها.");
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/ai/reorders/update-all", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ updates })
        });

        const result = await response.json();
        alert(result.message || "✅ تم تحديث جميع الطلبات بنجاح!");

        localStorage.removeItem("reorderStatus");
        reorderStatusMap = {};

        fetchReorderDetails(new URLSearchParams(window.location.search).get("reorder_date"));
    } catch (error) {
        console.error("❌ Error submitting reorders:", error);
    }
}



// ✅ Filter products by supplier
function filterBySupplier() {
    const selectedSupplier = document.getElementById("supplierFilter").value;
    document.querySelectorAll("#reorderDetailsTable tr").forEach(row => {
        row.style.display = selectedSupplier === "" || row.dataset.supplier === selectedSupplier ? "" : "none";
    });
}
