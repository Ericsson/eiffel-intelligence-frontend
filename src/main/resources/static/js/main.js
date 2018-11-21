jQuery(document).ready(function() {
    (function($) {
        $.fn.invisible = function() {
            return this.each(function() {
                $(this).css("visibility", "hidden");
            });
        };
        $.fn.visible = function() {
            return this.each(function() {
                $(this).css("visibility", "visible");
            });
        };
    }(jQuery));
    var router = new Navigo(null, true, '#');
    // Fetch injected URL from DOM
    var eiffelDocumentationUrlLinks = $('#eiffelDocumentationUrlLinks').text();
    var frontendServiceUrl = $('#frontendServiceUrl').text();
    var frontendServiceBackEndPath = "/backend";

    $("#subscriptionBtn").click(function() {
        updateBackEndInstanceList();
        $("#navbarResponsive").removeClass("show");
        $("#selectInstances").visible();
        router.navigate('subscriptions');
    });

    $("#testRulesBtn").click(function() {
        updateBackEndInstanceList();
        $("#navbarResponsive").removeClass("show");
        $("#selectInstances").visible();
        router.navigate('test-rules');
    });

    $("#eiInfoBtn").click(function() {
        updateBackEndInstanceList();
        $("#navbarResponsive").removeClass("show");
        $("#selectInstances").visible();
        router.navigate('ei-info');
    });

    $("#loginBtn").click(function() {
        $("#navbarResponsive").removeClass("show");
        $("#selectInstances").visible();
        router.navigate('login');
    });

    $("#addInstanceBtn").click(function() {
        $("#navbarResponsive").removeClass("show");
        $("#selectInstances").invisible();
        router.navigate('add-backend');
    });

    $("#switcherBtn").click(function() {
        $("#navbarResponsive").removeClass("show");
        $("#selectInstances").invisible();
        router.navigate('switch-backend');
    });

    $("#logoutBtn").click(function() {
        $("#navbarResponsive").removeClass("show");
        $("#selectInstances").visible();
        $.ajax({
            url : frontendServiceUrl + "/auth/logout",
            type : "GET",
            contentType : 'application/json; charset=utf-8',
            cache: false,
            complete : function (XMLHttpRequest, textStatus) {
                doIfUserLoggedOut();
                loadMainPage();
            }
        });
    });

    function doIfUserLoggedOut() {
        localStorage.removeItem("currentUser");
        $("#ldapUserName").text("Guest");
        $("#loginBlock").show();
        $("#logoutBlock").hide();
        localStorage.setItem('errorsStore', []);
    }

    function loadDocumentLinks(){
        // eiffelDocumentationUrlLinks variable is configure in application.properties
        var linksList = JSON.parse(eiffelDocumentationUrlLinks);
        var docLinksDoc = document.getElementById('collapseDocPages');
        var liTag = null;
        var aTag = null;

        Object.keys(linksList).forEach(function(linkKey) {
            liTag = document.createElement('li');
            aTag = document.createElement('a');
            aTag.innerHTML = linkKey;
            aTag.setAttribute('href', linksList[linkKey]);
            aTag.setAttribute('target', '_blanc');
            liTag.appendChild(aTag);
            docLinksDoc.appendChild(liTag);
        });
    }

    function init() {
        updateBackEndInstanceList();
        $("#navbarResponsive").removeClass("show");
        $("#selectInstances").visible();
        loadDocumentLinks();
    }

    init();

    function singleInstanceModel(name, host, port, path, https, active) {
        this.name = ko.observable(name),
        this.host = ko.observable(host),
        this.port = ko.observable(port),
        this.path = ko.observable(path),
        this.https = ko.observable(https),
        this.active = ko.observable(active),
        this.information = name.toUpperCase() + " - " + host + " " + port + "/" + path;
    }

    function viewModel(data) {
        var self = this;
        var currentName;
        self.instances = ko.observableArray();
        var json = JSON.parse(ko.toJSON(data));
        var oldSelectedActive = self.selectedActive;
        for(var i = 0; i < json.length; i++) {
            var obj = json[i];
            var instance = new singleInstanceModel(obj.name, obj.host, obj.port, obj.path, obj.https, obj.active);
            self.instances.push(instance);
            if(obj.active == true){
                currentName = obj.name;
            }
        }
        self.selectedActive = ko.observable(currentName);
        self.onChange = function(){
            if(typeof self.selectedActive() !== "undefined"){
                $.ajax({
                    url: frontendServiceUrl + frontendServiceBackEndPath,
                    type: "PUT",
                    data: self.selectedActive(),
                    contentType: 'application/json; charset=utf-8',
                    cache: false,
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        self.selectedActive = oldSelectedActive;
                        updateBackEndInstanceList();
                        window.logMessages(XMLHttpRequest.responseText);
                    },
                    success: function (responseData, XMLHttpRequest, textStatus) {
                        console.log("Response from IE front end back end: " + responseData.message);
                        location.reload();
                    }
                });
            } else {
                $.jGrowl("Please chose backend instance", {sticky: false, theme: 'Error'});
              }
        }
    }

    function updateBackEndInstanceList() {
        $.ajax({
            url: frontendServiceUrl + frontendServiceBackEndPath,
            type: "GET",
            contentType: 'application/json; charset=utf-8',
            cache: false,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                window.logMessages("Failure when trying to load backend instances");
            },
            success: function (responseData, XMLHttpRequest, textStatus) {
                var observableObject = $("#selectInstances")[0];
                ko.cleanNode(observableObject);
                ko.applyBindings(new viewModel(responseData),observableObject);
            }
        });
    }

    $('body').on('click', function (e) {
        if ($(e.target).data('toggle') !== 'tooltip' && $(e.target)[0].className !== 'tooltip-inner') {
            $('.tooltip').tooltip('hide');
        }
    });
});
