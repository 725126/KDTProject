import * as excelParser from "./module/excelParser.js"
import * as tableRowsEditor from "./module/tableRowsEditor.js"
import * as tutorialMessage from "./module/tutorialMessage.js"
import * as tmessage from "./module/tmessage.js"
import * as tableFilter from "./module/tableFilter.js"

// 그룹 div
const viewGroup = document.getElementById("tables-view");
const editGroup = document.getElementById("tables-edit");
const inputGroup = document.getElementById("tables-input");

// 그룹 제어 버튼 div
const inputManageBtn = document.getElementById("input-manage-btn");
const editManageBtn = document.getElementById("edit-manage-btn");
const viewManageBtn = document.getElementById("view-manage-btn");

// 테이블 리스트
const allTables = document.querySelectorAll(".table-excel");
const viewTables = viewGroup.querySelectorAll("table");
const editTables = editGroup.querySelectorAll("table");
const inputTables = inputGroup.querySelectorAll("table");

// 테이블
const matViewTable = viewGroup.querySelector("table[id|='mat']");
const matEditTable = editGroup.querySelector("table[id|='mat']");
const matInputTable = inputGroup.querySelector("table[id|='mat']");

const prdViewTable = viewGroup.querySelector("table[id|='prd']");
const prdEditTable = editGroup.querySelector("table[id|='prd']");
const prdInputTable = inputGroup.querySelector("table[id|='prd']");

const pbomViewTable = viewGroup.querySelector("table[id|='pbom']");
const pbomEditTable = editGroup.querySelector("table[id|='pbom']");
const pbomInputTable = inputGroup.querySelector("table[id|='pbom']");

// 품목관리 모드 라디오 버튼 이벤트 등록
(function () {
    tableRowsEditor.initPageTable();
})();

// 등록용 테이블 뷰어 관련 이벤트 등록
(function () {
    // 라디오
    const filterMat = document.getElementById("filter-mat-v");
    const filterPrd = document.getElementById("filter-prd-v");
    const filterPbom = document.getElementById("filter-pbom-v");

    // 자재, 상품, PBOM 라디오 클릭시 테이블 전환 이벤트
    filterMat.addEventListener("change", function (event) {
        for (const table of allTables) {
            table.style.display = "none";
        }

        const enabled = document.querySelector("input[type='radio'][name='i-filter']:checked");
        switch (enabled.id) {
            case "mod-insert":
                matInputTable.style.display = "table";
                break;
            case "mod-edit":
                matEditTable.style.display = "table";
                break;
            case "mod-view":
                matViewTable.style.display = "table";
        }
    });

    filterPrd.addEventListener("change", function (event) {
        for (const table of allTables) {
            table.style.display = "none";
        }

        const enabled = document.querySelector("input[type='radio'][name='i-filter']:checked");
        switch (enabled.id) {
            case "mod-insert":
                prdInputTable.style.display = "table";
                break;
            case "mod-edit":
                prdEditTable.style.display = "table";
                break;
            case "mod-view":
                prdViewTable.style.display = "table";
        }
    });

    filterPbom.addEventListener("change", function (event) {
        for (const table of allTables) {
            table.style.display = "none";
        }

        const enabled = document.querySelector("input[type='radio'][name='i-filter']:checked");
        switch (enabled.id) {
            case "mod-insert":
                pbomInputTable.style.display = "table";
                break;
            case "mod-edit":
                pbomEditTable.style.display = "table";
                break;
            case "mod-view":
                pbomViewTable.style.display = "table";
        }
    });

    // 초기화용 이벤트 발생
    const initial = document.querySelector("input[type='radio'][name='v-filter']:checked");
    initial.dispatchEvent(new Event('change'));
})();

// 테이블 ROW 관련 버튼 이벤트 등록
(function () {
    // 등록 테이블들에 조작 버튼 달기
    for (const table of inputTables) {
        tableRowsEditor.initEmptyTable(table);
    }
})();

