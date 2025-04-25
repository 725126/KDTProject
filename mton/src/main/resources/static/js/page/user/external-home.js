document.addEventListener('DOMContentLoaded', function () {

    const calendarEl = document.getElementById('calendar');

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,listWeek'
        },
        locale: 'ko',
        height: 425,

        events: [
            {
                title: '계약 승인 대기',
                start: '2025-04-15',
                backgroundColor: '#F57C00',
                borderColor: '#F57C00',
                extendedProps: {
                    type: '계약 진행',
                    item: '드론 모터 부품 계약서',
                    remarks: '관리자 승인 대기 중'
                }
            },
            {
                title: '출고 준비 - 킥보드 배터리',
                start: '2025-04-17',
                backgroundColor: '#27496D',
                borderColor: '#27496D',
                extendedProps: {
                    type: '출고 준비',
                    item: '킥보드용 36V 배터리팩',
                    remarks: '금일 출고 예정, 운송장 확인 필요'
                }
            },
            {
                title: '검수 요청 - 자전거 부품',
                start: '2025-04-20',
                backgroundColor: '#00909E',
                borderColor: '#00909E',
                extendedProps: {
                    type: '검수 요청',
                    item: '자전거 브레이크 케이블',
                    remarks: '관리자 검수 요청 접수'
                }
            },
            {
                title: '견적 마감일 - 전동킥보드 휠',
                start: '2025-04-30',
                backgroundColor: '#C70039',
                borderColor: '#F57C00',
                extendedProps: {
                    type: '견적 제출',
                    item: '10인치 휠 (허브모터 포함)',
                    remarks: '제출 마감일 전 검토 필요'
                }
            }

        ],
        eventClick: function (info) {
            document.getElementById('modal-type').innerText = info.event.extendedProps.type;
            document.getElementById('modal-item').innerText = info.event.extendedProps.item;
            document.getElementById('modal-dates').innerText = info.event.startStr;
            document.getElementById('modal-remarks').innerText = info.event.extendedProps.remarks;
            new bootstrap.Modal(document.getElementById('eventModal')).show();
        }
    });

    calendar.render();


    // 그래프 : 납품 완료 vs 지연 추이
    const deliveryCtx = document.getElementById('deliveryChart').getContext('2d');
    const deliveryChart = new Chart(deliveryCtx, {
        type: 'bar',
        data: {
            labels: ['1월', '2월', '3월', '4월', '5월'],
            datasets: [
                {
                    label: '완료',
                    data: [18, 20, 22, 19, 23],
                    backgroundColor: '#198754' // Bootstrap green
                },
                {
                    label: '지연',
                    data: [2, 1, 3, 4, 2],
                    backgroundColor: '#dc3545' // Bootstrap red
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { position: 'top' },
                tooltip: { mode: 'index', intersect: false }
            },
            scales: {
                x: { stacked: true },
                y: { stacked: true, beginAtZero: true }
            }
        }
    });

    // 월별 견적 승인률 추이
    const approvalCtx = document.getElementById('approvalChart').getContext('2d');
    const approvalChart = new Chart(approvalCtx, {
        type: 'line',
        data: {
            labels: ['1월', '2월', '3월', '4월', '5월'],
            datasets: [
                {
                    label: '승인률 (%)',
                    data: [75, 82, 78, 85, 90],
                    fill: false,
                    borderColor: '#0d6efd', // Bootstrap blue
                    tension: 0.3,
                    pointBackgroundColor: '#0d6efd'
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: true },
                tooltip: { mode: 'index', intersect: false }
            },
            scales: {
                y: {
                    min: 0,
                    max: 100,
                    ticks: {
                        callback: function (value) {
                            return value + '%';
                        }
                    }
                }
            }
        }
    });

});

document.addEventListener('DOMContentLoaded', function () {
    const currentPath = window.location.pathname;

    document.querySelectorAll('.nav-link').forEach(link => {
        const href = link.getAttribute('href');

        if (href === currentPath) {
            // a 태그에 active 클래스 추가
            link.classList.add('active');

            // li에도 active 클래스 추가
            const parentLi = link.closest('li');
            if (parentLi) {
                parentLi.classList.add('active');
            }

            // 해당 링크가 속한 .collapse 부모 메뉴도 펼침
            const parentCollapse = link.closest('.collapse');
            if (parentCollapse) {
                parentCollapse.classList.add('show');

                const toggleBtn = document.querySelector(`[data-bs-toggle="collapse"][href="#${parentCollapse.id}"]`);
                if (toggleBtn) {
                    toggleBtn.setAttribute('aria-expanded', 'true');
                }
            }
        }
    });
});

