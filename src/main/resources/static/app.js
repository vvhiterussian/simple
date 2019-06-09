var stompClient = null;

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

        stompClient.subscribe('/sdef', function (response) {
            showSecurityDefinitions(JSON.parse(response.body).isins);
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

function sendSecurityDefinitionsResponse() {
    stompClient.send("/app/sdef", {});
}

function showAuthResponse(message) {
    $("#userinfo").append("<tr><td>" + message + "</td></tr>");
}

function showSecurityDefinitions(message) {
    $("#userinfo").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendAuth(); });
    $( "#get-securities" ).click(function() { sendSecurityDefinitionsResponse(); });
});