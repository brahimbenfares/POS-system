function logout() {
    // Call the backend logout endpoint to remove the adminToken cookie
    fetch('http://localhost:8080/api/admin/logout', {
        method: 'POST',
        credentials: 'include' // This ensures cookies are sent with the request
    })
    .then(response => {
        if (response.ok) {
            // Clear tokens from localStorage on successful logout
            localStorage.removeItem('adminToken');
            localStorage.removeItem('adminRole');

            // Redirect to login page
            window.location.href = 'index.html';
        } else {
            console.error('Failed to log out');
            alert('Logout failed. Please try again.');
        }
    })
    .catch(error => {
        console.error('Error during logout:', error);
        alert('An error occurred during logout. Please try again.');
    });
}
