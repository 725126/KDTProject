import * as excelParser from "./module/excelParser.js"
import * as tableRowsEditor from "./module/tableRowsEditor.js"
import * as tutorialMessage from "./module/tutorialMessage.js"
import * as tmessage from "./module/tmessage.js"

// 그룹 div
const viewGroup = document.getElementById("tables-view");
const editGroup = document.getElementById("tables-edit");
const inputGroup = document.getElementById("tables-input");

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
    const manageMod = document.getElementById("manage-mod");
    const insertLabel = manageMod.querySelector("#manage-insert");
    const editLabel = manageMod.querySelector("#manage-edit");
    const viewLabel = manageMod.querySelector("#manage-view");

    manageMod.addEventListener("click", function (event) {
        const labels = manageMod.querySelectorAll("label");

        for (const label of labels) {
            if (label.control.checked) {
                label.classList.add("pe-none");
                label.classList.remove("btn-outline-secondary");
                label.classList.add("btn-primary");
            } else {
                label.classList.remove("pe-none");
                label.classList.add("btn-outline-secondary");
                label.classList.remove("btn-primary");
            }
        }
    });

    insertLabel.addEventListener("change", function (event) {
        inputGroup.style.display = "block";
        editGroup.style.display = "none";
        viewGroup.style.display = "none";

        const initial = document.querySelector("input[type='radio'][name='v-filter']:checked");
        initial.dispatchEvent(new Event('change'));
    });

    editLabel.addEventListener("change", function (event) {
        inputGroup.style.display = "none";
        editGroup.style.display = "block";
        viewGroup.style.display = "block";

        const initial = document.querySelector("input[type='radio'][name='v-filter']:checked");
        initial.dispatchEvent(new Event('change'));
    });

    viewLabel.addEventListener("change", function (event) {
        inputGroup.style.display = "none";
        editGroup.style.display = "none";
        viewGroup.style.display = "block";

        const initial = document.querySelector("input[type='radio'][name='v-filter']:checked");
        initial.dispatchEvent(new Event('change'));
    });

    tutorialMessage.bindTutorialMessage(insertLabel, tmessage.manageInsertTutorial);
    tutorialMessage.bindTutorialMessage(editLabel, tmessage.manageEditTutorial);
    tutorialMessage.bindTutorialMessage(viewLabel, tmessage.manageViewTutorial);
})();

// 등록용 테이블 뷰어 관련 이벤트 등록
(function () {
    // 라디오
    const filterMat = document.getElementById("filter-mat-v");
    const filterPrd = document.getElementById("filter-prd-v");
    const filterPbom = document.getElementById("filter-pbom-v");

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

    const initial = document.querySelector("input[type='radio'][name='v-filter']:checked");
    initial.dispatchEvent(new Event('change'));
})();

// 테이블 ROW 관련 버튼 이벤트 등록
(function () {
    for (const table of inputTables) {
        tableRowsEditor.initEmptyTable(table);
    }
})();

// 업로드 테스트. 품목 관리 버튼을 누르면 mat-table 이 업로드됨
(function () {
    const test = document.getElementById("upload-test");
    test.addEventListener("click", function (event) {
        event.preventDefault();
        event.stopPropagation();

        excelParser.tableUpload("/internal/product/register/mat", document.getElementById("mat-table"));
    });

    tutorialMessage.bindTutorialMessage(test, "자재 테이블 임시 업로드 버튼으로 기능하고 있습니다.");
    test.style.width = "fit-content";
    test.style.cursor = "pointer";
})();

(function () {
    const table = document.querySelector("#mat-table-view");
    tableRowsEditor.viewTable(table, "material");
})();