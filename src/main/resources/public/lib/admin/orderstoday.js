console.log("test");
getOrders();
//setTimeout(function() {
//    getOrders()
//}, 3000);

function getOrders() {
    fetch('/den-travak/orders').then(response => response.json())
        .then(data => {
            
            console.log(data);
            filterOrders(data);

        })
}

function filterOrders(data) {

    var table = document.getElementById("orders");
    while (table.hasChildNodes()) {
        table.removeChild(table.lastChild);
    }

    var today = new Date();
    today.setHours(1, 0, 0);
    var tomorrow = new Date();
    tomorrow.setHours(25, 0, 0);
    var filteredData = data.filter(function(product) {
        var date = new Date(product.creationDate);
        return (date >= today && date <= tomorrow);
    });
    console.log(filteredData);

    for (i = 0; i < filteredData.length; i++) {
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

function download_csv() {
    var csv = 'Name,Title\n';
    data.forEach(function(row) {
            csv += row.join(',');
            csv += "\n";
    });
 
    console.log(csv);
    var hiddenElement = document.createElement('a');
    hiddenElement.href = 'data:text/csv;charset=utf-8,' + encodeURI(csv);
    hiddenElement.target = '_blank';
    hiddenElement.download = 'people.csv';
    hiddenElement.click();
}

function getCsv() {
    fetch('den-travak/orders').then(response => response.json())
        .then(data => {
            console.log("dcsv");
            //console.log(data)
            generateCsv(data);

        })
}

function generateCsv(data){
	var replacer = (key, value) => value === null ? '' : value;
	var header = Object.keys(data[0]);
	let csv = data.map(row => header.map(fieldName => JSON.stringify(row[fieldName], replacer)).join(','));
	csv.unshift(header.join(','));
	csv = csv.join('\r\n');
	window.open('data:text/csv;charset=utf-8,' + escape(csv));
}
