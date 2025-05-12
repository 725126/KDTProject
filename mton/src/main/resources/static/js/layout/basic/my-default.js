document.addEventListener("DOMContentLoaded", function () {
    const currentPath = window.location.pathname;

    const editLink = document.getElementById("editLink");
    const deleteLink = document.getElementById("deleteLink");

    if (currentPath.includes("/account-edit") || currentPath.includes("/admin/my/user/")) {
        editLink.classList.add("active");
        editLink.classList.add("rounded");

        deleteLink.classList.remove("active");
    } else if (currentPath.includes("/account-delete") || currentPath.includes("/admin/my/user-deleted/")) {
        deleteLink.classList.add("active");
        deleteLink.classList.add("rounded");

        editLink.classList.remove("active");
    }
});

function goBack() {
    const prevUrl = sessionStorage.getItem('prevUrl');
    if (prevUrl) {
        sessionStorage.removeItem('prevUrl');
        window.location.href = prevUrl;
    } else {
        window.location.href = '/admin/my/user-list'; // fallback
    }
}