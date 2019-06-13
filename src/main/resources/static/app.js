var stompClient = null;
var secDefSubscription = null;

var subscriptions = {};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#userinfo").html("");
}

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/auth', function (response) {
            showAuthResponse(JSON.parse(response.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendAuth() {
    stompClient.send("/app/auth", {}, JSON.stringify({'name': $("#name").val(), 'token': $("#token").val()}));
}

function subscribeOnSecurityDefinitions() {
    secDefSubscription = stompClient.subscribe('/sdef', function (response) {
        showSecurityDefinitions(JSON.parse(response.body).isins);
    });

    stompClient.subscribe('/user/sdef', function (response) {
        showSecurityDefinitionsPrivate(JSON.parse(response.body).isins);
    });

    stompClient.send("/app/sdef", {});
}

function unsubscribeOnSecurityDefinitions() {
    secDefSubscription.unsubscribe();
}

function showAuthResponse(message) {
    $("#userinfo").append("<tr><td>" + message + "</td></tr>");
}

function showSecurityDefinitions(message) {
    $("#userinfo").append("<tr><td>" + message + "</td></tr>");
}

function showSecurityDefinitionsPrivate(message) {
    $("#userinfo").append("<tr><td>Private: " + message + "</td></tr>");
}



function subscribeOnMarketData(idSource, securityId, exDestination){
    var subscriptionKey = '/market-data/' + idSource + '/' + securityId + '/' + exDestination;
    subscriptions[subscriptionKey] = stompClient.subscribe(subscriptionKey, function (response) {
        showMarketDataUpdate(subscriptionKey, response.body);
    });
}

function unsubscribeFromMarketData(idSource, securityId, exDestination){
    var subscriptionKey = '/market-data/' + idSource + '/' + securityId + '/' + exDestination;
    if (subscriptions.hasOwnProperty(subscriptionKey)) {
        subscriptions[subscriptionKey].unsubscribe();
    }
}

function showMarketDataUpdate(source, message) {
    $("#userinfo").append("<tr><td>A message from: " + source + ": " + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendAuth(); });
    $( "#sub-sdef" ).click(function() { subscribeOnMarketData(); });
    $( "#unsub-sdef" ).click(function() { unsubscribeOnSecurityDefinitions(); });

    $( "#sub" ).click(function() { subscribeOnMarketData($("#id-source").val(), $("#security-id").val(), $("#ex-destination").val()); });
    $( "#unsub" ).click(function() { unsubscribeFromMarketData($("#id-source").val(), $("#security-id").val(), $("#ex-destination").val()); });
});