// 카드 우측 패널 버튼 이벤트
(function () {
    const insertFileBtn = document.getElementById("insert-file");
    const insertFileLabel = document.getElementById("insert-file-label");
    const insertUploadBtn = document.getElementById("insert-upload");
    const editRefreshBtn = document.getElementById("edit-refresh");
    const editUploadBtn = document.getElementById("edit-upload");
    const viewRefreshBtn = document.getElementById("view-refresh");
    const viewDownloadBtn = document.getElementById("view-download");

    // 파일 선택 버튼에 새 파일 선택시 내용 삽입
    insertFileBtn.addEventListener("change", function (e) {
        const currentTable = document.querySelector("table[style='display: table;']");
        const result = excelParser.sheetToTable(insertFileBtn.files[0], currentTable, true, -2);

        result.then(value => {
            tableRowsEditor.addTableUtilBtn(currentTable);
        });
    });

    // 업로드 버튼 클릭시 서버로 테이블 데이터를 전송
    insertUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("table[style='display: table;']");
        let dest = "";
        switch (currentTable.id) {
            case "mat-table":
                dest = "/internal/product/register/mat";
                break;
            case "prd-table":
                dest = "/internal/product/register/prd";
                break;
            case "pbom-table":
                dest = "/internal/product/register/pbom"
        }

        if (dest.length === 0) {
            console.log("테이블 참조가 잘못되었습니다.");
            return;
        }

        const result = excelParser.tableUpload(dest, currentTable);
        result.then((res) => {
            alert(res.message);
            viewRefreshBtn.dispatchEvent(new Event('click'));
            editRefreshBtn.dispatchEvent(new Event('click'));
        });
    });

    // 새로고침 버튼 클릭시 서버에서 목록 데이터를 받아옴
    viewRefreshBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        tableRowsEditor.viewAllProductTable();
    });

    // 다운로드 버튼 클릭시 화면에 보이는 테이블을 .xlsx 확장자로 다운로드
    viewDownloadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("table[style='display: table;']");
        excelParser.tableToFile(currentTable, currentTable.id + ".xlsx");
    });

    // 수정 새로고침 버튼
    editRefreshBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        tableRowsEditor.viewAllProductTable();
    });

    // 수정 반영 버튼
    editUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("table[style='display: table;']");
        let deleteResult = null;
        switch (currentTable.id) {
            case "mat-table-edit":
                deleteResult = tableRowsEditor.uploadEditedTable(currentTable, "material");
                break;
            case "prd-table-edit":
                deleteResult = tableRowsEditor.uploadEditedTable(currentTable, "product");
                break;
            case "pbom-table-edit":
                deleteResult = tableRowsEditor.uploadEditedTable(currentTable, "pbom");
        }

        if (deleteResult === null) { console.log("테이블 참조가 잘못되었습니다."); return; }

        deleteResult.then(res => {
            alert(res.message);
            editRefreshBtn.dispatchEvent(new Event("click"));
            viewRefreshBtn.dispatchEvent(new Event("click"));
        });
    });

    // 툴팁 메시지 바인딩
    tutorialMessage.bindTutorialMessage(insertFileLabel, tmessage.insertFileBtnTutorial);
    tutorialMessage.bindTutorialMessage(insertUploadBtn, tmessage.insertUploadBtnTutorial);
    tutorialMessage.bindTutorialMessage(viewRefreshBtn, tmessage.viewRefreshBtnTutorial);
    tutorialMessage.bindTutorialMessage(viewDownloadBtn, tmessage.viewDownloadBtnTutorial)
    tutorialMessage.bindTutorialMessage(editRefreshBtn, tmessage.editRefreshBtnTutorial);
    tutorialMessage.bindTutorialMessage(editUploadBtn, tmessage.editUploadBtnTutorial);
})();

// 테이블 뷰 관련 초기화
(function () {
    tableRowsEditor.viewAllProductTable();

    for (const table of editTables) {
        tableRowsEditor.initEditButtons(table);
    }
})();

// 뷰 테이블 소팅
(function () {
    tableFilter.applyTableSorting(matViewTable);
    tableFilter.applyTableSorting(prdViewTable);
    tableFilter.applyTableSorting(pbomViewTable);
})();
