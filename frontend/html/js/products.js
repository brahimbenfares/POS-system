$(document).ready(function () {
    const apiURL = "/api/products";
    const productList = $("#product-list");
    const cartCount = $("#cart-count");

    let cart = JSON.parse(localStorage.getItem("cart")) || [];

    // Fetch and Display Products
    function fetchProducts() {
        $.get(apiURL, function (products) {
            productList.empty(); // Clear list before rendering
            products.forEach(product => {
                // Ensure the property names match your API response
                const productCard = `
                    <div class="product-card">
                        <img src="${product.image_url || 'img/default-product.jpg'}" alt="${product.name}">
                        <h3>${product.name}</h3>
                        <p>Price: $${(product.salesPrice || 0).toFixed(2)}</p>
                        <button class="add-to-cart" data-id="${product.id}">Add to Cart</button>
                    </div>
                `;
                productList.append(productCard);
            });

            // Attach event listener after rendering
            $(".add-to-cart").click(addToCart);
        }).fail(function () {
            productList.html("<p>Error loading products. Please try again later.</p>");
        });
    }

    // Add product to Cart
    function addToCart() {
        const productId = $(this).data("id");
        const existingItem = cart.find(item => item.id === productId);

        if (existingItem) {
            existingItem.quantity += 1;
        } else {
            cart.push({ id: productId, quantity: 1 });
        }

        localStorage.setItem("cart", JSON.stringify(cart));
        updateCartCount();
    }

    // Update Cart Count
    function updateCartCount() {
        cartCount.text(cart.reduce((sum, item) => sum + item.quantity, 0));
    }

    // Initialize Page
    fetchProducts();
    updateCartCount();
});
