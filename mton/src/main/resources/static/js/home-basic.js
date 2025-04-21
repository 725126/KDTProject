// 사이드바
const sidebar = document.getElementById('sidebar');
const toggler = document.querySelector('.sidebar-toggler');
const toggleIcon = document.getElementById('toggle-icon');

toggler.addEventListener('click', () => {
    sidebar.classList.toggle('collapsed');
    toggleIcon.classList.toggle('bi-chevron-left');
    toggleIcon.classList.toggle('bi-chevron-right');
});