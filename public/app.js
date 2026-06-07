document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const loginError = document.getElementById('login-error');
    
    const loginView = document.getElementById('login-view');
    const dashboardView = document.getElementById('dashboard-view');
    
    const userGreeting = document.getElementById('user-greeting');
    const logoutBtn = document.getElementById('logout-btn');
    const itemsGrid = document.getElementById('items-grid');

    // Check if already logged in
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
        showDashboard(JSON.parse(storedUser));
    }

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
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
            console.error(err);
        }
    });

    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('user');
        loginForm.reset();
        loginView.classList.add('active');
        dashboardView.classList.remove('active');
    });

    function showDashboard(user) {
        loginView.classList.remove('active');
        dashboardView.classList.add('active');
        userGreeting.textContent = `Hello, ${user.username}`;
        
        loadItems();
    }

    async function loadItems() {
        itemsGrid.innerHTML = '<p>Loading items...</p>';
        try {
            const response = await fetch('/api/items');
            if (response.ok) {
                const items = await response.json();
                renderItems(items);
            } else {
                itemsGrid.innerHTML = '<p class="error-msg">Failed to load items.</p>';
            }
        } catch (err) {
            itemsGrid.innerHTML = '<p class="error-msg">Network error.</p>';
            console.error(err);
        }
    }

    function renderItems(items) {
        itemsGrid.innerHTML = '';
        if (items.length === 0) {
            itemsGrid.innerHTML = '<p>No items found.</p>';
            return;
        }

        items.forEach(item => {
            const card = document.createElement('div');
            card.className = 'glass-card item-card';
            
            const dateStr = new Date(item.createdAt).toLocaleDateString();
            
            card.innerHTML = `
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
                    <div class="item-detail" style="margin-top: 10px;">
                        ${escapeHtml(item.description || 'No description provided')}
                    </div>
                </div>
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
});
