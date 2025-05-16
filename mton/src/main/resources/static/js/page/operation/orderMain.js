import * as excelParser from "./module/excelParser.js"
import * as tableRowsEditor from "./module/tableRowsEditor.js"
import * as tutorialMessage from "./module/tutorialMessage.js"
import * as tmessage from "./module/tmessage.js"
import * as tableFilter from "./module/tableFilter.js"

const orderViewTable = document.querySelector("#order-table-view");
const orderEditTable = document.querySelector("#order-table-edit");
const orderInputTable = document.querySelector("#order-table");

(function () {
    tableRowsEditor.initPageTable();
    orderViewTable.style.display = "table";
    orderEditTable.style.display = "table";
    orderInputTable.style.display = "table";
})();

// 수정, 등록용 테이블 초기화
// 필요없는 열을 삭제하고 수정 테이블에는 초기 내용을 삽입
(function () {
    tableRowsEditor.viewOrderingTable(true);
    tableRowsEditor.initEditButtons(orderEditTable);
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
            tableRowsEditor.addOrderTableUtilBtn(currentTable);
        });
    });

    insertUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");
        const dest = "/internal/procurement/register/order";
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

        tableRowsEditor.viewOrderingTable();
    });

    viewDownloadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");

        const editedTable = currentTable.cloneNode(true);
        excelParser.tableToFile(editedTable, currentTable.id + ".xlsx");
    });

    editRefreshBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        tableRowsEditor.viewOrderingTable();
    });

    editUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");
        const del = "/internal/procurement/delete/order";
        const upd = "/internal/procurement/update/order";
        const deleteResult = tableRowsEditor.uploadEditedTable(currentTable, "", del, upd);

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
    tableFilter.applyTableSorting(orderViewTable);
})();