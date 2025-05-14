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

// 계획관리 모드 라디오 버튼 이벤트 등록
(function () {
    tableRowsEditor.initPageTable();
    pplanViewTable.style.display = "table";
    pplanEditTable.style.display = "table";
    pplanInputTable.style.display = "table";
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
        tutorialMessage.bindTutorialMessage(viewDownloadBtn, tmessage.viewDownloadBtnTutorial)
        tutorialMessage.bindTutorialMessage(editRefreshBtn, tmessage.editRefreshBtnTutorial);
        tutorialMessage.bindTutorialMessage(editUploadBtn, tmessage.editUploadBtnTutorial);
    });
})();

(function () {
    tableFilter.applyTableSorting(pplanViewTable);
})();