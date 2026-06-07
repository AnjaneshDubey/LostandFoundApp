document.addEventListener('DOMContentLoaded', () => {
    // Auth Elements
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const loginError = document.getElementById('login-error');
    const registerError = document.getElementById('register-error');
    
    // View Elements
    const loginView = document.getElementById('login-view');
    const registerView = document.getElementById('register-view');
    const dashboardView = document.getElementById('dashboard-view');
    
    // Toggles
    const showRegisterBtn = document.getElementById('show-register');
    const showLoginBtn = document.getElementById('show-login');
    
    // Dashboard Elements
    const userGreeting = document.getElementById('user-greeting');
    const logoutBtn = document.getElementById('logout-btn');
    const itemsGrid = document.getElementById('items-grid');
    const searchInput = document.getElementById('search-input');
    
    let allItems = [];
    let currentUser = null;

    // Check if already logged in
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
        showDashboard(JSON.parse(storedUser));
    }

    // View Switching
    showRegisterBtn.addEventListener('click', () => {
        loginView.classList.remove('active');
        setTimeout(() => registerView.classList.add('active'), 300);
    });

    showLoginBtn.addEventListener('click', () => {
        registerView.classList.remove('active');
        setTimeout(() => loginView.classList.add('active'), 300);
    });

    // Login Submission
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const username = document.getElementById('login-username').value;
        const password = document.getElementById('login-password').value;
        
        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            
            if (response.ok) {
                const user = await response.json();
                localStorage.setItem('user', JSON.stringify(user));
                loginError.textContent = '';
                showDashboard(user);
            } else {
                loginError.textContent = 'Invalid username or password';
            }
        } catch (err) {
            loginError.textContent = 'Network error. Please try again.';
        }
    });

    // Register Submission
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const username = document.getElementById('reg-username').value;
        const email = document.getElementById('reg-email').value;
        const phone = document.getElementById('reg-phone').value;
        const password = document.getElementById('reg-password').value;
        const role = document.getElementById('reg-role').value;
        
        try {
            const response = await fetch('/api/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, phone, password, role })
            });
            
            if (response.ok) {
                const user = await response.json();
                localStorage.setItem('user', JSON.stringify(user));
                registerError.textContent = '';
                showDashboard(user);
            } else {
                const errData = await response.json();
                registerError.textContent = errData.error || 'Registration failed';
            }
        } catch (err) {
            registerError.textContent = 'Network error. Please try again.';
        }
    });

    // Logout
    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('user');
        currentUser = null;
        loginForm.reset();
        registerForm.reset();
        dashboardView.classList.remove('active');
        setTimeout(() => loginView.classList.add('active'), 300);
    });

    function showDashboard(user) {
        currentUser = user;
        loginView.classList.remove('active');
        registerView.classList.remove('active');
        setTimeout(() => dashboardView.classList.add('active'), 300);
        
        userGreeting.textContent = `Hello, ${user.username} ${user.role === 'ADMIN' ? '(Admin)' : ''}`;
        loadItems();
    }

    // Search Functionality
    searchInput.addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase();
        const filteredItems = allItems.filter(item => {
            return (item.itemName && item.itemName.toLowerCase().includes(query)) ||
                   (item.description && item.description.toLowerCase().includes(query)) ||
                   (item.category && item.category.toLowerCase().includes(query)) ||
                   (item.location && item.location.toLowerCase().includes(query)) ||
                   (item.trackingNumber && item.trackingNumber.toLowerCase().includes(query));
        });
        renderItems(filteredItems);
    });

    async function loadItems() {
        itemsGrid.innerHTML = '<p style="text-align: center; width: 100%; color: var(--text-light); font-weight: 500;">Loading items...</p>';
        try {
            const response = await fetch('/api/items');
            if (response.ok) {
                allItems = await response.json();
                renderItems(allItems);
            } else {
                itemsGrid.innerHTML = '<p class="error-msg">Failed to load items.</p>';
            }
        } catch (err) {
            itemsGrid.innerHTML = '<p class="error-msg">Network error.</p>';
        }
    }

    function renderItems(items) {
        itemsGrid.innerHTML = '';
        if (items.length === 0) {
            itemsGrid.innerHTML = '<p style="text-align: center; width: 100%; color: var(--text-light); font-weight: 500;">No items found.</p>';
            return;
        }

        items.forEach(item => {
            const card = document.createElement('div');
            card.className = 'glass-card item-card';
            
            const dateStr = new Date(item.createdAt).toLocaleDateString();
            
            card.innerHTML = `
                <div class="tracking-number">Tracking #: ${escapeHtml(item.trackingNumber)}</div>
                <div class="item-header">
                    <div class="item-title">${escapeHtml(item.itemName)}</div>
                    <div class="item-status status-${item.status}">${item.status}</div>
                </div>
                <div class="item-body">
                    <div class="item-detail">
                        <strong>Category:</strong> ${escapeHtml(item.category)}
                    </div>
                    <div class="item-detail">
                        <strong>Location:</strong> ${escapeHtml(item.location)}
                    </div>
                    <div class="item-detail">
                        <strong>Date:</strong> ${dateStr}
                    </div>
                    <div class="item-desc">
                        ${escapeHtml(item.description || 'No description provided')}
                    </div>
                </div>
                <div class="contact-info">
                    <div style="font-size: 0.85rem; font-weight: 700; color: var(--text-light); margin-bottom: 4px;">CONTACT ${item.status === 'LOST' ? 'OWNER' : 'FINDER'}</div>
                    ${item.reporterEmail ? `<div class="contact-chip">✉️ ${escapeHtml(item.reporterEmail)}</div>` : ''}
                    ${item.reporterPhone ? `<div class="contact-chip">📞 ${escapeHtml(item.reporterPhone)}</div>` : ''}
                    ${!item.reporterEmail && !item.reporterPhone ? '<div style="font-size: 0.9rem; color: var(--text-light);">No contact info available</div>' : ''}
                </div>
                ${currentUser && currentUser.role === 'ADMIN' ? `
                <div class="admin-actions">
                    <button class="admin-btn resolve-btn" onclick="updateItemStatus(${item.itemId}, 'CLAIMED')">Claimed</button>
                    <button class="admin-btn resolve-btn" onclick="updateItemStatus(${item.itemId}, 'RETURNED')">Returned</button>
                    <button class="admin-btn delete-btn" onclick="deleteItem(${item.itemId})">Delete</button>
                </div>
                ` : ''}
            `;
            
            itemsGrid.appendChild(card);
        });
    }

    // Helper to prevent XSS
    function escapeHtml(unsafe) {
        if (!unsafe) return '';
        return unsafe
             .replace(/&/g, "&amp;")
             .replace(/</g, "&lt;")
             .replace(/>/g, "&gt;")
             .replace(/"/g, "&quot;")
             .replace(/'/g, "&#039;");
    }

    // Admin API functions
    window.deleteItem = async function(itemId) {
        if (!confirm("Are you sure you want to delete this item?")) return;
        
        try {
            const res = await fetch('/api/items/action', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ action: 'DELETE', itemId: itemId, adminId: currentUser.userId })
            });
            if (res.ok) {
                loadItems();
            } else {
                alert("Failed to delete item.");
            }
        } catch (err) {
            console.error(err);
        }
    };

    window.updateItemStatus = async function(itemId, status) {
        try {
            const res = await fetch('/api/items/action', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ action: 'UPDATE_STATUS', itemId: itemId, adminId: currentUser.userId, status: status })
            });
            if (res.ok) {
                loadItems();
            } else {
                alert("Failed to update status.");
            }
        } catch (err) {
            console.error(err);
        }
    };
});
