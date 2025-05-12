import * as excelParser from "./module/excelParser.js"
import * as tableRowsEditor from "./module/tableRowsEditor.js"
import * as tutorialMessage from "./module/tutorialMessage.js"
import * as tmessage from "./module/tmessage.js"
import * as tableFilter from "./module/tableFilter.js"

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