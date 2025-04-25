// 사이드바
const sidebar = document.getElementById('sidebar');
const toggler = document.querySelector('.sidebar-toggler');
const toggleIcon = document.getElementById('toggle-icon');

toggler.addEventListener('click', () => {
    sidebar.classList.toggle('collapsed');
    toggleIcon.classList.toggle('bi-chevron-left');
    toggleIcon.classList.toggle('bi-chevron-right');
});

// document.addEventListener('DOMContentLoaded', function () {
//     const currentPath = window.location.pathname;
//
//     document.querySelectorAll('.nav-link').forEach(link => {
//         const href = link.getAttribute('href');
//
//         if (href === currentPath) {
//             // 링크 활성화
//             link.classList.add('active');
//
//             // 해당 링크가 속한 .collapse 부모 메뉴도 펼침
//             const parentCollapse = link.closest('.collapse');
//             if (parentCollapse) {
//                 parentCollapse.classList.add('show');
//
//                 // toggle 버튼도 열린 상태 표시 (화살표 방향 등)
//                 const toggleBtn = document.querySelector(`[data-bs-toggle="collapse"][href="#${parentCollapse.id}"]`);
//                 if (toggleBtn) {
//                     toggleBtn.setAttribute('aria-expanded', 'true');
//                 }
//             }
//         }
//     });
// });
// document.addEventListener('DOMContentLoaded', function () {
//     const currentPath = window.location.pathname;
//     const sidebar = document.querySelector('.sidebar');
//
//     // 모든 .nav-link 처리
//     document.querySelectorAll('.nav-link').forEach(link => {
//         const href = link.getAttribute('href');
//
//         if (href === currentPath) {
//             link.classList.add('active');
//
//             const parentCollapse = link.closest('.collapse');
//             if (parentCollapse) {
//                 // 사이드바가 펼쳐진 상태에서만 show 적용
//                 if (!sidebar.classList.contains('collapsed')) {
//                     parentCollapse.classList.add('show');
//                 }
//
//                 const toggleBtn = document.querySelector(`[data-bs-toggle="collapse"][href="#${parentCollapse.id}"]`);
//                 if (toggleBtn) {
//                     toggleBtn.setAttribute('aria-expanded', !sidebar.classList.contains('collapsed') ? 'true' : 'false');
//                 }
//             }
//         }
//     });
//
//     // 사이드바 토글 감지
//     const observer = new MutationObserver(() => {
//         const isCollapsed = sidebar.classList.contains('collapsed');
//
//         document.querySelectorAll('.nav-link.active').forEach(link => {
//             const parentCollapse = link.closest('.collapse');
//             if (parentCollapse) {
//                 if (isCollapsed) {
//                     parentCollapse.classList.remove('show');
//                 } else {
//                     parentCollapse.classList.add('show');
//                 }
//
//                 const toggleBtn = document.querySelector(`[data-bs-toggle="collapse"][href="#${parentCollapse.id}"]`);
//                 if (toggleBtn) {
//                     toggleBtn.setAttribute('aria-expanded', !isCollapsed ? 'true' : 'false');
//                 }
//             }
//         });
//
//         observer.observe(sidebar, {
//             attributes: true,
//             attributeFilter: ['class']
//         });
//
//     });
//
//     function highlightParentMenuWhenCollapsed() {
//         const sidebar = document.querySelector('.sidebar');
//         const isCollapsed = sidebar.classList.contains('collapsed');
//
//         document.querySelectorAll('.nav-link').forEach(link => {
//             const parentGroup = link.closest('.collapse');
//             const parentToggle = parentGroup ? document.querySelector(`[data-bs-toggle="collapse"][href="#${parentGroup.id}"]`) : null;
//
//             if (link.classList.contains('active') && isCollapsed && parentToggle) {
//                 parentToggle.classList.add('highlight-parent');
//             } else if (parentToggle) {
//                 parentToggle.classList.remove('highlight-parent');
//             }
//         });
//     }
//
//
//     // 사이드바 class 변경 감지 시작
//     observer.observe(sidebar, { attributes: true, attributeFilter: ['class'] });
// });

document.addEventListener('DOMContentLoaded', function () {
    const currentPath = window.location.pathname;
    const sidebar = document.querySelector('.sidebar');

    document.querySelectorAll('.nav-link').forEach(link => {
        const href = link.getAttribute('href');

        if (href === currentPath) {
            link.classList.add('active');

            const parentCollapse = link.closest('.collapse');
            const navItem = link.closest('li'); // 여기서 감싸는 li 찾음

            if (parentCollapse) {
                const toggleBtn = document.querySelector(`[data-bs-toggle="collapse"][href="#${parentCollapse.id}"]`);
                const toggleLi = toggleBtn ? toggleBtn.closest('li') : null;

                if (!sidebar.classList.contains('collapsed')) {
                    parentCollapse.classList.add('show');
                    if (toggleBtn) toggleBtn.setAttribute('aria-expanded', 'true');
                    if (toggleLi) toggleLi.classList.remove('has-active-sub');
                } else {
                    if (toggleBtn) toggleBtn.setAttribute('aria-expanded', 'false');
                    if (toggleLi) toggleLi.classList.add('has-active-sub'); // 여기!
                }
            }

            if (navItem) {
                navItem.classList.add('active'); // li 자체에도 active 클래스 부여 가능
            }
        }
    });

    const observer = new MutationObserver(() => {
        const isCollapsed = sidebar.classList.contains('collapsed');

        document.querySelectorAll('.nav-link.active').forEach(link => {
            const parentCollapse = link.closest('.collapse');
            const toggleBtn = parentCollapse
                ? document.querySelector(`[data-bs-toggle="collapse"][href="#${parentCollapse.id}"]`)
                : null;
            const toggleLi = toggleBtn ? toggleBtn.closest('li') : null;

            if (parentCollapse) {
                if (isCollapsed) {
                    parentCollapse.classList.remove('show');
                    if (toggleBtn) toggleBtn.setAttribute('aria-expanded', 'false');
                    if (toggleLi) toggleLi.classList.add('has-active-sub');
                } else {
                    parentCollapse.classList.add('show');
                    if (toggleBtn) toggleBtn.setAttribute('aria-expanded', 'true');
                    if (toggleLi) toggleLi.classList.remove('has-active-sub');
                }
            }
        });
    });

    observer.observe(sidebar, { attributes: true, attributeFilter: ['class'] });
});



