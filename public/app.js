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
    
    // Navbar & Modals
    const navAllItems = document.getElementById('nav-all-items');
    const navMyItems = document.getElementById('nav-my-items');
    const navReportItem = document.getElementById('nav-report-item');
    
    const profileBtn = document.getElementById('profile-btn');
    const profileDropdown = document.getElementById('profile-dropdown');
    
    const itemModal = document.getElementById('item-modal');
    const itemForm = document.getElementById('item-form');
    const modalCancel = document.getElementById('modal-cancel');
    const modalCloseX = document.getElementById('modal-close-x');
    const modalTitle = document.getElementById('modal-title');
    const modalStatusGroup = document.getElementById('modal-status-group');
    const modalItemId = document.getElementById('modal-item-id');

    // Profile Modal
    const profileModal = document.getElementById('profile-modal');
    const profileCloseX = document.getElementById('profile-close-x');
    const profileAvatarContainer = document.getElementById('profile-avatar-container');
    const profileAvatarUpload = document.getElementById('profile-avatar-upload');
    const profileRoleBadge = document.getElementById('profile-role-badge');
    const profileUsername = document.getElementById('profile-username');
    const profileEmail = document.getElementById('profile-email');
    const profilePhone = document.getElementById('profile-phone');
    const profileCollege = document.getElementById('profile-college');
    
    let allItems = [];
    let currentUser = null;
    let showingMyItems = false;

    // Toast Notifications
    window.showToast = function(msg, type = 'success') {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = msg;
        container.appendChild(toast);
        
        toast.offsetHeight; // trigger reflow
        toast.classList.add('show');
        
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    };

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
        const collegeName = document.getElementById('reg-college').value;
        
        try {
            const response = await fetch('/api/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, phone, password, collegeName })
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
        showToast('Logged out successfully');
        setTimeout(() => loginView.classList.add('active'), 300);
    });

    // Profile Dropdown Logic
    if (profileBtn) {
        profileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            profileDropdown.classList.toggle('show');
        });
    }

    window.addEventListener('click', () => {
        if (profileDropdown && profileDropdown.classList.contains('show')) {
            profileDropdown.classList.remove('show');
        }
    });

    document.getElementById('menu-profile')?.addEventListener('click', (e) => { 
        e.preventDefault(); 
        if (currentUser.avatarBase64) {
            profileAvatarContainer.innerHTML = `<img src="${currentUser.avatarBase64}" style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;">`;
        } else {
            profileAvatarContainer.innerHTML = `<span id="profile-avatar-initials">${currentUser.username.charAt(0).toUpperCase()}</span>`;
        }
        profileRoleBadge.textContent = currentUser.role;
        profileUsername.value = currentUser.username;
        profileEmail.value = currentUser.email || '';
        profilePhone.value = currentUser.phone || '';
        profileCollege.value = currentUser.collegeName || '';
        
        if(currentUser.role === 'ADMIN') {
            profileRoleBadge.className = 'item-status status-LOST';
        } else {
            profileRoleBadge.className = 'item-status status-FOUND';
        }

        profileModal.classList.add('active'); 
    });

    const profileForm = document.getElementById('profile-form');
    if (profileForm) {
        profileForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const updatedData = {
                action: 'UPDATE_PROFILE',
                userId: currentUser.userId,
                username: profileUsername.value,
                email: profileEmail.value,
                phone: profilePhone.value,
                collegeName: profileCollege.value
            };
            
            try {
                const response = await fetch('/api/user/action', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(updatedData)
                });
                
                if (response.ok) {
                    currentUser = await response.json();
                    
                    const userGreeting = document.getElementById('user-greeting');
                    if (userGreeting) {
                        userGreeting.textContent = `Hello, ${currentUser.username} ${currentUser.role === 'ADMIN' ? '(Admin)' : ''}`;
                    }
                    
                    showToast('Profile updated successfully!', 'success');
                    profileModal.classList.remove('active');
                } else {
                    const err = await response.json();
                    showToast(err.error || 'Failed to update profile.');
                }
            } catch (error) {
                console.error(error);
                showToast('Error communicating with server.');
            }
        });
    }

    if (profileCloseX) {
        profileCloseX.addEventListener('click', () => {
            profileModal.classList.remove('active');
        });
    }

    if (profileAvatarContainer && profileAvatarUpload) {
        profileAvatarUpload.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = async (event) => {
                    const base64Data = event.target.result;
                    
                    const data = {
                        action: 'UPDATE_AVATAR',
                        userId: currentUser.userId,
                        avatarBase64: base64Data
                    };
                    
                    try {
                        const response = await fetch('/api/user/action', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify(data)
                        });
                        
                        if (response.ok) {
                            currentUser = await response.json();
                            if (currentUser.avatarBase64) {
                                profileAvatarContainer.innerHTML = `<img src="${currentUser.avatarBase64}" style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;">`;
                            }
                            showToast('Avatar updated successfully!', 'success');
                        } else {
                            showToast('Failed to update avatar.');
                        }
                    } catch(err) {
                        showToast('Error updating avatar.');
                    }
                };
                reader.readAsDataURL(file);
            }
        });
    }

    document.getElementById('menu-settings')?.addEventListener('click', (e) => { e.preventDefault(); showToast('Settings coming soon!'); });
    document.getElementById('menu-contact')?.addEventListener('click', (e) => { e.preventDefault(); showToast('Contact support at support@college.edu', 'success'); });

    function showDashboard(user) {
        currentUser = user;
        loginView.classList.remove('active');
        registerView.classList.remove('active');
        setTimeout(() => dashboardView.classList.add('active'), 300);
        
        const userGreeting = document.getElementById('user-greeting');
        if (userGreeting) {
            userGreeting.textContent = `Hello, ${user.username} ${user.role === 'ADMIN' ? '(Admin)' : ''}`;
        }
        
        const dashboardTitle = document.getElementById('dashboard-title');
        if (dashboardTitle && user.collegeName) {
            dashboardTitle.textContent = `Find Stuff - ${user.collegeName}`;
        }

        loadItems();
    }

    // Navbar events
    navAllItems.addEventListener('click', () => {
        showingMyItems = false;
        navAllItems.classList.add('active');
        navMyItems.classList.remove('active');
        renderItems(allItems);
    });

    navMyItems.addEventListener('click', () => {
        showingMyItems = true;
        navMyItems.classList.add('active');
        navAllItems.classList.remove('active');
        renderItems(allItems);
    });

    navReportItem.addEventListener('click', () => {
        itemForm.reset();
        modalItemId.value = '';
        modalTitle.textContent = 'Report Item';
        modalStatusGroup.style.display = 'block';
        itemModal.classList.add('active');
    });

    modalCancel.addEventListener('click', () => {
        itemModal.classList.remove('active');
    });
    
    if (modalCloseX) {
        modalCloseX.addEventListener('click', () => {
            itemModal.classList.remove('active');
        });
    }

    // Form Submissions
    itemForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const action = modalItemId.value ? 'EDIT' : 'REPORT';
        
        const fileInput = document.getElementById('modal-image');
        let imageBase64 = null;
        if (fileInput && fileInput.files.length > 0) {
            const file = fileInput.files[0];
            const reader = new FileReader();
            reader.readAsDataURL(file);
            await new Promise(resolve => {
                reader.onload = () => {
                    imageBase64 = reader.result;
                    resolve();
                };
            });
        }

        const payload = {
            action: action,
            userId: currentUser.userId,
            collegeName: currentUser.collegeName,
            status: document.getElementById('modal-status').value,
            itemName: document.getElementById('modal-name').value,
            category: document.getElementById('modal-category').value,
            location: document.getElementById('modal-location').value,
            description: document.getElementById('modal-description').value,
            imageBase64: imageBase64,
            date: new Date().getTime()
        };

        if (action === 'EDIT') {
            payload.itemId = parseInt(modalItemId.value);
        }

        try {
            const res = await fetch('/api/items/action', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                showToast(action === 'EDIT' ? 'Item updated!' : 'Item reported successfully!');
                itemModal.classList.remove('active');
                loadItems();
            } else {
                showToast('Failed to save item.', 'error');
            }
        } catch (err) {
            showToast('Network error.', 'error');
        }
    });

    window.openEditModal = function(id, name, cat, loc, desc) {
        modalItemId.value = id;
        document.getElementById('modal-name').value = name;
        document.getElementById('modal-category').value = cat;
        document.getElementById('modal-location').value = loc;
        document.getElementById('modal-description').value = desc || '';
        document.getElementById('modal-image').value = ''; // clear file input
        
        modalTitle.textContent = 'Edit Item';
        modalStatusGroup.style.display = 'none'; // Can't change type of report
        itemModal.classList.add('active');
    };

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
            const collegeQuery = currentUser && currentUser.collegeName ? '?college=' + encodeURIComponent(currentUser.collegeName) : '';
            const response = await fetch('/api/items' + collegeQuery);
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
        
        let itemsToRender = items;
        if (showingMyItems && currentUser) {
            itemsToRender = items.filter(item => item.userId === currentUser.userId);
        }

        if (itemsToRender.length === 0) {
            itemsGrid.innerHTML = '<p style="text-align: center; width: 100%; color: var(--text-light); font-weight: 500;">No items found.</p>';
            return;
        }

        itemsToRender.forEach(item => {
            const card = document.createElement('div');
            card.className = 'glass-card item-card';
            
            const dateStr = new Date(item.createdAt || new Date()).toLocaleDateString();
            const imageHtml = item.imageBase64 ? `<img src="${item.imageBase64}" class="item-image" alt="Item Image">` : '';
            
            card.innerHTML = `
                <div class="tracking-number">Tracking #: ${escapeHtml(item.trackingNumber)}</div>
                ${imageHtml}
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
                ${currentUser && currentUser.userId === item.userId ? `
                <div class="contact-info" style="margin-top: 10px;">
                    <button class="btn secondary-btn" style="width: 100%" data-edit-id="${item.itemId}">Edit Item</button>
                </div>
                ` : ''}
                ${currentUser && currentUser.role === 'ADMIN' ? `
                <div class="admin-actions">
                    <button class="admin-btn resolve-btn" onclick="updateItemStatus(${item.itemId}, 'CLAIMED')">Claimed</button>
                    <button class="admin-btn resolve-btn" onclick="updateItemStatus(${item.itemId}, 'RETURNED')">Returned</button>
                    <button class="admin-btn delete-btn" onclick="deleteItem(${item.itemId})">Delete</button>
                </div>
                ` : ''}
            `;
            
            itemsGrid.appendChild(card);

            if (currentUser && currentUser.userId === item.userId) {
                const editBtn = card.querySelector('[data-edit-id]');
                if(editBtn) {
                    editBtn.addEventListener('click', () => openEditModal(item.itemId, item.itemName, item.category, item.location, item.description));
                }
            }
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
                showToast("Item deleted!");
                loadItems();
            } else {
                showToast("Failed to delete item.", "error");
            }
        } catch (err) {
            console.error(err);
            showToast("Network error.", "error");
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
                showToast("Status updated!");
                loadItems();
            } else {
                showToast("Failed to update status.", "error");
            }
        } catch (err) {
            console.error(err);
            showToast("Network error.", "error");
        }
    };
});
