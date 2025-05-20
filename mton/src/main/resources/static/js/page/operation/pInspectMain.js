import * as tableFilter from "./module/tableFilter.js";

const ordTable = document.getElementById("ord-table");
const insTable = document.getElementById("ins-table");
const qtyDialog = document.getElementById("qty-dialog");
const qtyCancel = document.getElementById("qty-cancel");
const qtySelect = document.getElementById("qty-select");
const qtySubmit = document.getElementById("qty-submit");
const ordSubmit = document.getElementById("ord-submit");
const insSubmit = document.getElementById("ins-submit");

let currentStatBtn = null;
let current

(function () {
    ordTable.style.display = "table";
    insTable.style.display = "table";
    tableFilter.applyTableSorting(ordTable);
    tableFilter.applyTableSorting(insTable);
})();

(function () {
    const statBtns = document.querySelectorAll(".stat-btn");

    for (const btn of statBtns) {
        btn.addEventListener("click", function(e) {
            e.preventDefault();
            e.stopPropagation();

            currentStatBtn = btn;

            qtyDialog.showModal();
            qtySubmit.focus();
        });
    }

    qtyCancel.addEventListener("click", function(e) {
        e.stopPropagation();
        currentStatBtn = null;
        qtyDialog.close();
    });

    qtyDialog.addEventListener("submit", function(e) {
        e.stopPropagation();

        if (currentStatBtn !== null) {
            if (qtySelect.value === "완료") {
                // 장납기 자재인지 검출해야함
                const list = Array.from(insTable.rows).filter(x =>
                    x.cells[1].innerText === currentStatBtn.parentElement.parentElement.cells[0].innerText
                    && x.cells[6].querySelector("span").innerText === "진행중"
                );
                if (list.length > 0) {
                    alert("아직 진척 검수가 끝나지 않았습니다. 진척도를 눌러 확인하세요.");
                    return;
                }
            }

            currentStatBtn.innerText = qtySelect.value;
            currentStatBtn.classList.remove("btn-primary", "btn-danger", "btn-success", "btn-warning");

            switch (qtySelect.value) {
                case "진행중":
                    currentStatBtn.classList.add("btn-primary");
                    break;
                case "취소":
                    currentStatBtn.classList.add("btn-danger");
                    break;
                case "완료":
                    currentStatBtn.classList.add("btn-success");
                    break;
            }
        }
    });

    ordSubmit.addEventListener("click", function(e) {
        e.stopPropagation();
        e.preventDefault();

        const dataTable = ordTable.cloneNode(true);
        const jData = Array.from(dataTable.rows).filter(row => row.rowIndex > 0).map(row => {
            return {
                orderId: row.cells[0].innerText,
                orderStat: row.cells[5].querySelector("button").innerText,
            }
        });

        console.log(jData);

        const result = fetch("/external/update/order", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').getAttribute('content')
            },
            body: JSON.stringify(jData)
        }).then(res => res.json()).then(data => {
            console.log(data);
            if (data.success === true) {
                location.reload();
            }
        }).catch(error => {
            console.log(error);
        });
    });
})();

(function () {
    const qtyBtns = document.querySelectorAll(".qty-btn");
    for (const btn of qtyBtns) {
        btn.addEventListener("click", function(e) {
            e.preventDefault();
            e.stopPropagation();

            const targetRow = Array.from(insTable.rows).filter(row =>
                row.cells[1].innerText === btn.parentElement.parentElement.cells[0].innerText
            );

            if (targetRow.length > 0) {
                targetRow[0].cells[2].focus();
            }
        });
    }

    insSubmit.addEventListener("click", function(e) {
        e.stopPropagation();
        e.preventDefault();

        const dataTable = insTable.cloneNode(true);
        const jData = Array.from(dataTable.rows).filter(row => row.rowIndex > 0).map(row => {
            return {
                insId: row.cells[0].innerText,
                insQty: row.cells[2].innerText,
            }
        });
        console.log(jData);

        const result = fetch("/external/update/inspect", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').getAttribute('content')
            },
            body: JSON.stringify(jData)
        }).then(res => res.json()).then(data => {
            console.log(data);
            if (data.success === true) {
                location.reload();
            }
        }).catch(error => {
            console.log(error);
        });
    });
})();