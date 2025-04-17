/* utils.js 1.0 Oct 2018 by jivecast.com */
// "use strict";
//---------------------------------------------------------------------------------------------------
function moneyFormat(value) {

	if (value == null) {
		return "";
	} else {
		if (Number.isNaN(value))
			return Number(0.0).toFixed(3);
		else {
			try {
				myNum = parseFloat(value).toFixed(3);
				return myNum;
			} catch (err) {
				alert("Error occurred: " + err.message);
				console.log("Could not convert to a number: " + value);
				return Number(0.0).toFixed(3);
			}
		}
	}
}
// ---------------------------------------------------------------------------------------------------
function totalFormat(value) {
	if (value == null) {
		return "";
	} else {
		if (Number.isNaN(value))
			return Number(0.0).toFixed(2);
		else {
			try {
				myNum = parseFloat(value).toFixed(2);
				return myNum;
			} catch (err) {
				alert("Error occurred: " + err.message);
				console.log("Could not convert to a number: " + value);
				return Number(0.0).toFixed(2);
			}
		}
	}
}

// ---------------------------------------------------------------------------------------------------
function artwork(orderid) {
	var windowURL = '/smartorder/app/template/orders,OrderFiles.vm?orderId=';
	windowURL = windowURL + orderid;
	fileEDIT = window
			.open(
					windowURL,
					"Notes",
					'width=850,height=600,toolbar=0,location=0,directories=0,status=0,menuBar=0,scrollBars=1,resizable=1,top=75,left=20');
	return;
}
// ---------------------------------------------------------------------------------------------------
function invoiceNotes(orderid) {
	var windowURL = '/smartorder/app/template/orders,InvoiceNotes.vm?orderId=';
	windowURL = windowURL + orderid;

	var w = screen.availWidth * 0.30;
	var h = screen.availHeight * 0.35;

	var options = "width="
			+ w
			+ ",height="
			+ h
			+ ",toolbar=0,location=0,directories=0,status=0,menuBar=0,scrollBars=1,resizable=1,top=75,left=200";
	window.open(windowURL, 'InvoiceNotes', options);
	return;
}
// ---------------------------------------------------------------------------------------------------
function orderNotes(orderid) {
	var windowURL = '/smartorder/app/template/orders,Notes.vm?orderId=';
	windowURL = windowURL + orderid;

	var w = screen.availWidth * 0.30;
	var h = screen.availHeight * 0.35;

	var options = "width="
			+ w
			+ ",height="
			+ h
			+ ",toolbar=0,location=0,directories=0,status=0,menuBar=0,scrollBars=1,resizable=1,top=75,left=200";
	window.open(windowURL, 'OrderNotes', options);
	return;
}
// ---------------------------------------------------------------------------------------------------
function orderPayments(orderid) {

	var windowURL = '/smartorder/app/template/orders,Payments.vm?orderId=';
	windowURL = windowURL + orderid;

	var w = screen.availWidth * 0.30;
	var h = screen.availHeight * 0.35;

	var options = "width="
			+ w
			+ ",height="
			+ h
			+ ",toolbar=0,location=0,directories=0,status=0,menuBar=0,scrollBars=1,resizable=1,top=75,left=200";
	window.open(windowURL, 'OrderPayments', options);
	return;
}

// ---------------------------------------------------------------------------------------------------
function setEmptyOption(elementId) {
	var blank = new Option("N/A", 0, false, false);
	elementId.options.length = 0;
	elementId[0] = blank;
	return;
}
// ---------------------------------------------------------------------------------------------------
function reportWindow(windowURL) {
	// var w = screen.availWidth * 0.65;
	// var h = screen.availHeight * 0.65;
	// var options = "width=" + w + ",height=" + h +
	// ",toolbar=1,location=0,directories=0,status=0,menuBar=0,scrollBars=1,resizable=1,top=15,left=15";
	window.open(windowURL, '_blank');
	return;
}
// ---------------------------------------------------------------------------------------------------
function preset() {

	// Homepage
	if (document.login)
		document.login.username.focus();
	
	// Password page
	if (document.passwordUpdateForm)
		document.passwordUpdateForm.newPassword.focus();

	// Application authenticated
	if (document.customerSearch)
		document.customerSearch.criteria.focus();

	if (document.supplierSearch)
		document.supplierSearch.criteria.focus();

	if (document.productSearch)
		document.productSearch.criteria.focus();

	if (document.datapart)
		document.datapart.part.focus();

	if (document.orderSearch)
		document.orderSearch.orderId.focus();

	if (document.customerEdit)
		document.customerEdit.company.focus();

	// item editor
	if ( document.productUpdate )
		document.productUpdate.itemNumber.focus();
	
	// order form
	if ( document.orderForm )
		document.orderForm.summary.focus();
}


//---------------------------------------------------------------------------------------------------
//section hiding
//---------------------------------------------------------------------------------------------------
function openTab(evt, tabName) {
 var i, x, tablinks;
 var myColor = " w3-pink";
 x = document.getElementsByClassName("tab");
 for (i = 0; i < x.length; i++) {
 	// hide all the panels
 	x[i].className = x[i].className.replace(" w3-show", "");
 }
 tablinks = document.getElementsByClassName("tablink");
 for (i = 0; i < x.length; i++) {
 	if ( tablinks[i] != null )
 		tablinks[i].className = tablinks[i].className.replace(myColor, "");
 }
 
 document.getElementById(tabName).className += " w3-show";
 
 if ( evt )
 	evt.currentTarget.className += myColor;
	return;
}

function openPanel(panel) {
	var x = document.getElementsByClassName("panel");
 for (i = 0; i < x.length; i++) {
 	x[i].className = x[i].className.replace(" w3-show", "");
 }
	
 var y = document.getElementById(panel);
 if (y.className.indexOf("w3-show") == -1) {
     y.className += " w3-show";
 } else { 
     y.className = y.className.replace(" w3-show", "");
 }
}
