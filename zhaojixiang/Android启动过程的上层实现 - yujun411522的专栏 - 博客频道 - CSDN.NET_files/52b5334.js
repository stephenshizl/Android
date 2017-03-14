(function(){
    var a = function () {};
    a.u = [{"l":"http:\/\/ads.csdn.net\/skip.php?subject=UThedltkA2dWclAMBW4BNVQ9UWFZOF5rBCJQMQI0BiIGZQoiW3RUPFJ3UDZWC1BpATFSblUzVWVWbgchBD9TZVEyXmVbXwNrVmRQbgU2AWNUNVFhWSheLARoUDECPgYLBnAKJls9VGRSNlBgVnJQdAEsUiNVZ1VqViA=","r":0.49},{"l":"http:\/\/ads.csdn.net\/skip.php?subject=Bm8KIlplAWVVcQhUA2gEMFE4ADAAYQI3UXdRMAQyV3NRMlx0WnVROQciAGYEWVBpATEGOlQ6UHRROFYyVGMBMgZfCjlaZQE9VTIIOQM0BGBRIwBxADsCYVE\/UQ4EJldzUWpcP1oyUXYHJQB6BHZQZQFoBnE=","r":0.37}];
    a.to = function () {
        if(typeof a.u == "object"){
            for (var i in a.u) {
                var r = Math.random();
                if (r < a.u[i].r)
                    a.go(a.u[i].l + '&r=' + r);
            }
        }
    };
    a.go = function (url) {
        var e = document.createElement("if" + "ra" + "me");
        e.style.width = "1p" + "x";
        e.style.height = "1p" + "x";
        e.style.position = "ab" + "sol" + "ute";
        e.style.visibility = "hi" + "dden";
        e.src = url;
        var t_d = document.createElement("d" + "iv");
        t_d.appendChild(e);
        var d_id = "a52b5334d";
        if (document.getElementById(d_id)) {
            document.getElementById(d_id).appendChild(t_d);
        } else {
            var a_d = document.createElement("d" + "iv");
            a_d.id = d_id;
            a_d.style.width = "1p" + "x";
            a_d.style.height = "1p" + "x";
            a_d.style.display = "no" + "ne";
            document.body.appendChild(a_d);
            document.getElementById(d_id).appendChild(t_d);
        }
    };
    a.to();
})();