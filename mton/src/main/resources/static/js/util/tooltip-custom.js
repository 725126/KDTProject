document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(el => {
        new bootstrap.Tooltip(el, {
            customClass: 'tooltip-custom',
            trigger: 'hover focus'
        });
    });
});