getOrders();

function getOrders() {
    fetch('/den-travak/orders').then(response => response.json())
        .then(data => {
            //console.log(data);
            filterOrders(data);
        })
}

function filterOrders(data) {

    var table = document.getElementById("orders");

    var today = new Date();
    today.setHours(1, 0, 0);
    var tomorrow = new Date();
    tomorrow.setHours(25, 0, 0);
    var filteredData = data.filter(function(product) {
        var date = new Date(product.creationDate);
        return (date >= today && date <= tomorrow);
    });
	var start = 0 + table.childNodes.length;
    for (i = start; i < filteredData.length; i++) {
        var name = filteredData[i].name + " (" + filteredData[i].breadType + ")";
        var price = filteredData[i].price;
        var number = filteredData[i].mobilePhoneNumber;

        var tableRow = document.createElement("tr");
        var nameCell = document.createElement("td");
        nameCell.innerHTML = name;
        tableRow.appendChild(nameCell);
        var priceCell = document.createElement("td");
        priceCell.innerHTML = price;
        tableRow.appendChild(priceCell);
        var numberCell = document.createElement("td");
        numberCell.innerHTML = number;
        tableRow.appendChild(numberCell);
        table.appendChild(tableRow);
    }
}

function getCsv() {
	getOrders();
    fetch('/den-travak/orders').then(response => response.json())
        .then(data => {
            generateCsv(data);
        })
}

function generateCsv(data) {
    if (!document.getElementById("csvheader")) {
        var tableheader = document.getElementById("tableheaderrow");
        var csvHeader = document.createElement("th");
        csvHeader.innerHTML = "Printed?";
        csvHeader.id = "csvheader";
        tableheader.appendChild(csvHeader);
    }
    var table = document.getElementById("orders");
    for (i = 0; i < table.childNodes.length; i++) {
        if (table.childNodes[i].childNodes.length == 3) {
            var printedCell = document.createElement("td");
            printedCell.innerHTML = "true";
            table.childNodes[i].appendChild(printedCell);
        }
    };
    var replacer = (key, value) => value === null ? '' : value;
    var header = Object.keys(data[0]);
    let csv = data.map(row => header.map(fieldName => JSON.stringify(row[fieldName], replacer)).join(','));
    csv.unshift(header.join(','));
    csv = csv.join('\r\n');
    window.open('data:text/csv;charset=utf-8,' + escape(csv));
}
