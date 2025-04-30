// 파일을 읽은 다음 워크북을 리턴한다.
async function parseExcel(file) {
    try {
        const data = await file.arrayBuffer();
        return XLSX.read(data);
    } catch (e) {
        throw new Error(`Failed to parseSheet(${e.message})`);
    }
}

// 시트의 첫 번째 행, 즉 시트 테이블 헤더를 반환한다.
function sheetHeaders(sheet) {
    return XLSX.utils.sheet_to_json(sheet, { header: 1 })[0];
}

// 테이블의 첫 번째 행, 즉 html 테이블 헤더를 반환한다.
function tableHeaders(table) {
    return Array.from(table.querySelector("tr").children).map(x => x.textContent);
}

// 두 배열이 같은 내용을 가지고 있는지 확인한다.
function compareArrays(arr1, arr2) {
    return arr1.length === arr2.length && arr1.every((element, index) => element === arr2[index]);
}

// 테이블 검증 및 내용을 삽입한다.
// 대상 테이블에는 thead, th, tbody 가 포함되어야 한다.
function sheetToTable(file, table) {
    const workbook = parseExcel(file);
    workbook.then((value) => {
        const sheet = value.Sheets[value.SheetNames[0]];

        if (!compareArrays(sheetHeaders(sheet), tableHeaders(table))) {
            console.log("file does not match with table.");
            return false;
        }

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
        return true;
    });
}

// 지정한 테이블을 Excel 로 다운로드한다.
// 파일 이름을 지정하지 않을 경우 "document.xlsx"로 다운로드하며,
// 파일 이름을 지정할 경우 ".xlsx" 확장자가 필요하다.
function tableToFile(table, filename = "") {
    // 워크북, 시트, JSON 변환된 시트, 셀 내용의 길이
    const workbook = XLSX.utils.table_to_book(table);
    const worksheet = workbook.Sheets[workbook.SheetNames[0]];
    const jsheet = XLSX.utils.sheet_to_json(worksheet, {header: 1});
    const lens = jsheet.map(x => x.map(y => y.toString().length));
    const maxchar = Array(lens[0].length).fill(0);

    // 최대 내용 길이를 maxchar 에 넣음
    for (let i = 0; i < maxchar.length; i++) {
        for (const row of lens) {
            maxchar[i] = Math.max(maxchar[i], row[i]);
        }
    }

    // 열의 너비를 문자 단위로 설정
    for (let i = 1; i < maxchar.length; i++) {
        worksheet["!cols"][i] = {wch: maxchar[i]};
    }

    let downFile = "document.xlsx";
    if (filename.length > 0) {
        downFile = filename;
    }

    XLSX.writeFile(workbook, downFile);
}

// Codes above need this dependency:
// <script lang="javascript" src="https://cdn.sheetjs.com/xlsx-0.20.3/package/dist/xlsx.full.min.js"></script>
