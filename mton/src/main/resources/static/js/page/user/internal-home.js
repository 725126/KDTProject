document.addEventListener('DOMContentLoaded', function () {
    // 캘린더, 그래프 js
    const calendarEl = document.getElementById('calendar');

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,listWeek'
        },
        locale: 'ko',
        height: 684,

        events: [
            {
                title: '입고 예정 - 배터리',
                start: '2025-05-27',
                backgroundColor: '#00909E',
                extendedProps: {
                    type: '입고 예정',
                    item: '배터리 셀 21700',
                    remarks: '중국 협력사 출하 대기 중'
                }
            },
            {
                title: '입고 예정22 - 배터리',
                start: '2025-05-27',
                backgroundColor: '#00909E',
                extendedProps: {
                    type: '입고 예정',
                    item: '배터리 셀 21700',
                    remarks: '중국 협력사 출하 대기 중'
                }
            },
            {
                title: '입고 예정99 - 배터리',
                start: '2025-05-27',
                backgroundColor: '#00909E',
                extendedProps: {
                    type: '입고 예정',
                    item: ['배터리 셀 21700', '1234', '45697'],
                    remarks: '중국 협력사 출하 대기 중'
                }
            },
            {
                title: '발주일 - 킥보드 부품',
                start: '2025-05-30',
                backgroundColor: '#27496D',
                borderColor: '#27496D',
                extendedProps: {
                    type: '발주',
                    item: '킥보드 핸들바',
                    remarks: '긴급 발주'
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

    // Chart.js 설정
    const inoutCtx = document.getElementById('inoutChart').getContext('2d');
    new Chart(inoutCtx, {
        type: 'bar',
        data: {
            labels: ['월', '화', '수', '목', '금'],
            datasets: [
                { label: '입고', data: [1000, 1200, 900, 1400, 1100] },
                { label: '출고', data: [800, 950, 700, 1200, 980] }
            ]
        },
        options: { responsive: true }
    });

    const stockCtx = document.getElementById('stockChart').getContext('2d');
    new Chart(stockCtx, {
        type: 'line',
        data: {
            labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
            datasets: [{ label: '재고금액', data: [45, 50, 53, 56] }]
        },
        options: { responsive: true }
    });

});
