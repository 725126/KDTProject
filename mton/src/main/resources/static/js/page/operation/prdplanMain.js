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

    tableRowsEditor.initEmptyTable(prdplanInputTable);
    tableRowsEditor.addTableUtilBtn(prdplanInputTable);

    for (const row of prdplanEditTable.rows) {
        row.deleteCell(6);
        row.deleteCell(4);
        row.deleteCell(2);
    }

    tableRowsEditor.viewPrdPlanTable();
    tableRowsEditor.initEditButtons(prdplanEditTable);
})();
