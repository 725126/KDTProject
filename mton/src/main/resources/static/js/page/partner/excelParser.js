// Codes below need this dependency:
// <script lang="javascript" src="https://cdn.sheetjs.com/xlsx-0.20.3/package/dist/xlsx.full.min.js"></script>

// 파일을 읽은 다음 첫 번째 시트를 JSON Object 로 리턴
async function parseSheet(data) {
    let file;
    try {
        file = await data.arrayBuffer();
    } catch (e) {
        console.log(e);
    }

    const workbook = XLSX.read(file);
    const sheet = workbook.Sheets[workbook.SheetNames[0]];
    const jsonSheet = XLSX.utils.sheet_to_json(sheet);

    return jsonSheet;
}
