// 사이드바
const sidebar = document.getElementById('sidebar');
const toggler = document.querySelector('.sidebar-toggler');
const toggleIcon = document.getElementById('toggle-icon');

toggler.addEventListener('click', () => {
    sidebar.classList.toggle('collapsed');
    toggleIcon.classList.toggle('bi-chevron-left');
    toggleIcon.classList.toggle('bi-chevron-right');
});

document.addEventListener('DOMContentLoaded', function () {
    const currentPath = window.location.pathname;

    document.querySelectorAll('.nav-link').forEach(link => {
        const href = link.getAttribute('href');

        if (href === currentPath) {
            // 링크 활성화
            link.classList.add('active');

            // 해당 링크가 속한 .collapse 부모 메뉴도 펼침
            const parentCollapse = link.closest('.collapse');
            if (parentCollapse) {
                parentCollapse.classList.add('show');

                // toggle 버튼도 열린 상태 표시 (화살표 방향 등)
                const toggleBtn = document.querySelector(`[data-bs-toggle="collapse"][href="#${parentCollapse.id}"]`);
                if (toggleBtn) {
                    toggleBtn.setAttribute('aria-expanded', 'true');
                }
            }
        }
    });
});
