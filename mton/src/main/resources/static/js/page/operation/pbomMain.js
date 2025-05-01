import * as excelParser from "./module/excelParser.js"
import * as tableRowsEditor from "./module/tableRowsEditor.js"

// 테이블
const matTable = document.getElementById("mat-table");
const prdTable = document.getElementById("prd-table");
const pbomTable = document.getElementById("pbom-table");

// 품목관리 모드 라디오 버튼 이벤트 등록
(function () {
    const manageMod = document.getElementById("manage-mod");

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
})();

// 등록용 테이블 뷰어 관련 이벤트 등록
(function () {
    // 라디오
    const filterMat = document.getElementById("filter-mat-v");
    const filterPrd = document.getElementById("filter-prd-v");
    const filterPbom = document.getElementById("filter-pbom-v");

    filterMat.addEventListener("change", function (event) {
        matTable.style.display = "table";
        prdTable.style.display = "none";
        pbomTable.style.display = "none";
    });

    filterPrd.addEventListener("change", function (event) {
        matTable.style.display = "none";
        prdTable.style.display = "table";
        pbomTable.style.display = "none";
    });

    filterPbom.addEventListener("change", function (event) {
        matTable.style.display = "none";
        prdTable.style.display = "none";
        pbomTable.style.display = "table";
    });

    const initial = document.querySelector("input[type='radio'][name='v-filter']:checked");
    initial.dispatchEvent(new Event('change'));
})();

// 테이블 ROW 관련 버튼 이벤트 등록
(function () {
    tableRowsEditor.initEmptyTable(matTable);
})();

(function () {
    const test = document.getElementById("upload-test");
    test.addEventListener("click", function (event) {
        event.preventDefault();
        event.stopPropagation();

        excelParser.tableUpload("/internal/product/test", document.getElementById("mat-table"));
    });
})();