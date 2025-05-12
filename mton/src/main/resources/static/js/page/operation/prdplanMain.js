import * as excelParser from "./module/excelParser.js"
import * as tableRowsEditor from "./module/tableRowsEditor.js"
import * as tutorialMessage from "./module/tutorialMessage.js"
import * as tmessage from "./module/tmessage.js"
import * as tableFilter from "./module/tableFilter.js"

// 테이블: 리스트가 없는 이유는 어차피 테이블이 하나씩이라서.
const prdplanViewTable = document.querySelector("#prdplan-table-view");
const prdplanEditTable = document.querySelector("#prdplan-table-edit");
const prdplanInputTable = document.querySelector("#prdplan-table");

// 계획관리 모드 라디오 버튼 이벤트 등록
(function () {
    tableRowsEditor.initPageTable();
    prdplanViewTable.style.display = "table";
    prdplanEditTable.style.display = "table";
    prdplanInputTable.style.display = "table";
})();

// 수정, 등록용 테이블 초기화
// 필요없는 열을 삭제하고 수정 테이블에는 초기 내용을 삽입
(function () {
    for (const row of prdplanInputTable.rows) {
        row.deleteCell(6);
        row.deleteCell(4);
        row.deleteCell(2);
    }

    for (const row of prdplanEditTable.rows) {
        row.deleteCell(6);
        row.deleteCell(4);
        row.deleteCell(2);
    }

    tableRowsEditor.viewPrdPlanTable();
    tableRowsEditor.initEditButtons(prdplanEditTable);
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
            tableRowsEditor.addPrdPlanTableUtilBtn(currentTable);
        });
    });

    insertUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");
        const dest = "/internal/product/register/prdplan";
        const result = excelParser.tableUpload(dest, currentTable);
        result.then(res => {
            alert(res.message);
            viewRefreshBtn.dispatchEvent(new Event("click"));
            editRefreshBtn.dispatchEvent(new Event("click"));
        });
    });

    viewRefreshBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        tableRowsEditor.viewPrdPlanTable();
    });

    viewDownloadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");

        // 그대로 다운로드하면 나중에 집어넣을때 문제가 생기므로 이렇게 함
        const editedTable = currentTable.cloneNode(true);
        for (const row of editedTable.rows) {
            row.deleteCell(6);
            row.deleteCell(4);
            row.deleteCell(2);
        }
        excelParser.tableToFile(editedTable, currentTable.id + ".xlsx");
    });

    editRefreshBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        tableRowsEditor.viewPrdPlanTable();
    });

    editUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");
        const deleteResult = tableRowsEditor.uploadEditedTable(currentTable, "prdplan");

        if (deleteResult === null) { console.log("테이블 참조가 잘못되었습니다."); return; }

        deleteResult.then(res => {
            alert(res.message);
            editRefreshBtn.dispatchEvent(new Event("click"));
            viewRefreshBtn.dispatchEvent(new Event("click"));
        });
    });

    tutorialMessage.bindTutorialMessage(insertFileLabel, tmessage.insertFileBtnTutorial);
    tutorialMessage.bindTutorialMessage(insertUploadBtn, tmessage.insertUploadBtnTutorial);
    tutorialMessage.bindTutorialMessage(viewRefreshBtn, tmessage.viewRefreshBtnTutorial);
    tutorialMessage.bindTutorialMessage(viewDownloadBtn, tmessage.viewDownloadBtnTutorial)
    tutorialMessage.bindTutorialMessage(editRefreshBtn, tmessage.editRefreshBtnTutorial);
    tutorialMessage.bindTutorialMessage(editUploadBtn, tmessage.editUploadBtnTutorial);
})();

(function () {
    tableFilter.applyTableSorting(prdplanViewTable);
})();