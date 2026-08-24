/* =========================================================
   Generic Category / Publisher Manager
   ========================================================= */

function createResourceManager(type, endpoints, label) {

    if (!requireAdmin()) return;

    const list = document.getElementById("resourceList");
    const input = document.getElementById("resourceName");
    const saveButton = document.getElementById("saveResourceBtn");
    const clearButton = document.getElementById("cancelEdit");

    if (!list || !input || !saveButton || !clearButton) {
        console.error("Resource manager HTML elements are missing.");
        return;
    }

    let editingId = null;

    function resetForm() {
        editingId = null;
        input.value = "";
        saveButton.textContent = `Add ${label}`;
    }

    async function load() {
        list.innerHTML = `<div class="empty">Loading ${label.toLowerCase()}s...</div>`;

        try {
            const response = await apiFetch(endpoints.all);

            if (!response) return;

            const data = await readResponse(response);

            if (!response.ok) {
                const message = typeof data === "string"
                    ? data
                    : data?.message || `Unable to load ${label.toLowerCase()}s.`;

                list.innerHTML = `<div class="empty">${escapeHtml(message)}</div>`;

                if (response.status === 403) {
                    showToast(
                        "Access denied. Login again with an ADMIN account.",
                        "error"
                    );
                }

                return;
            }

            const rows = Array.isArray(data) ? data : [];

            if (rows.length === 0) {
                list.innerHTML = `<div class="empty">No ${label.toLowerCase()}s found.</div>`;
                return;
            }

            list.innerHTML = rows.map(item => `
                <div class="resource-row">
                    <div>
                        <div class="resource-name">
                            ${escapeHtml(item.name)}
                        </div>
                        <small class="muted">ID: ${item.id}</small>
                    </div>

                    <div class="small-actions">
                        <button
                            type="button"
                            class="btn btn-outline edit-resource"
                            data-id="${item.id}"
                            data-name="${escapeHtml(item.name)}">
                            Edit
                        </button>

                        <button
                            type="button"
                            class="btn btn-danger delete-resource"
                            data-id="${item.id}">
                            Delete
                        </button>
                    </div>
                </div>
            `).join("");

            document.querySelectorAll(".edit-resource").forEach(button => {
                button.addEventListener("click", () => {
                    editingId = button.dataset.id;
                    input.value = button.dataset.name;
                    saveButton.textContent = `Update ${label}`;
                    input.focus();
                });
            });

            document.querySelectorAll(".delete-resource").forEach(button => {
                button.addEventListener("click", () => {
                    deleteResource(button.dataset.id);
                });
            });

        } catch (error) {
            console.error(error);
            list.innerHTML = `<div class="empty">Unable to connect to the server.</div>`;
        }
    }

    async function deleteResource(id) {
        if (!confirm(`Delete this ${label.toLowerCase()}?`)) return;

        try {
            const response = await apiFetch(
                endpoints.delete(id),
                { method: "DELETE" }
            );

            if (!response) return;

            const data = await readResponse(response);

            if (!response.ok) {
                const message = typeof data === "string"
                    ? data
                    : data?.message || `Unable to delete ${label.toLowerCase()}.`;

                showToast(message, "error");
                return;
            }

            showToast(`${label} deleted successfully.`, "success");
            await load();

        } catch (error) {
            console.error(error);
            showToast(`Unable to delete ${label.toLowerCase()}.`, "error");
        }
    }

    saveButton.addEventListener("click", async () => {
        const name = input.value.trim();

        if (!name) {
            showToast(`Enter a ${label.toLowerCase()} name.`, "error");
            input.focus();
            return;
        }

        const path = editingId
            ? endpoints.update(editingId)
            : endpoints.add;

        const method = editingId ? "PUT" : "POST";

        saveButton.disabled = true;
        saveButton.textContent = editingId
            ? `Updating ${label}...`
            : `Adding ${label}...`;

        try {
            const response = await apiFetch(path, {
                method,
                body: JSON.stringify({ name })
            });

            if (!response) return;

            const data = await readResponse(response);

            if (!response.ok) {
                const message = typeof data === "string"
                    ? data
                    : data?.message || `Unable to save ${label.toLowerCase()}.`;

                if (response.status === 403) {
                    showToast(
                        "Access denied. Please logout and login again with your ADMIN account.",
                        "error"
                    );
                } else if (message.toLowerCase().includes("already exists")) {
                    showToast(`${label} already exists.`, "error");
                } else {
                    showToast(message, "error");
                }

                return;
            }

            showToast(
                editingId
                    ? `${label} updated successfully.`
                    : `${label} added successfully.`,
                "success"
            );

            resetForm();
            await load();

        } catch (error) {
            console.error(error);
            showToast(`Unable to save ${label.toLowerCase()}.`, "error");
        } finally {
            saveButton.disabled = false;
            saveButton.textContent = editingId
                ? `Update ${label}`
                : `Add ${label}`;
        }
    });

    clearButton.addEventListener("click", resetForm);

    input.addEventListener("keydown", event => {
        if (event.key === "Enter") {
            event.preventDefault();
            saveButton.click();
        }
    });

    load();
}
