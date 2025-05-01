document.addEventListener("DOMContentLoaded", function () {
    const currentPath = window.location.pathname;

    const editLink = document.getElementById("editLink");
    const deleteLink = document.getElementById("deleteLink");

    if (currentPath.includes("/account-edit")) {
        editLink.classList.add("active");
        editLink.classList.add("rounded");
    } else if (currentPath.includes("/account-delete")) {
        deleteLink.classList.add("active");
        deleteLink.classList.remove("text-danger"); // 선택적
        deleteLink.classList.add("rounded");
    }
});