import * as excelParser from "./module/excelParser.js"
import * as tableRowsEditor from "./module/tableRowsEditor.js"
import * as tutorialMessage from "./module/tutorialMessage.js"
import * as tmessage from "./module/tmessage.js"
import * as tableFilter from "./module/tableFilter.js"
import {addPPlanTableUtilBtn} from "./module/tableRowsEditor.js";

// 테이블
const pplanViewTable = document.querySelector("#pplan-table-view");
const pplanEditTable = document.querySelector("#pplan-table-edit");
const pplanInputTable = document.querySelector("#pplan-table");

// 생산계획 PBOM 파싱용
let pbomList = [];

// 계획관리 모드 라디오 버튼 이벤트 등록
(function () {
    tableRowsEditor.initPageTable();
    pplanViewTable.style.display = "table";
    pplanEditTable.style.display = "table";
    pplanInputTable.style.display = "table";

    const iFilters = document.querySelectorAll("input[type='radio'][name='i-filter']");
    const tableCard = document.getElementById("table-card");
    const btnCard = document.querySelector("#btn-card");
    btnCard.classList.add("d-none");

    for (const filter of iFilters) {
        filter.addEventListener("change", function () {
            const enabled = document.querySelector("input[type='radio'][name='i-filter']:checked");
            if (enabled.id === "mod-insert") {
                tableCard.classList.add("max-height-pplan");
                btnCard.classList.remove("d-none");
            } else {
                tableCard.classList.remove("max-height-pplan");
                btnCard.classList.add("d-none");
            }
        });
    }
})();

// 수정, 등록용 테이블 초기화
// 필요없는 열을 삭제하고 수정 테이블에는 초기 내용을 삽입
(function () {
    for (const row of pplanInputTable.rows) {
        row.deleteCell(7);
        row.deleteCell(5);
        row.deleteCell(3);
    }

    for (const row of pplanEditTable.rows) {
        row.deleteCell(5);
        row.deleteCell(3);
    }

    tableRowsEditor.viewPPlanTable();
    tableRowsEditor.initEditButtons(pplanEditTable);
})();

(function () {
    const insertFileBtn = document.getElementById("insert-file");
    const insertFileLabel = document.getElementById("insert-file-label");
    const insertUploadBtn = document.getElementById("insert-upload");
    const editRefreshBtn = document.getElementById("edit-refresh");
    const editUploadBtn = document.getElementById("edit-upload");
    const viewRefreshBtn = document.getElementById("view-refresh");
    const viewDownloadBtn = document.getElementById("view-download");

    insertFileBtn.addEventListener("change", function (e) {
        const currentTable = document.querySelector("div[style='display: block;'] table");
        const result = excelParser.sheetToTable(insertFileBtn.files[0], currentTable, true, -2);

        result.then(value => {
            tableRowsEditor.addPPlanTableUtilBtn(currentTable);
        });
    });

    insertUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");
        const dest = "/internal/procurement/register/pplan";
        const result = excelParser.tableUpload(dest, currentTable);
        result.then(res => {
            alert(res.message);
            viewRefreshBtn.dispatchEvent(new Event("click"));
            editRefreshBtn.dispatchEvent(new Event("click"));
            tableRowsEditor.viewChange();
        });
    });

    viewRefreshBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        tableRowsEditor.viewPPlanTable();
    });

    viewDownloadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");

        const editedTable = currentTable.cloneNode(true);
        for (const row of editedTable.rows) {
            row.deleteCell(7);
            row.deleteCell(5);
            row.deleteCell(3);
        }
        excelParser.tableToFile(editedTable, currentTable.id + ".xlsx");
    });

    editRefreshBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        tableRowsEditor.viewPPlanTable();
    });

    editUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");
        const deleteResult = tableRowsEditor.uploadEditedTable(currentTable, "pplan");

        if (deleteResult === null) { console.log("테이블 참조가 잘못되었습니다."); return; }

        deleteResult.then(res => {
            alert(res.message);
            editRefreshBtn.dispatchEvent(new Event("click"));
            viewRefreshBtn.dispatchEvent(new Event("click"));
        });

        tutorialMessage.bindTutorialMessage(insertFileLabel, tmessage.insertFileBtnTutorial);
        tutorialMessage.bindTutorialMessage(insertUploadBtn, tmessage.insertUploadBtnTutorial);
        tutorialMessage.bindTutorialMessage(viewRefreshBtn, tmessage.viewRefreshBtnTutorial);
        tutorialMessage.bindTutorialMessage(viewDownloadBtn, tmessage.viewDownloadBtnTutorial);
        tutorialMessage.bindTutorialMessage(editRefreshBtn, tmessage.editRefreshBtnTutorial);
        tutorialMessage.bindTutorialMessage(editUploadBtn, tmessage.editUploadBtnTutorial);
    });
})();

(function () {
    tableFilter.applyTableSorting(pplanViewTable);

    // 조달계획 자동생성 버튼 이벤트 등록
    const pplanButton = document.getElementById("pplan-btn");
    pplanButton.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const pplanId = document.getElementById("prdplan-select").value;
        const dest = "/internal/procurement/calc/pplan";

        const result = jsonFetcher(dest, pplanId);
        result.then(res => {
            automaticPplan(res, pplanInputTable);
            tableRowsEditor.addPPlanTableUtilBtn(pplanInputTable);
        });
    });
})();

// JSON 페치용
async function jsonFetcher(dest, jData) {
    return await fetch(dest, {
        method: "POST",
        headers: {
            "Content-Type": "text/plain",
            "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: jData
    }).then((res) => {
        return res.json();
    }).then(data => {
        return data;
    }).catch(error => {
        console.log(error);
    });
}

function automaticPplan(jData, table) {
    const newContent = [];

    for (const data of jData) {
        const row = {
            pplanId: "",
            prdplanId: data.prdplanId,
            matId: data.matId,
            pbomMaxQty: data.pbomMaxQty,
            prdplanEnd: data.prdplanEnd,
        }
        newContent.push(row);
    }

    console.log(newContent);

    const sheet = XLSX.utils.json_to_sheet(newContent);
    const htmlSheet = XLSX.utils.sheet_to_html(sheet, {editable: true});
    const startIndex = htmlSheet.indexOf("<tr", htmlSheet.indexOf("<tr") + 1);

    if (startIndex === -1) {
        console.log("file is match with table, but does not have data.");
        return false;
    }

    const endIndex = htmlSheet.lastIndexOf("</tr>") + "</tr>".length;
    const htmlRow = htmlSheet.substring(startIndex, endIndex);

    const tbody = table.querySelector("tbody");
    tbody.innerHTML = htmlRow;
}